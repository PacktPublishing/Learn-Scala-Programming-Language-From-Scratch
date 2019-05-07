



package com.sbeans

case class User(firstName: String, lastName: String) {  }


class ScalaProductFactory(x: Int,  y: Int) {
  // val evaluates as a read only varaible when defined
  val result =  x * y
  def this(x: Int) = this(x, 1)

  def supplyProduct(): Int = result

}

class ScalaClass {

  // def : handle : parameters : return type
  def multiply (x: Int,  y: Int): Int= {  x * y }
}

object ScalaRunner {

 // def : handle : parameters : return type
  def main( args:Array[String] ): Unit = {

   val sc = new ScalaClass()
   println("Scala simple function "+ sc.multiply(2,3))

   val scPFItem = new ScalaProductFactory(3,4)
   println("Scala simple Class "+ scPFItem.supplyProduct())

   var scPFLimited_Item = new ScalaProductFactory(3)
   println("Scala simple overloaded  Class  "
     + scPFLimited_Item.supplyProduct())
 }


}





