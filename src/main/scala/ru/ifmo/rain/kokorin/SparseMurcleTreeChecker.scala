package ru.ifmo.rain.kokorin

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Base64

import scala.io.StdIn

object SparseMurcleTreeChecker {

  private val digest = MessageDigest.getInstance("SHA-256")

  def getData(): String = {
    val x = StdIn.readLine()
    if (x == "null") {
      null
    } else x
  }

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
    val sz = (1 << h) - 1
    val rootHash = Base64.getDecoder.decode(StdIn.readLine().getBytes(StandardCharsets.UTF_8))
    val q = StdIn.readInt()
    for (i <- 1 to q) {
      val lines = StdIn.readLine().split(" ")
      val index = Integer.parseInt(lines(0))
      val data = if (lines(1) == "null") null else lines(1)

      var curIndex = index + sz
      var curHash = hashLeaf(data)

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
