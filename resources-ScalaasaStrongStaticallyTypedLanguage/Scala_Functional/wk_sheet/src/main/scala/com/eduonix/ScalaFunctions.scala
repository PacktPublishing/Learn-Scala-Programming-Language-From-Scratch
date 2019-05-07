package com.eduonix
object FunctBag {
  def sum(n: Int, acc: Int):Int = 
    if(n == 0) acc else sum(n - 1, acc + n) 
 def errorMsg(errorCode: Int) = errorCode match {
 case 1 => "File not found"
 case 2 => "Permission denied"
 case 3 => "Invalid operation"  }
  
 def factorial(n: Long) : Long = n match {
    case 0 => 1
    case _ => n * factorial(n-1)
}

 
 
 
 def factorialWithTail(n: Int, accumulator: Long = 1): Long = {
    if(n == 0) accumulator else factorialWithTail(n - 1, (accumulator * n))
}   
def sqrFunction(x: Int) = x * x  

def addFunction(f: Int=>Int, a: Int, b: Int): Int = if (a == b) f(a) else f(a) + addFunction(f, a + 1, b) 

def add(x: Int, y: Int): Int = x + y 

def addCuuried(y: Int):Int=>Int = x => x + y 

def closureProvider(y: Int):Int=>Int = x => x + y 

def closureUser(f :Int=>Int  ) = { 
  println(f(2)) } 

}
object  FunctRunner extends App  {
    val r = FunctBag.closureUser(FunctBag.closureProvider(10))     
  //  println(r)

}