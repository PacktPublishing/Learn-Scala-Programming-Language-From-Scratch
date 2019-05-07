package com.eduonix.statictypes

import scala.slick.driver.H2Driver.simple._
import scala.slick.lifted.{ProvenShape, ForeignKeyQuery}

//http://stackoverflow.com/questions/13906684/scala-slick-method-i-can-not-understand-so-far

case class Base(id: Option[Int] = None, name: String)

case class User(id: Option[Int] = None, name: String, street: String,
                                city: String, state: String, zip: String)


class UserEntity(tag: Tag) extends  Table[(Int, String, String, String, String, String )] (tag, "UserEntity") {
  // This is the primary key column:
  def id: Column[Int] = column[Int]("ID", O.PrimaryKey)
  def name: Column[String] = column[String]("NAME")
  def street: Column[String] = column[String]("STREET")
  def city: Column[String] = column[String]("CITY")
  def state: Column[String] = column[String]("STATE")
  def zip: Column[String] = column[String]("ZIP")

  //  * projection -> the columns (or computed values) I want.
  override def * : ProvenShape[( Int, String, String, String, String, String )] =
                                                  ( id, name, street, city, state, zip )
}
