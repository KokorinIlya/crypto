package ru.ifmo.rain.kokorin.sparse_mercle_tree

object TestUtils {
  def getMap(inds: Array[Int], docs: Array[Array[Byte]]): Map[Int, Array[Byte]] = {
    require(inds.length == docs.length)
    inds.zip(docs).toMap
  }
}
