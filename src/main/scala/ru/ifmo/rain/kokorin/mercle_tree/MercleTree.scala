package ru.ifmo.rain.kokorin.mercle_tree

import scala.collection.mutable.ListBuffer
import MercleTree._
import MercleTreeUtils.{getHash, getParentNumber, calcPow2}

sealed class Node(val index: Int, val hash: Array[Byte])

case class TreeNode(override val index: Int, override val hash: Array[Byte]) extends Node(index, hash)

case class LeafNode(document: Document, override val index: Int, override val hash: Array[Byte])
  extends Node(index, hash)

case class Document(value: Array[Byte])

case class ProofElement(value: Array[Byte])

case class Digest(value: Array[Byte])

class MercleTree(docs: Array[Document]) {

  def getDocument(ind: Int): Document = {
    if (ind < docs.length) {
      docs(ind)
    } else Document(Array[Byte](0))
  }

  private val sz = calcPow2(docs.length) - 1
  private val nodes: Array[Node] = Array.fill(2 * sz + 1)(null)

  private def createNode(index: Int): Node = {
    nodes(index) = if (index >= sz) {
      val doc = getDocument(index - sz)
      LeafNode(doc, index, getHash(doc.value))
    } else {
      val leftInd = 2 * index + 1
      val rightInd = 2 * index + 2
      val leftHash = createNode(leftInd).hash
      val rightHash = createNode(rightInd).hash
      TreeNode(index, getHash(Array.concat(leftHash, rightHash)))
    }
    nodes(index)
  }

  val digest: Digest = Digest(createNode(0).hash)

  def getProof(docIndex: Int): List[ProofElement] = {
    val index = docIndex + sz
    val answer = new ListBuffer[ProofElement]()

    var curInd = index

    while (curInd != 0) {
      val other = getOther(curInd)
      answer.append(ProofElement(nodes(other).hash))
      curInd = getParentNumber(curInd)
    }

    answer.toList
  }
}

object MercleTree {
  private def getOther(index: Int) = {
    val parent = getParentNumber(index)
    if (index % 2 == 0) {
      parent * 2 + 1
    } else parent * 2 + 2
  }
}

