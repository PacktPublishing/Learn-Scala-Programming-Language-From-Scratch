package com.eduonix

import com.rockymadden.stringmetric.similarity._


object Main extends App {
  	println("Hello, project world")
	val metricCalc  = NGramMetric(1).compare("night", "nacht")
	//http://docs.scala-lang.org/overviews/core/string-interpolation.html
	println(s"Run a dependency lib, $metricCalc")
}
