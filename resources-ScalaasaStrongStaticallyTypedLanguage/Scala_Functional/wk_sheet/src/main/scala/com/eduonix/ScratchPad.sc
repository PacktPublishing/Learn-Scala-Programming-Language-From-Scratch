package com.eduonix
import java.nio.file._

object ScratchPadDev {

 val testLine = "001300000063ZHxAAM		Alliant Securities, Inc.	695 N Legacy Ridge Dr Ste 300	Liberty Lake	WA	99019-7722	US		84"
                                                  //> testLine  : String = 001300000063ZHxAAM		Alliant Securities, Inc.
                                                  //| 	695 N Legacy Ridge Dr Ste 300	Liberty Lake	WA	99019-7722	
                                                  //| US		84
 val cols = testLine.split("\t").map(_.trim)      //> cols  : Array[String] = Array(001300000063ZHxAAM, "", Alliant Securities, In
                                                  //| c., 695 N Legacy Ridge Dr Ste 300, Liberty Lake, WA, 99019-7722, US, "", 84)
                                                  //| 
 val k =cols.length                               //> k  : Int = 10

}