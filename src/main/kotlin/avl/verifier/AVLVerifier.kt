package avl.verifier

import avl.*

class AVLVerifier(startDigest: Digest) {

    private var curDigest = startDigest

    fun proveSearch(key: Int, value: LeafData, nextKey: Int?, proof: Proof): Boolean {
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

    fun proveInsertion(key: Int, value: LeafData, nextKey: Int?, proof: Proof, newDigest: Digest): Boolean {
        var curHash = hashLeafNode(key, value, nextKey)
        val size = proof.heights.size
        val heights = proof.heights
        val directions = proof.directions
        val proofElems = proof.entries

        for (i in 0 until size) {
            var smallRotationDone = false
            if (i < size - 1) {
                /*
                a - (i + 1)-ая вершина
                b - i-ая вершина
                 */

                val bLeftHeight = heights[i].leftHeight
                val bRightHeight = heights[i].rightHeight

                val aLeftHeight = heights[i + 1].leftHeight
                val aRightHeight = heights[i + 1].rightHeight

                val bBalance = bLeftHeight - bRightHeight
                val aBalance = aLeftHeight - aRightHeight

                val curDirection = directions[i]

                if (aBalance == -2 && (bBalance == -1 || bBalance == 0)) {
                    /*
                    P = a.left
                    Q = b.left
                    R = b.right
                    b = a.right
                    Малый левый поворот
                    */
                    smallRotationDone = true
                    if (curDirection == Direction.RIGHT) {
                        /*
                        Мы поднимаемся по пути R -> b -> a
                        curHash не меняется (это хеш R)
                        proof[i + 1] - хеш брата R (раньше это был хеш брата b, которым был P)
                        Теперь брат R - a'
                        a' = TreeNode(P, Q)
                        direction[i + 1] не поменяется, так как
                        direction(R -> b') == direction(b -> a) == Direction.RIGHT
                         */
                        require(directions[i + 1] == Direction.RIGHT)
                        val Q = proofElems[i]
                        val P = proofElems[i + 1]
                        proofElems[i + 1] = hashTreeNode(P, Q)
                    } else {
                        /*
                        Мы поднимаемся по пути Q -> b -> a
                        curHash меняется, теперь это хеш a'
                        a' = TreeNode(P, Q)
                        direction[i + 1] меняется на противоположный
                        direction(b -> a) был RIGHT, direction(a' -> b') стал LEFT
                        proof[i + 1] меняется, тк раньше это был хеш брата b (это P),
                        теперь это хеш брата a' (это R)
                         */
                        require(directions[i + 1] == Direction.RIGHT)
                        directions[i + 1] = Direction.LEFT
                        val Q = curHash
                        val P = proofElems[i + 1]
                        val R = proofElems[i]
                        curHash = hashTreeNode(P, Q)
                        proofElems[i + 1] = R
                    }
                    /*
                    Пересчитаем heights[i + 1] = heights(b')
                    Теперь height(b'_l) = height(a') = max(height(P, Q) + 1)
                    height(b'_r) = height(R)
                    height(R) = height(b.right)
                    height(P) = height(a.left)
                    height(Q) = height(b.left)
                     */
                    val heightR = heights[i].rightHeight
                    val heightP = heights[i + 1].leftHeight
                    val heightQ = heights[i].leftHeight
                    heights[i + 1] = NodeHeightInfo(
                            maxOf(heightP, heightQ) + 1,
                            heightR
                    )
                } else if (aBalance == 2 && (bBalance == -1 || bBalance == 0)) {
                    /*
                    P = b.left
                    Q = b.right
                    R = a.right
                    b = a.left
                    Малый правый повторот
                     */
                    smallRotationDone = true
                    if (curDirection == Direction.RIGHT) {
                        /*
                        Поднимаемся по пути Q -> b -> a
                        curHash меняется, теперь это хеш a'
                        a' = TreeNode(Q, R)
                        direction[i + 1] поменялся на противоположное значение,
                        так как direction(b -> a) был равен LEFT,
                        но direction(a' -> b') стал RIGHT
                        proofElems[i + 1] меняется, раньше это был брат b (это был R),
                        теперь это брат a' (это P)
                         */
                        require(directions[i + 1] == Direction.LEFT)
                        directions[i + 1] == Direction.RIGHT
                        val P = proofElems[i]
                        val Q = curHash
                        val R = proofElems[i + 1]
                        curHash = hashTreeNode(Q, R)
                        proofElems[i + 1] = P
                    } else {
                        /*
                        Поднимаемся по пути P -> b -> a
                        curHash не меняется, это остаётся P
                        direction[i + 1] не меняется, так как
                        direction(b -> a) == direction(P -> b') == LEFT
                        proofElems[i + 1] меняется. Раньше это был брат b (это был R),
                        теперь это брат P (это a')
                        a' = TreeNode(Q, R)
                         */
                        require(directions[i + 1] == Direction.LEFT)
                        val Q = proofElems[i]
                        val R = proofElems[i + 1]
                        proofElems[i + 1] == hashTreeNode(Q, R)
                    }
                    /*
                    Пересчитаем heights[i + 1] == height(b')
                    height(b'.l) == height(P)
                    height(b'.r) = height(a') = max(height(Q), height(R)) + 1
                    height(P) = height(b.left)
                    height(Q) = height(b.right)
                    height(R) = height(a.right)
                     */
                    val heightP = heights[i].leftHeight
                    val heightQ = heights[i].rightHeight
                    val heightR = heights[i + 1].rightHeight
                    heights[i + 1] = NodeHeightInfo(
                            heightP,
                            maxOf(heightQ, heightR) + 1
                    )
                }
            }
            if (i < size - 2 && !smallRotationDone) {
                // Малый повопрот ещё не совершался, можно попробовать сделать большой
                // TODO - большие повороты
            }
        }
        val result = curHash.data contentEquals newDigest.data
        if (result) {
            curDigest = newDigest
        }
        return result
    }

}
