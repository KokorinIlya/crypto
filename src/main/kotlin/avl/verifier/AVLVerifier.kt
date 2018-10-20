package avl.verifier

import avl.Digest
import avl.LeafData
import avl.Proof
import avl.hashLeafNode

class AVLVerifier(startDigest: Digest) {
    fun proofSearch(key: Int, value: LeafData, nextKey: Int?, proof: Proof): Boolean {
        var curHash = hashLeafNode(key, value, nextKey)
        for ()
    }
}
