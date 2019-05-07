package com.eduonix

import java.net.{HttpURLConnection, URL}
import java.util.concurrent.TimeUnit

import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import akka.pattern.Patterns
import akka.util.Timeout

import scala.concurrent.Await
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry._
import scalafx.scene._
import scalafx.scene.control._
import scalafx.scene.layout._
import scalafx.Includes._
import scalafx.event.ActionEvent

/**
  *
// http://stackoverflow.com/questions/20828726/javafx2-or-scalafx-akka
// https://gist.github.com/mucaho/8973013

// http://stackoverflow.com/questions/21836133/scalafx-event-handler-with-first-class-function
// https://github.com/scalafx/ProScalaFX/blob/master/src/proscalafx/ch02/metronome1/Metronome1Main.scala

  val system = ActorSystem("ActorSystem-for-Application")

  def props(remote: String, replies: ActorRef) =
    Props(classOf[SimpleClientActor], remote, replies)

  val clientActor = system
    .actorOf(ThreadBlocker.props("http://www.google.com" , null)
      ,"SimpleClientActor");
  *
  *
  */


object EntryPoint extends JFXApp {

  val system = ActorSystem("ActorSystem-for-Application")

  def props(remote: String, replies: ActorRef) =
    Props(classOf[SimpleClientActor], remote, replies)

  val clientActor = system
    .actorOf(ThreadBlocker.props("http://www.google.com" , null)
      ,"SimpleClientActor")


  val t = new Timeout(5, TimeUnit.SECONDS)




  val button1 = new Button {
    text = "button 1"
    onAction = {
      event: ActionEvent => {
        var  future = Patterns.ask(clientActor, "connect", t)
        val  response =  Await.result(future, t.duration )
        feedBack.setText(s"Actor Response: $response")
      }

    }
  }

  val button2 = new Button {
    text = "button 2"
    onAction = {
      event: ActionEvent =>
        feedBack.setText("feedback from button 2")

    }
  }

  val button3 = new Button {
    text = "Exit"
    onAction = {
      event: ActionEvent => System.exit(1)
    }
  }


  val feedBack = new Label() {text = "feedback"}

  val buttons = new HBox {
    prefWidth = 380
    prefHeight = 100
    spacing = 20
    padding = Insets(0, 0, 0, 20 )
    children = List(button1, button2, button3)
  }

  stage = new PrimaryStage {
    scene = new Scene(400, 300) {
      root = new StackPane {
        padding = Insets(25)
        children = List(
          buttons, feedBack
        )
      }
    }
  }
}

//
//
//class SimpleReactiveActor(remote: String, listener: ActorRef) extends Actor {
//
//
//  def receive = {
//    // --This is a defined message
//    case "connect" => getUrlInputStream(remote)
//    // --This is a undefined message
//    case _       => println("recieved undefined message  do nothing")
//  }
//
//
//
//  def getUrlInputStream(url: String,
//                        connectTimeout: Int = 5000,
//                        readTimeout: Int = 5000,
//                        requestMethod: String = "GET") = {
//    val u = new URL(url)
//    val conn = u.openConnection.asInstanceOf[HttpURLConnection]
//    HttpURLConnection.setFollowRedirects(false)
//    conn.setConnectTimeout(connectTimeout)
//    conn.setReadTimeout(readTimeout)
//    conn.setRequestMethod(requestMethod)
//    conn.connect
//    val rCode =conn.getResponseCode
//    println(s"ResponseCode: $rCode")
//    val inStream = conn.getInputStream
//    val content = io.Source.fromInputStream(inStream)(io.Codec.ISO8859).mkString
//    println(content)
//    System.exit(1)
//
//  }
//}