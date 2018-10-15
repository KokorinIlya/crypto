package ru.ifmo.rain.kokorin.sparse_mercle_tree

import SparseMercleTreeUtils._

import scala.collection.mutable

class SparseMercleTree(documents: Map[Int, Array[Byte]], height: Int) {
  private val size = (1 << height) - 1

  private val nodesHash: mutable.Map[Int, Array[Byte]] = mutable.Map.empty[Int, Array[Byte]]

  private def buildNodes() {
    val nodesToBuild = mutable.Queue.empty[Int]
    for ((number, document) <- documents) {
      nodesToBuild.enqueue(number + size)
      nodesHash(number + size) = hashLeaf(document)
    }
    while (nodesToBuild.nonEmpty) {
      val curIndex = nodesToBuild.dequeue()
      if (curIndex != 0) {
        nodesToBuild.enqueue(getParentNumber(curIndex))
      }
      if (!nodesHash.contains(curIndex)) {
        val leftHash = nodesHash.getOrElse(2 * curIndex + 1, Array[Byte]())
        val rightHash = nodesHash.getOrElse(2 * curIndex + 2, Array[Byte]())
        nodesHash(curIndex) = hashNode(leftHash, rightHash)
      }
    }
  }

  buildNodes()

  def getDocument(documentNumber: Int): Array[Byte] = documents.getOrElse(documentNumber, null)

  def getProof(documentIndex: Int): Array[Array[Byte]] = {
    val proof: Array[Array[Byte]] = Array.fill(height)(null)
    var curIndex = documentIndex + size

    for (i <- 0 until height) {
      val brotherIndex = getBrotherNumber(curIndex)
      proof(i) = nodesHash.getOrElse(brotherIndex, null)
      curIndex = getParentNumber(curIndex)
    }

    proof
  }

  val digest: Array[Byte] = nodesHash.getOrElse(0, null)
}

