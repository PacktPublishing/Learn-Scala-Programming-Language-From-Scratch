package com.eduonix

import java.util.UUID

import _root_.akka.io.IO
import _root_.akka.io.Tcp
import akka.actor._
import akka.io.{ IO, Tcp }
import akka.util.{ByteStringBuilder, ByteString}
import akka.serialization.SerializationExtension
import akka.util.ByteString
import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._
import scala.util.Random


/**
  * Created by ubu on 01/12/15.
  *
  * https://github.com/zarinfam/akka-tcp-client-server-java/blob/master/src/main/java/com/saeed/akka/tcp/ApplicationMain.java
  */

object ThreadBlockerAdvanced {
  def props(systemListener: ActorRef) = Props(new ThreadBlockerAdvanced(systemListener))
  val path = "tcp"
}


class ThreadBlockerAdvanced(systemListener: ActorRef) extends Actor with ActorLogging {

  import akka.io.Tcp._
  import context.{ dispatcher, system }

  private val simpleBuffer = new SimpleBuffer
  private var connectMsg: Option[Connect] = None
  private var retryCount = 0
  private def resetRetryCount() = retryCount = 0

  private def exponentialBackOff(): FiniteDuration = {
    def nextDelay(r: Int) = (scala.math.pow(2, r - 1).round * 100 * (Random.nextDouble + 1)).milliseconds
    val delay = if (retryCount == 0) Duration.Zero else nextDelay(retryCount)
    retryCount = retryCount + 1
    log.info(s"Reconnect attempt #$retryCount after ${delay.toMillis / 1000.0f} seconds...")
    delay
  }

  private def handleDataReceived(data: ByteString): Unit = {
    log.debug(s"Attempting to deserialize data as SerializableMessage from ByteString length ${data.size}")
    simpleBuffer.nextMessageBytes(data).foreach { bytes ⇒
      val msg = SerializableMessage(bytes)
      log.debug("Received " + (msg match {
        case _: RoomInfo ⇒ "RoomInfo(...)"
        case m           ⇒ m.toString
      }))
      systemListener ! msg
    }
  }

  def receive = {
    case msg: Connect ⇒
      connectMsg = Some(msg)
      IO(Tcp) ! msg

    case CommandFailed(_: Connect) ⇒
      system.scheduler.scheduleOnce(exponentialBackOff(), IO(Tcp), connectMsg.get)

    case c @ Connected(remoteAddress, localAddress) ⇒
      resetRetryCount()
      systemListener ! c
      val connection = sender()
      connection ! Register(self)
      context become {
        case msg: SerializableMessage ⇒
          log.debug(s"Writing $msg")
          connection ! Write(simpleBuffer.serializableMessageWithLength(msg))
        case Received(data) ⇒
          log.debug(s"Received data of class: ${data.getClass.getName}")
          handleDataReceived(data)
        case CommandFailed(w: Write) ⇒
          // O/S buffer was full
          systemListener ! "write failed"
        case msg: ConnectionClosed if !msg.isPeerClosed ⇒
          // close was initiated by this side (the client), so don't try to reconnect
          context.unbecome()
          systemListener ! msg
        case msg: ConnectionClosed if msg.isPeerClosed ⇒
          log.error("Connection closed, attempting to reconnect..")
          context.unbecome()
          system.scheduler.scheduleOnce(exponentialBackOff(), IO(Tcp), connectMsg.get)
        case "close" ⇒
          connection ! Close
        case msg ⇒
          log.error(s"Unexpected message received: $msg")
      }
  }

}




class SimpleBuffer {

  implicit val byteOrder = java.nio.ByteOrder.BIG_ENDIAN

  private var buffer: ByteString = ByteString()
  private var _expectedByteCount: Int = 0

  def nextMessageBytes(incomingBytes: ByteString): List[ByteString] = {
    val resultBuffer = new ListBuffer[ByteString]()

    def isBufferReady = expectedByteCount() > 0 && bufferHasCompleteFrame

    def expectedByteCount() = {
      if (_expectedByteCount > 0) {
        _expectedByteCount
      }
      else if (buffer.size >= 4) {
        val iterator = buffer.iterator
        buffer = buffer.drop(4)
        _expectedByteCount = iterator.getInt
        _expectedByteCount
      }
      else 0
    }

    def bufferHasCompleteFrame = {
      val count = expectedByteCount()
      count <= buffer.size
    }

    def takeFrame() = {
      val (prefix, suffix) = buffer.splitAt(expectedByteCount())
      resultBuffer += prefix
      buffer = suffix
      _expectedByteCount = 0
    }

    @tailrec
    def process(): Unit = {
      if (isBufferReady) {
        takeFrame()
        process()
      }
    }

    buffer = buffer ++ incomingBytes
    process()
    resultBuffer.toList
  }

  def serializableMessageWithLength(msg: SerializableMessage)(implicit system: ActorSystem): ByteString = {
    val dataBytes: Array[Byte] = msg.toByteArray
    val builder = new ByteStringBuilder
    builder.putInt(dataBytes.size)
    builder.putBytes(dataBytes)
    builder.result()
  }
}


class ResultStatus extends Enumeration {
}

object ResultStatus extends Enumeration {
  type ResultStatus = Value
  val Ok, WrongPassword = Value
}


object SerializableMessage {
  def apply(byteString: ByteString)(implicit system: ActorSystem): SerializableMessage = {
    val serialization = SerializationExtension(system)
    serialization.deserialize(byteString.toArray, classOf[SerializableMessage])
      .recover { case e ⇒ throw e }
      .get
  }
}

sealed trait SerializableMessage extends Serializable {
  def toByteArray(implicit system: ActorSystem): Array[Byte] = {
    val serialization = SerializationExtension(system)
    serialization.serialize(this)
      .recover { case e ⇒ throw e }
      .get
  }
}

case class AskLogin(username: String, password: String)
  extends SerializableMessage

case class LoginResult(result: ResultStatus, username: String)
  extends SerializableMessage

case class Join(username: String, roomName: String)
  extends SerializableMessage

case class LeaveChat(username: String, roomName: String)
  extends SerializableMessage

// The id is a server-generated time-based UUID, filled in by the server
case class Chat(id: Option[UUID], sender: String, roomName: String, htmlText: String)
  extends SerializableMessage

case class RoomInfo(roomName: String, history: List[Chat], participants: List[String])
  extends SerializableMessage

