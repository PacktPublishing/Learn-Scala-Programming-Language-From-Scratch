package com.eduonix

case class ScalaParserBag(s:String) {

}
import java.nio.file._

class ScalaParser(path:String) {
  val pathToData= Paths.get(path)
  val bufferedSource = io.Source.fromURL(pathToData.toUri().toURL())

  def parseLines () : Unit = {

    for (line <- bufferedSource.getLines) {
      val cols = line.split("\t").map(_.trim)
      // do whatever you want with the columns here
      println(s"${cols(0)}|${cols(1)}|${cols(2)}|${cols(3)}")
    }

  }
}

object ScratchPad {

  def main( args:Array[String] ): Unit = {
    val scalaParser = new  ScalaParser("src/main/resources/ComercialBanksTest.csv")
    scalaParser.parseLines()

  }

}