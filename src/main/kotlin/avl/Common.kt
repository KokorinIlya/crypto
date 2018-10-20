package avl

import java.security.MessageDigest
import java.util.*

data class ProofHash(val data: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProofHash

        if (!Arrays.equals(data, other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(data)
    }
}

data class Hash(val data: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Hash

        if (!Arrays.equals(data, other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(data)
    }
}

sealed class Node(open var prev: Node?)

data class LeafNode(val key: Int, val nextKey: Int?, val data: ByteArray, override var prev: Node?): Node(prev) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LeafNode

        if (key != other.key) return false
        if (nextKey != other.nextKey) return false
        if (!Arrays.equals(data, other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key
        result = 31 * result + (nextKey ?: 0)
        result = 31 * result + Arrays.hashCode(data)
        return result
    }
}

data class TreeNode(var left: Node?, var right: Node?, var hash: ByteArray,
                    override var prev: Node?, var rightMin: Int) : Node(prev) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TreeNode

        if (left != other.left) return false
        if (right != other.right) return false
        if (!Arrays.equals(hash, other.hash)) return false
        if (prev != other.prev) return false

        return true
    }

    override fun hashCode(): Int {
        var result = left?.hashCode() ?: 0
        result = 31 * result + (right?.hashCode() ?: 0)
        result = 31 * result + Arrays.hashCode(hash)
        result = 31 * result + (prev?.hashCode() ?: 0)
        return result
    }
}

val Hasher: MessageDigest = MessageDigest.getInstance("SHA-256")

fun intToByteArray(x: Int): ByteArray {
    val result = ByteArray(4)
    result[0] =
}

fun hashLeafNode(key: Int, value: ByteArray, nextKey: Int?): Hash {
    val keyBytes = ByteArray(4)

}


