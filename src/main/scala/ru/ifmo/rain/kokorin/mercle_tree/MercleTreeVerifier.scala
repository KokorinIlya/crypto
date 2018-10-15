package ru.ifmo.rain.kokorin.mercle_tree

import MercleTreeUtils.{getHash, getParentNumber, calcPow2}
import scala.util.{Failure, Success, Try}
import ru.ifmo.rain.kokorin.common.{Digest, ProofElement, Document}

class MercleTreeVerifier(digest: Digest, docsCount: Int) {
  private val sz = calcPow2(docsCount) - 1

  def verify(index: Int, document: Document, proof: List[ProofElement]): Boolean = {
    Try {
      var curIndex = index + sz
      var curHash = getHash(document.value)
      var curHeight = 0
      while (curIndex != 0) {
        if (curIndex % 2 == 0) {
          curHash = getHash(Array.concat(proof(curHeight).value, curHash))
        } else {
          curHash = getHash(Array.concat(curHash, proof(curHeight).value))
        }
        curHeight += 1
        curIndex = getParentNumber(curIndex)
      }
      (curHeight == proof.length) && (curHash sameElements digest.value)
    } match {
      case Failure(_) => false
      case Success(value) => value
    }
  }
}
