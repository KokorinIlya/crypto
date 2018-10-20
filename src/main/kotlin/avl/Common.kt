package avl

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

sealed class Node(open val prev: Node?)

data class LeafNode(val key: Int, val nextKey: Int?, val data: ByteArray, override val prev: Node): Node(prev) {
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

data class TreeNode(val left: Node?, val right: Node?, val hash: ByteArray, override val prev: Node?) : Node(prev) {

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



