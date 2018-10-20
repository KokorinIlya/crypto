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

    fun proveInsertion(key: Int, value: LeafData, nextKey: Int?, proof: Proof): Boolean {
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
                    smallRotationDone = true
                    if (curDirection == Direction.RIGHT) {
                        /*
                        Мы поднимаемся по пути R -> b -> a
                        curHash не меняется (это хеш R)
                        proof[i + 1] - хеш брата R (раньше это был хеш брата b, которым был P)
                        Теперь брат R - a'
                        a' = TreeNode(P, Q)
                        direction[i + 1] не поменяется, так как
                        direction(R -> b) == direction(b -> a) == Direction.RIGHT
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
                        direction(b -> a) был RIGHT, станет LEFT
                        proof[i + 1] не меняется (раньше это был хеш брата b, теперь -
                        хеш брата a'. В обоих случаях это R)
                         */
                        require(directions[i + 1] == Direction.RIGHT)
                        directions[i + 1] = Direction.LEFT
                    }
                    /*
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
                    smallRotationDone = true
                    // TODO: правый малый поворот
                }
            }
        }
    }

}
