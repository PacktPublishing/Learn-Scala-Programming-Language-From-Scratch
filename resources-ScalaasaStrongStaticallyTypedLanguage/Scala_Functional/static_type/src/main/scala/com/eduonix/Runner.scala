package com.eduonix

import scala.slick.driver.H2Driver.simple._

object Runner extends App {
  
    // The query interface for the Suppliers table
  val suppliers: TableQuery[Suppliers] = TableQuery[Suppliers]

  // the query interface for the Coffees table
  val coffees: TableQuery[Coffees] = TableQuery[Coffees]
  
    // Create a connection (called a "session") to an in-memory H2 database
  val db = Database.forURL("jdbc:h2:mem:hello", driver = "org.h2.Driver")
  db.withSession { implicit session =>

    // Create the schema by combining the DDLs for the Suppliers and Coffees
    // tables using the query interfaces
    (suppliers.ddl ++ coffees.ddl).create

  
    /* Create / Insert */
  
    // Insert some suppliers
    suppliers += (101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199")
    suppliers += ( 49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460")
    suppliers += (150, "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966")

    // Insert some coffees (using JDBC's batch insert feature)
    val coffeesInsertResult: Option[Int] = coffees ++= Seq (
      ("Colombian",         101, 7.99, 0, 0),
      ("French_Roast",       49, 8.99, 0, 0),
      ("Espresso",          150, 9.99, 0, 0),
      ("Colombian_Decaf",   101, 8.99, 0, 0),
      ("French_Roast_Decaf", 49, 9.99, 0, 0)
    )
  
    val allSuppliers: List[(Int, String, String, String, String, String)] =
      suppliers.list

    // Print the number of rows inserted
    coffeesInsertResult foreach { numRows =>
      println(s"Inserted $numRows rows into the Coffees table")
    }
    
  } //end h2 session 
  
    println("Generated SQL for base Coffees query:\n" + coffees.selectStatement)

    // Query the Coffees table using a foreach and print each row
    coffees foreach { case (name, supID, price, sales, total) =>
      println("  " + name + "\t" + supID + "\t" + price + "\t" + sales + "\t" + total)
    }
  
  
}