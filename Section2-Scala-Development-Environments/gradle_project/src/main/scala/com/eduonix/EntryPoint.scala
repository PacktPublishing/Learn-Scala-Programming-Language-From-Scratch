package com.eduonix


import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence

import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

object Publisher {

  def main(args: Array[String]) {
    val brokerUrl = "tcp://localhost:1883"
    val topic = "eduonix.match.service"
    val msg = "gold is where you find it"

    var client: MqttClient = null

    // Creating new persistence for mqtt client
    val persistence = new MqttDefaultFilePersistence("/tmp")

    try {
      // mqtt client with specific url and client id
      client = new MqttClient(brokerUrl, MqttClient.generateClientId, persistence)

      client.connect()

      println("Connected to HiveMQ on port 1883 publishing to Topic: %s ".format(topic ))

      val msgTopic = client.getTopic(topic)
      val message = new MqttMessage(msg.getBytes("utf-8"))
      Thread.sleep(3000)
        msgTopic.publish(message)
        println("Requesting match text evaluation on Topic: %s, Message : %s".format(msgTopic.getName, message))


    }

    catch {
      case e: MqttException => println("Exception Caught: " + e)
    }

    finally {
      client.disconnect()
    }
  }
}


import com.rockymadden.stringmetric.similarity._
object Subscriber {

  def main(args: Array[String]) {

    val brokerUrl = "tcp://localhost:1883"
    val topic = "eduonix.match.service"

    //Set up persistence for messages
    val persistence = new MemoryPersistence

    //Initializing Mqtt Client specifying brokerUrl, clientID and MqttClientPersistance
    val client = new MqttClient(brokerUrl, MqttClient.generateClientId, persistence)

    //Connect to MqttBroker
    client.connect

    //Subscribe to Mqtt topic
    client.subscribe(topic)

    println("Connected to HiveMQ on port 1883 subscribing to Topic: %s ".format(topic ))

    //Callback automatically triggers as and when new message arrives on specified topic
    val callback = new MqttCallback {

      override def messageArrived(topic: String, message: MqttMessage): Unit = {
        println("Receiving Data for Topic: %s, Message is: %s".format(topic, message))
        val metricCalc  = NGramMetric(1).compare("message", "gold-mine")
        println("Match test for NGramMetric similarity to 'gold-mine' is: %s, Message is: %s".format(topic, metricCalc))

      }

      override def connectionLost(cause: Throwable): Unit = {
        println(cause)
      }

      override def deliveryComplete(token: IMqttDeliveryToken): Unit = {

      }
    }

    //Set up callback for MqttClient
    client.setCallback(callback)

  }
}