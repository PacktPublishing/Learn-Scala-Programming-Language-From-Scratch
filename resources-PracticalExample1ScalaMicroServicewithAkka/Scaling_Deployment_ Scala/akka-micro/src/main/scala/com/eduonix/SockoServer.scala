package com.eduonix


import java.util.Date

import org.mashupbots.socko.events.HttpRequestEvent
import org.mashupbots.socko.routes._
import org.mashupbots.socko.infrastructure.Logger
import org.mashupbots.socko.webserver.WebServer
import org.mashupbots.socko.webserver.WebServerConfig

import akka.actor.{Actor, ActorSystem, Props}


object SockoServerApp extends Logger {
  //
  // STEP #1 - Define Actors and Start Akka
  // See `ServiceHandler`
  //
  val actorSystem = ActorSystem("DateServiceActorSystem")

  //
  // STEP #2 - Define Routes
  // Dispatch all HTTP GET events to a newly instanced `ServiceHandler` actor for processing.
  // `ServiceHandler` will `stop()` itself after processing each request.
  //
  val routes = Routes({
    case GET(request) => {
      actorSystem.actorOf(Props[ServiceHandler]) ! request
    }
  })

  //
  // STEP #3 - Start and Stop Socko Web Server
  //
  def main(args: Array[String]) {
    val webServer = new WebServer(WebServerConfig(), routes, actorSystem)
    webServer.start()

    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run { webServer.stop() }
    })

    System.out.println("Open your browser and navigate to http://localhost:8888")
  }
}

/**
  * ServiceHandler processor writes a date and stops.
  */
class ServiceHandler extends Actor {
  def receive = {
    case event: HttpRequestEvent =>
      event.response.write("Date Service (" + new Date().toString + ")")
      context.stop(self)
  }
}