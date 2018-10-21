package avl

class AVLVerifier(startDigest: Digest) {

    private var curDigest = startDigest

    fun verifySearch(key: Int, value: LeafData, nextKey: LeafNode?, proof: Proof): Boolean {
        var curHash = hashLeafNode(key, value, nextKey)
        val size = proof.entries.size
        for (i in 0 until size) {
            val treeNodeHash = proof.entries[i]
            val curDirection = proof.directions[i]
            curHash = if (curDirection == Direction.LEFT) {
                hashTreeNode(curHash, treeNodeHash)
            } else {
                hashTreeNode(treeNodeHash, curHash)
            }
        }
        return curHash.data contentEquals curDigest.data
    }

    fun verifyChange(key: Int, value: LeafData, nextKey: LeafNode?, proof: Proof, newDigest: Digest): Boolean {
        var curHash = hashLeafNode(key, value, nextKey)
        val size = proof.heights.size

        val heights = proof.heights
        val directions = proof.directions
        val proofElems = proof.entries

        /*
        Массивы отражают путь от 0 (листа) к корню (size), а надо наоборот
         */
        heights.reverse()
        directions.reverse()
        proofElems.reverse()
        /*
        Всего на пути size + 1 вершин с номерами с 0 по size включительно
        Вершина с i-ым номером - это вершина с расстоянием до корня, равным i.
        Нулевая вершина - корень. size-ая вершина - лист, от которого мы поднимаемся.

        directions[i] - направление поднятия от (i + 1)-ой верншины к i-ой.
        proofElement[i] - хеш брата (i + 1)-ой вершины
        heights[i] - высоты левого и правого поддеревьев i-ой вершины на пути
        (то есть  высоты поддеревьев (i + 1)-ой вершины и её брата)
         */

        for (curIndex in size downTo 1) {
            var smallRotationPerformed = false
            var bigRotationPerformed = false
            if (curIndex >= 2) {
                /*
                b - (i - 1)-ая вершина
                a - (i - 2)-ая вершина
                 */
                val bIndex = curIndex - 1
                val aIndex = curIndex - 2

                val aLeftHeight = heights[aIndex].leftHeight
                val aRightHeight = heights[aIndex].rightHeight

                val bLeftHeight = heights[bIndex].leftHeight
                val bRightHeight = heights[bIndex].rightHeight

                val aBalance = aLeftHeight - aRightHeight
                val bBalance = bLeftHeight - bRightHeight

                if (aBalance == -2 && (bBalance == -1 || bBalance == 0)) {
                    /*
                    Малый левый поворот
                     */
                    smallRotationPerformed = true
                    if (directions[curIndex - 1] == Direction.RIGHT) {
                        /*
                        Идём по направлению R -> b -> a
                         */
                        val P = proofElems[curIndex - 2]
                        val Q = proofElems[curIndex - 1]
                        // R = curHash, не меняется

                        if (directions[curIndex - 2] != Direction.RIGHT) {
                            return false
                        }

                        proofElems[curIndex - 2] = hashTreeNode(P, Q)
                    } else {
                        /*
                        Идём по направлению Q -> b -> a
                         */
                        val P = proofElems[curIndex - 2]
                        val Q = curHash
                        val R = proofElems[curIndex - 1]

                        if (directions[curIndex - 2] != Direction.RIGHT) {
                            return false
                        }

                        proofElems[curIndex - 2] = R
                        directions[curIndex - 2] = Direction.LEFT
                        curHash = hashTreeNode(P, Q)
                    }

                    /*
                    Пересчёт высоты
                     */
                    val pHeight = aLeftHeight
                    val qHeight = bLeftHeight
                    val rHeight = bRightHeight
                    val newAHeight = maxOf(pHeight, qHeight) + 1
                    // высота b'
                    heights[curIndex - 2] = NodeHeightInfo(newAHeight, rHeight)
                } else if (aBalance == 2 && (bBalance == -1 || bBalance == 0)) {
                    /*
                    Малый правый поворот
                     */
                    smallRotationPerformed = true
                    if (directions[curIndex - 1] == Direction.LEFT) {
                        /*
                        Идём по пути P -> b -> a
                         */
                        if (directions[curIndex - 2] != Direction.LEFT) {
                            return false
                        }
                        // P = curHash, не меняется
                        val Q = proofElems[curIndex - 1]
                        val R = proofElems[curIndex - 2]

                        proofElems[curIndex - 2] = hashTreeNode(Q, R)
                    } else {
                        /*
                        Идём по пути Q -> b -> a
                         */
                        if (directions[curIndex - 2] != Direction.LEFT) {
                            return false
                        }
                        directions[curIndex - 2] = Direction.RIGHT

                        val P = proofElems[curIndex - 1]
                        val Q = curHash
                        val R = proofElems[curIndex - 2]

                        curHash = hashTreeNode(Q, R)
                        proofElems[curIndex - 2] = P
                    }

                    /*
                    Пересчёт высоты
                     */

                    val pHeight = bLeftHeight
                    val qHeight = bRightHeight
                    val rHeight = aRightHeight
                    val newAHeight = maxOf(qHeight, rHeight) + 1
                    // высота b'
                    heights[curIndex - 2] = NodeHeightInfo(pHeight, newAHeight)
                }
            }
            if (curIndex >= 3 && !smallRotationPerformed) {
                /*
                Малое вращение не производилось, можно провести большое
                 */
                val aIndex = curIndex - 3
                val bIndex = curIndex - 2
                val cIndex = curIndex - 1

                val aLeftHeight = heights[aIndex].leftHeight
                val aRightHeight = heights[aIndex].rightHeight

                val bLeftHeight = heights[bIndex].leftHeight
                val bRightHeight = heights[bIndex].rightHeight

                val cLeftHeight = heights[cIndex].leftHeight
                val cRightHeight = heights[cIndex].rightHeight

                val aBalance = aLeftHeight - aRightHeight
                val bBalance = bLeftHeight - bRightHeight
                val cBalance = cLeftHeight - cRightHeight

                if (aBalance == -2 && bBalance == 1 && (cBalance == -1 || cBalance == 0 || cBalance == 1)) {
                    /*
                    Большой левый поворот
                     */
                    bigRotationPerformed = true
                    if (directions[curIndex - 1] == Direction.LEFT) {
                        /*
                        Идём по пути Q -> c -> b -> a
                         */
                        if (directions[curIndex - 2] != Direction.LEFT) {
                            return false
                        }
                        if (directions[curIndex - 3] != Direction.RIGHT) {
                            return false
                        }

                        val P = proofElems[curIndex - 3]
                        // Q = curHash, не меняется
                        val R = proofElems[curIndex - 1]
                        val S = proofElems[curIndex - 2]

                        directions[curIndex - 2] = Direction.RIGHT
                        directions[curIndex - 3] = Direction.LEFT

                        proofElems[curIndex - 2] = P
                        proofElems[curIndex - 3] = hashTreeNode(R, S)

                        /*
                        Пересчитаем высоту
                        */

                        val pHeight = aLeftHeight
                        val qHeight = cLeftHeight
                        val rHeight = cRightHeight
                        val sHeight = bRightHeight
                        val newAHeight = maxOf(pHeight, qHeight) + 1
                        val newBHeight = maxOf(rHeight, sHeight) + 1
                        // (i - 2)-ая вершина это a'
                        heights[curIndex - 2] = NodeHeightInfo(pHeight, qHeight)
                        // (i - 3)-я вершина это c'
                        heights[curIndex - 3] = NodeHeightInfo(newAHeight, newBHeight)
                    } else {
                        /*
                        Идём по пути R -> c -> b -> a
                         */
                        if (directions[curIndex - 2] != Direction.LEFT) {
                            return false
                        }
                        if (directions[curIndex - 3] != Direction.RIGHT) {
                            return false
                        }

                        val P = proofElems[curIndex - 3]
                        val Q = proofElems[curIndex - 1]
                        // R = curHash, не меняется
                        val S = proofElems[curIndex - 2]

                        proofElems[curIndex - 2] = S
                        proofElems[curIndex - 3] = hashTreeNode(P, Q)

                        val pHeight = aLeftHeight
                        val qHeight = cLeftHeight
                        val rHeight = cRightHeight
                        val sHeight = bRightHeight
                        val newAHeight = maxOf(pHeight, qHeight) + 1
                        val newBHeight = maxOf(rHeight, sHeight) + 1
                        // (i - 2)-ая вершина это b'
                        heights[curIndex - 2] = NodeHeightInfo(rHeight, sHeight)
                        // (i - 3)-я вершина это c'
                        heights[curIndex - 3] = NodeHeightInfo(newAHeight, newBHeight)
                    }

                } else if (aBalance == 2 && bBalance == 1 &&
                        (cBalance == -1 || cBalance == 0 || cBalance == 1)) {
                    bigRotationPerformed = true
                    /*
                    Большой правый поворот
                     */
                    if (directions[curIndex - 1] == Direction.LEFT) {
                        /*
                        Идём по пути Q -> c -> b -> a
                         */
                        if (directions[curIndex - 2] != Direction.RIGHT) {
                            return false
                        }
                        if (directions[curIndex - 3] != Direction.LEFT) {
                            return false
                        }

                        val P = proofElems[curIndex - 2]
                        // Q = curHash, не меняется
                        val R = proofElems[curIndex - 1]
                        val S = proofElems[curIndex - 3]

                        proofElems[curIndex - 2] = P
                        proofElems[curIndex - 3] = hashTreeNode(R, S)

                        /*
                        Пересчитаем высоту
                         */

                        val pHeight = bLeftHeight
                        val qHeight = cLeftHeight
                        val rHeight = cRightHeight
                        val sHeight = aRightHeight
                        val newAHeight = maxOf(pHeight, qHeight) + 1
                        val newBHeight = maxOf(rHeight, sHeight) + 1
                        // (i - 2)-ая вершина это a'
                        heights[curIndex - 2] = NodeHeightInfo(pHeight, qHeight)
                        // (i - 3)-я вершина это c'
                        heights[curIndex - 3] = NodeHeightInfo(newAHeight, newBHeight)
                    } else {
                        /*
                        Идём по пути R -> c -> b -> a
                         */
                        if (directions[curIndex - 2] != Direction.RIGHT) {
                            return false
                        }
                        if (directions[curIndex - 1] != Direction.LEFT) {
                            return false
                        }

                        directions[curIndex - 2] = Direction.LEFT
                        directions[curIndex - 3] = Direction.RIGHT

                        val P = proofElems[curIndex - 2]
                        val Q = proofElems[curIndex - 1]
                        // R = curHash, не меняется
                        val S = proofElems[curIndex - 3]

                        proofElems[curIndex - 2] = S
                        proofElems[curIndex - 3] = hashTreeNode(P, Q)

                        /*
                        Пересчёт высоты
                         */

                        val pHeight = bLeftHeight
                        val qHeight = cLeftHeight
                        val rHeight = cRightHeight
                        val sHeight = aRightHeight
                        val newAHeight = maxOf(pHeight, qHeight) + 1
                        val newBHeight = maxOf(rHeight, sHeight) + 1
                        // (i - 2)-ая вершина это b'
                        heights[curIndex - 2] = NodeHeightInfo(rHeight, sHeight)
                        // (i - 3)-я чершина это c'
                        heights[curIndex - 3] = NodeHeightInfo(newAHeight, newBHeight)
                    }
                }
            }
            if (!smallRotationPerformed && !bigRotationPerformed) {
                /*
                Повороты не производились
                Пересчитаем хеш и высоту
                 */
                val curHeight = if (curIndex == size) 0 else
                    maxOf(heights[curIndex].leftHeight, heights[curIndex].rightHeight) + 1
                if (directions[curIndex - 1] == Direction.LEFT) {
                    curHash = hashTreeNode(curHash, proofElems[curIndex - 1])
                    val neighbourHeight = heights[curIndex - 1].rightHeight
                    heights[curIndex - 1] = NodeHeightInfo(curHeight, neighbourHeight)
                } else {
                    curHash = hashTreeNode(proofElems[curIndex - 1], curHash)
                    val neighbourHeight = heights[curIndex - 1].leftHeight
                    heights[curIndex - 1] = NodeHeightInfo(neighbourHeight, curHeight)
                }
            }
        }


        val result = curHash.data contentEquals newDigest.data
        if (result) {
            curDigest = newDigest
        }
        return result
    }
}
