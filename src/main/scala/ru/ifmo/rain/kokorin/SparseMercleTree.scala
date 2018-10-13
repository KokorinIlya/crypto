package ru.ifmo.rain.kokorin

import java.security.MessageDigest
import java.util.Base64

import SparseMercleTree._

import scala.io.StdIn

class SparseMercleTree(documents: Map[Int, String], height: Int) {
  private val size = (1 << height) - 1
  private val nodes: Array[Array[Byte]] = Array.fill(2 * size + 1)(null)

  private def buildNode(index: Int): Array[Byte] = {
    nodes(index) = if (index >= size) {
      val documentIndex = index - size
      val document = documents.get(documentIndex).orNull
      hashLeaf(document)
    } else {
      val leftIndex = 2 * index + 1
      val rightIndex = 2 * index + 2
      val leftHash = buildNode(leftIndex)
      val rightHash = buildNode(rightIndex)
      hashNode(leftHash, rightHash)
    }
    nodes(index)
  }

  private val rootHash = buildNode(0)

  def getDocument(index: Int): String = documents.getOrElse(index, "null")

  def getProof(index: Int): Array[Array[Byte]] = {
    val proof: Array[Array[Byte]] = Array.fill(height)(null)
    var curIndex = index + size
    for (i <- 0 until height) {
      val parent = if (curIndex % 2 == 1) {
        (curIndex - 1) / 2
      } else (curIndex - 2) / 2
      if (curIndex % 2 == 1) {
        proof(i) = nodes(parent * 2 + 2)
      } else {
        proof(i) = nodes(parent * 2 + 1)
      }
    }
    proof
  }
}

object SparseMercleTree {
  private val digest = MessageDigest.getInstance("SHA-256")

  def hashLeaf(string: String): Array[Byte] = {
    if (string == null) {
      null
    } else {
      digest.digest(
        Array.concat(
          Array[Byte](0),
          Base64.getDecoder.decode(string)
        )
      )
    }
  }

  def hashNode(leftHash: Array[Byte], rightHash: Array[Byte]): Array[Byte] = {
    if (leftHash == null && rightHash == null) {
      null
    } else if (leftHash == null && rightHash != null) {
      digest.digest(
        Array.concat(
          Array[Byte](1),
          Array[Byte](2),
          rightHash
        )
      )
    } else if (leftHash != null && rightHash == null) {
      digest.digest(
        Array.concat(
          Array[Byte](1),
          leftHash,
          Array[Byte](2)
        )
      )
    } else digest.digest(
      Array.concat(
        Array[Byte](1),
        leftHash,
        Array[Byte](2),
        rightHash
      )
    )
  }

  def main(args: Array[String]): Unit = {
    val h = StdIn.readInt()
    val blocks = StdIn.readInt()
    val map = (for {
      i <- 0 until blocks
      lines = StdIn.readLine().split(" ")
      num = Integer.parseInt(lines(0))
      data = if (lines(1) == "null") null else lines(1)
    } yield (num, data)).toMap

    val tree = new SparseMercleTree(map, h)

    val z = StdIn.readLine()
    val qs = StdIn.readLine().split(" ").map(Integer.parseInt)
    for (q <- qs) {
      val document = tree.getDocument(q)
      println(s"$q $document")
      val proof = tree.getProof(q)
      for (s <- proof) {
        val curProof = Option(s).map(new String(_)).getOrElse("null")
        println(curProof)
      }
    }
  }
}
