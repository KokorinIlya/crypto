package avl

import java.security.MessageDigest
import java.util.*

data class NodeHeightInfo(val leftHeight: Int, val rightHeight: Int)

enum class Direction {
    LEFT, RIGHT
}

data class Proof(val entries: MutableList<Hash>,
                 val heights: MutableList<NodeHeightInfo>, val directions: MutableList<Direction>) {

    init {
        require(entries.size == heights.size && heights.size == directions.size)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Proof

        if (entries != other.entries) return false
        if (heights != other.heights) return false
        if (directions != other.directions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = entries.hashCode()
        result = 31 * result + heights.hashCode()
        result = 31 * result + directions.hashCode()
        return result
    }
}

data class ProofEntity(val entry: Hash, val direction: Direction)

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

    override fun toString(): String {
        return Base64.getEncoder().encodeToString(data)
    }
}

data class Digest(val data: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Digest

        if (!Arrays.equals(data, other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(data)
    }
}

data class LeafData(val data: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LeafData

        if (!Arrays.equals(data, other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(data)
    }
}

sealed class Node(open var prev: Node?, open var hash: Hash)

data class LeafNode(val key: Int, var nextKey: LeafNode?, var prevKey: LeafNode?, val data: LeafData,
                    override var prev: Node?, override var hash: Hash): Node(prev, hash) {

    constructor(key: Int, nextKey: LeafNode?, prevKey: LeafNode?, data: LeafData, prev: Node?) : this(
            key, nextKey, prevKey, data, prev, hashLeafNode(key, data, nextKey)
    )
}

data class TreeNode(var left: Node?, var right: Node?, override var prev: Node?,
                    var rightMin: Int?, var allMin: Int?, override var hash: Hash,
                    var leftHeight: Int, var rightHeight: Int) : Node(prev, hash) {

    fun calculateAll() {
        calculateHash()
        calculateHeights()
        calculateMins()
        updatePrevs()
    }

    private fun updatePrevs() {
        if (left != null) {
            if (left is TreeNode) {
                (left as TreeNode).prev = this
            } else if (left is LeafNode) {
                (left as LeafNode).prev = this
            }
        }
        if (right != null) {
            if (right is TreeNode) {
                (right as TreeNode).prev = this
            } else if (right is LeafNode) {
                (right as LeafNode).prev = this
            }
        }
    }

    private fun calculateHash() {
        hash = hashTreeNode(left, right)
    }


    private fun calculateHeights() {
        leftHeight = getHeight(left)
        rightHeight = getHeight(right)
    }

    private fun calculateMins() {
        rightMin = getRightMinHelper(right)
        val leftAllMin = getAllMinHelper(left)
        val rightAllMin = getAllMinHelper(right)
        allMin = if (leftAllMin != null && rightAllMin != null) {
            minOf(leftAllMin, rightAllMin)
        } else leftAllMin ?: rightAllMin
    }

    private fun getAllMinHelper(node: Node?): Int? {
        return when (node) {
            null -> null
            is LeafNode -> node.key
            is TreeNode -> node.allMin
        }
    }

    private fun getRightMinHelper(node: Node?): Int? {
        return when (node) {
            null -> null
            is LeafNode -> node.key
            is TreeNode -> node.allMin
        }

    }

    private fun getHeight(node: Node?): Int {
        return when (node) {
            null -> 0
            is LeafNode -> 0
            is TreeNode -> maxOf(node.leftHeight, node.rightHeight) + 1
        }

    }
}

val Hasher: MessageDigest = MessageDigest.getInstance("SHA-256")

fun intToByteArray(x: Int): ByteArray {
    val result = ByteArray(4)
    val x0 = (x shr 24) and 255
    result[0] = x0.toByte()

    val x1 = (x shr 16) and 255
    result[1] = x1.toByte()

    val x2 = (x shr 8) and 255
    result[2] = x2.toByte()

    val x3 = x and 255
    result[3] = x3.toByte()

    return result
}

fun hashLeafNode(key: Int, value: LeafData, nextKey: LeafNode?): Hash {
    val keyBytes = intToByteArray(key)
    val nextKeyBytes = intToByteArray(nextKey?.key ?: Integer.MAX_VALUE)
    val pref = ByteArray(1){ i -> 0 }
    return Hash(pref + Hasher.digest(keyBytes + nextKeyBytes + value.data))
}

fun hashTreeNode(left: Node?, right: Node?): Hash {
    return hashTreeNode(left?.hash, right?.hash)
}


fun hashTreeNode(leftHash: Hash?, rightHash: Hash?): Hash {
    val pref1 = ByteArray(1){ i -> 1}
    val pref2 = ByteArray(1) { i -> 2 }
    val right = rightHash?.data ?: ByteArray(0)
    val left = leftHash?.data ?: ByteArray(0)
    return Hash(Hasher.digest(pref1 + left + pref2 + right))
}



