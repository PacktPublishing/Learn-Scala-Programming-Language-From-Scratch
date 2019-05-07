package com.logistic.artifacts

import java.net.URL

// https://github.com/apache/spark/tree/master/examples/src/main/scala/org/apache/spark/examples/ml

class BasicStats(fileName: String)  {


  def getDataFile :  URL = getClass.getResource(fileName)

}

import org.apache.spark.{SparkConf, SparkContext}


object BasicStatsRunner {

  // def handle parameters return type
  def main( args:Array[String] ): Unit = {

    val conf = new SparkConf()
      .setAppName("SimpleStatsExample")
      .setMaster("local[*]")

    val sc = new SparkContext(conf)

    val basicStats = new BasicStats("/ComercialBanks10k.csv")

    val url= basicStats.getDataFile

    // Resilient Distributed Dataset RDD
    val rawDataAsRDD = sc.textFile(url.getPath)

    println("Scala RDD count "+ rawDataAsRDD.count())

  }


}
