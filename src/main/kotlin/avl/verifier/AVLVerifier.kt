package avl.verifier

import avl.*

class AVLVerifier(startDigest: Digest) {

    private var curDigest = startDigest

    fun proofSearch(key: Int, value: LeafData, nextKey: Int?, proof: Proof): Boolean {
        var curHash = hashLeafNode(key, value, nextKey)
        require(proof.entries.size == proof.directions.size)
        val size = proof.entries.size
        for (i in 0 until size) {
            val treeNodeHash = proof.entries[i]
            val curDirection = proof.directions[i]
            curHash = if (curDirection == Direction.LEFT) {
                hashTreeNode(curHash, treeNodeHash.data)
            } else {
                hashTreeNode(treeNodeHash.data, curHash)
            }
        }
        return curHash.data contentEquals curDigest.data
    }



}
