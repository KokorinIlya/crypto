package ru.ifmo.rain.kokorin.mercle_tree

import java.security.MessageDigest
import ru.ifmo.rain.kokorin.common.Document

sealed class Node(val index: Int, val hash: Array[Byte])

case class TreeNode(override val index: Int, override val hash: Array[Byte]) extends Node(index, hash)

case class LeafNode(document: Document, override val index: Int, override val hash: Array[Byte])
  extends Node(index, hash)

object MercleTreeUtils {
  private val Hasher = MessageDigest.getInstance("SHA-256")

  def getHash(bytes: Array[Byte]): Array[Byte] = Hasher.digest(bytes)

  def getParentNumber(index: Int): Int = {
    if (index % 2 == 0) {
      (index - 2) / 2
    } else (index - 1) / 2
  }

  def calcPow2(length: Int): Int = {
    var i = 1
    while (i < length) {
      i *= 2
    }
    i
  }
}
