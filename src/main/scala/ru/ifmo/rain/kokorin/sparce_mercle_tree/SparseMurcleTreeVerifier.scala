package ru.ifmo.rain.kokorin.sparse_mercle_tree

import java.nio.charset.StandardCharsets
import java.util.Base64
import SparseMercleTreeUtils._

import scala.io.StdIn

class SparseMurcleTreeVerifier(digest: Array[Byte], height: Int) {
  private val sz = (1 << height) - 1

  def verify(index: Int, document: Array[Byte], proof: Array[Array[Byte]]): Boolean = {
    var curIndex = index + sz
    var curHash = hashLeaf(document)

    for (proofElement <- proof) {
      if (curIndex % 2 == 1) {
        curHash = hashNode(
          Option(curHash).getOrElse(Array[Byte]()),
          Option(proofElement).getOrElse(Array[Byte]())
        )
        curIndex = (curIndex - 1) / 2
      } else {
        curHash = hashNode(
          Option(proofElement).getOrElse(Array[Byte]()),
          Option(curHash).getOrElse(Array[Byte]())
        )
        curIndex = (curIndex - 1) / 2
      }
    }

    if (curHash == null && digest == null) {
      true
    } else digest != null && curHash != null && (digest sameElements curHash)
  }
}

object SparseMurcleTreeVerifier {

  def getData(): String = {
    val x = StdIn.readLine()
    if (x == "null") {
      null
    } else x
  }

  def main(args: Array[String]): Unit = {
    val h = StdIn.readInt()
    val sz = (1 << h) - 1
    val rootHash = Base64.getDecoder.decode(StdIn.readLine().getBytes(StandardCharsets.UTF_8))
    val q = StdIn.readInt()
    for (i <- 1 to q) {
      val lines = StdIn.readLine().split(" ")
      val index = Integer.parseInt(lines(0))
      val data = if (lines(1) == "null") null else lines(1)

      var curIndex = index + sz
      var curHash = hashLeaf(Base64.getDecoder.decode(data))

      for (j <- 1 to h) {
        val curProof = Option(getData())
          .map(x => Base64.getDecoder.decode(x.getBytes(StandardCharsets.UTF_8)))
          .orNull

        if (curIndex % 2 == 1) {
          curHash = hashNode(curHash, curProof)
          curIndex = (curIndex - 1) / 2
        } else {
          curHash = hashNode(curProof, curHash)
          curIndex = (curIndex - 2) / 2
        }
      }

      if ((curHash != null) && (curHash sameElements rootHash)) {
        println("YES")
      } else {
        println("NO")
      }
    }
  }
}
