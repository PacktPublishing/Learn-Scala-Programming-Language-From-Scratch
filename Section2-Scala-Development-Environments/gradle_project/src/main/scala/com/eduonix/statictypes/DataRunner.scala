package com.eduonix.statictypes


import scala.slick.driver.H2Driver.simple._

/**
  * https://github.com/typesafehub/activator-hello-slick/blob/wip/slick-2.0/src/main/scala/CaseClassMapping.scala
  * http://stackoverflow.com/questions/26284955/scala-slick-c3p0-connection-pool-using-mysql
  *
  *
  */
object DataRunner extends App {

  // The query interface for the Suppliers table
  val users: TableQuery[UserEntity] = TableQuery[UserEntity]

  val db = Database.forURL("jdbc:h2:mem:entitusers", driver = "org.h2.Driver")

  db.withSession { implicit session =>

    users.ddl.create
    // Insert some users
    val usersInsertResult: Option[Int] = users ++= Seq (
      (1, "John Doe", "99 Market Street", "Groundsville", "CA", "95199"),
      (2, "Jill Coe", "1 Party Place", "Mendocino", "CA", "95460")
    )

    usersInsertResult foreach (numRows =>
      println(s"Inserted $numRows rows into the UserEntity table"))

    println("Generated SQL for base UserEntity query:\n" + users.selectStatement)

    users.foreach(println)


  } //end h2 session



//  users foreach { case (id, name, street, city, state, zip) =>
//    println("  " + name + "\t" + street + "\t" + city + "\t" + state + "\t" + zip)



}
