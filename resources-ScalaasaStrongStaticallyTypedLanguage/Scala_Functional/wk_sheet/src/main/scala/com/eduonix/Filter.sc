package com.eduonix

object Filter {


val set1 = (-10 to 10).toSet
def isEven(i:Int) = i % 2 == 0

set1.filterNot(i => isEven(i))


}