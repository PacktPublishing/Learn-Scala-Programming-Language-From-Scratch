package com.sbeans

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

object AkkaRunner  extends App  {
 // only aplphanumeric and no leading _ for ActorSystem identifiers
  val system = ActorSystem("ActorSystem_for_Application")
  val simpleActor =
          system.actorOf(Props[SimpleActor], name = "simpleActor")
// send the messages to the Actor  "receive"  method
  simpleActor ! "Defined Message"
  simpleActor ! "This is a message not defined in the simple actor"
}


class SimpleActor extends Actor {
  // define behavior in this method.
  def receive = {
    // --This is a defined message
    case "Defined Message" => println("Hello Concurrent  World")
     // --This is a undefined message
    case _       => println("recieved undefined message  do nothing")
  }
}