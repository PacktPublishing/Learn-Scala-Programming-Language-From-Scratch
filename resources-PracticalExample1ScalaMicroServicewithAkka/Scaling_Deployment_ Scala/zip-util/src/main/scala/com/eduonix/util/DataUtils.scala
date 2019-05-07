package com.eduonix.util

import java.io.{FileInputStream, ByteArrayInputStream, ByteArrayOutputStream}


import java.util.zip._
import java.io._

import akka.actor.{Actor, ActorSystem, Props}


import scala.util.{Failure, Try, Success}
import scala.collection.JavaConversions._



object UtilsRunner extends App {

  val system = ActorSystem("ActorSystem-for-Application")

  def props(fileToZip : File, urlForFileToZip : String ) =
    Props(classOf[ZipUtilsActor], fileToZip, urlForFileToZip)


  val homeDir = System.getProperty("user.dir")



  val dirOfFilesToZipLocation = s"${homeDir}/deployment"

  val dirOfFiles : File = new File(dirOfFilesToZipLocation)


  var counter :Int = 0

  for(file <- dirOfFiles.listFiles if file.getName endsWith ".csv"){
    println(file.getName)

    val clientActor = system
      .actorOf(UtilsRunner.props(file , file.getPath.replaceFirst(".csv", ".zip"))
        ,s"${counter}ZipUtilsActor")

    clientActor ! "compressFile"

    counter =counter +1
  }

}




  class ZipUtilsActor (fileToZip : File, urlForFileToZip : String ) extends Actor  {

    var zipFile = fileToZip


    def receive = {
      case "compressFile" => {
        compressFile(urlForFileToZip)
      }
      case _  => println("recieved undefined message  do nothing")
    }



    def compressFile(url:String ) = Try {
      val file : File = new File(url)
      val buffer = new Array[Byte](512)
      val input =  new  FileInputStream(zipFile)
      var counter = 0
      var read = 0
      val out = new FileOutputStream(file);
      val zipOutputStream = new GZIPOutputStream(out)

      while({read =input.read(buffer); read != -1 }) {
        counter = counter + read
        zipOutputStream.write(buffer)
      }

      zipOutputStream.close()
      out.close();

      println(s"counter  ${counter}")
    }



  }