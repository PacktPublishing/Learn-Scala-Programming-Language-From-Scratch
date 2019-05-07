package com.eduonix.jersyclient

import javax.ws.rs.client.{Entity, ClientBuilder}
import javax.ws.rs.core.MediaType

import org.glassfish.jersey.client.ClientResponse

/**
  * Created by osboxes on 10/12/15.
  */


class Client(baseUri:String)  {

  val client = ClientBuilder.newClient();

  val  webTarget = client.target(baseUri);

  val invocationBuilder =   webTarget.request(MediaType.TEXT_HTML)
    .accept(MediaType.TEXT_HTML);

  val response =  invocationBuilder.get( )

  val serverResultCode = response.getStatus

  println(s"${serverResultCode}")

 // val serverResult  = response.readEntity(manifest[String].erasure.asInstanceOf[Class[String]] )
  val serverResult  = response.readEntity(manifest[String].runtimeClass.asInstanceOf[Class[String]] )

  println(s"${serverResult}")
}

object ClientRunner extends App  {


  val client = new Client("http://localhost:8888")



}
