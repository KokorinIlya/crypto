package ru.ifmo.rain.kokorin.sparse_mercle_tree

import java.security.MessageDigest
import java.util.Base64

object SparseMercleTreeUtils {
  def getParentNumber(index: Int): Int = {
    if (index % 2 == 0) {
      (index - 2) / 2
    } else (index - 1) / 2
  }

  def getBrotherNumber(index: Int): Int = {
    val parentNumber = getParentNumber(index)
    if (index % 2 == 0) {
      parentNumber * 2 + 1
    } else parentNumber * 2 + 2
  }

  private val Hasher = MessageDigest.getInstance("SHA-256")

  def hashLeaf(documentData: Array[Byte]): Array[Byte] = {
    if (documentData == null) {
      null
    } else {
      Hasher.digest(
        Array.concat(
          Array[Byte](0),
          documentData
        )
      )
    }
  }

  def hashNode(leftHash: Array[Byte], rightHash: Array[Byte]): Array[Byte] = {
    if (leftHash.isEmpty && rightHash.isEmpty) {
      null
    } else Hasher.digest(
      Array.concat(
        Array[Byte](1),
        leftHash,
        Array[Byte](2),
        rightHash
      )
    )
  }
}
