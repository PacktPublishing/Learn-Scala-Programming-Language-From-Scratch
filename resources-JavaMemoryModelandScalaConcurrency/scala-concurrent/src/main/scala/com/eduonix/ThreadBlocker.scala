package com.eduonix

import java.net.{HttpURLConnection, URL, InetSocketAddress}
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, Props, Actor, ActorSystem}
import akka.io.{IO, Tcp}
import akka.pattern.Patterns
import akka.util.{Timeout, ByteString}

import scala.concurrent.Await

/**
  * Created by osboxes on 01/12/15.
  * http://doc.akka.io/docs/akka/snapshot/scala/io-tcp.html
  * http://www.scalablescala.com/presents/building-on-akka.html#/22
  * http://doc.akka.io/docs/akka-stream-and-http-experimental/1.0-M2/scala/stream-io.html
  * http://alvinalexander.com/scala/simple-akka-actors-remote-example
  *
  *   val t = new Timeout(5, TimeUnit.SECONDS);

  val  fut = Patterns.ask(clientActor, "connect", t);

  val  response =  Await.result(fut, t.duration );

  println(s"Actor Response: $response")

   clientActor ! "connect"

  *
  *
  */

object  ThreadBlocker extends App {

  val system = ActorSystem("ActorSystem-for-Application")

  def props(remote: String, replies: ActorRef) =
    Props(classOf[SimpleClientActor], remote, replies)

  val clientActor = system
                    .actorOf(ThreadBlocker.props("http://www.google.com" , null)
                                                      ,"SimpleClientActor")
  val t = new Timeout(5, TimeUnit.SECONDS)

  val  future = Patterns.ask(clientActor, "connect", t)

  val  response =  Await.result(future, t.duration )

  println(s"Actor Response: $response")



}


class SimpleClientActor(remote: String, listener: ActorRef) extends Actor {


  def receive = {
    // --This is a defined message
    case "connect" => {

      val responseCode =   getUrlInputStream(remote)

      sender ! responseCode

    }

    // --This is a undefined message
    case _       => println("recieved undefined message  do nothing")
  }



  def getUrlInputStream(url: String,
                        connectTimeout: Int = 5000,
                        readTimeout: Int = 5000,
                        requestMethod: String = "GET"): Int = {
    val u = new URL(url)
    val conn = u.openConnection.asInstanceOf[HttpURLConnection]
    HttpURLConnection.setFollowRedirects(false)
    conn.setConnectTimeout(connectTimeout)
    conn.setReadTimeout(readTimeout)
    conn.setRequestMethod(requestMethod)
    conn.connect
    val rCode =conn.getResponseCode
    println(s"ResponseCode: $rCode")
    val inStream = conn.getInputStream
    val content = io.Source.fromInputStream(inStream)(io.Codec.ISO8859).mkString
    println(content)

    return rCode

  }
}