package com.eduonix.logistic


import org.apache.spark.sql.{Row, DataFrame, SQLContext}
import org.apache.spark.{SparkContext, SparkConf}

import scala.collection.JavaConversions._


object BinClassRunner extends App{


  val sparkConf = new SparkConf().setAppName("BinClassRunner").setMaster("local[*]")

  val ctx = new  SparkContext(sparkConf)
  val sqlContext = new SQLContext(ctx)

  // this is used to implicitly convert an RDD to a DataFrame.
  import sqlContext.implicits._

  val urlForFileToZip = getClass.getResource ("/com/eduonix/logistic/ComercialBanks10k.csv").getPath

  println(urlForFileToZip)


  val addressData =  ctx.textFile(urlForFileToZip).cache()

  val addressDataFrame =  addressData.map(_.split(" +"))
    .map(p => DuplicateBag( p(0), p(1), p(2), p(3), p(4), p(5), p(6), p(7))).toDF()


//  DuplicateBag( p(0), p(1), p(2), p(3), p(4), p(5), p(6), p(7)





//
//
//
//  val reduced_set = sqlContext.sql("SELECT id, name, billingCity FROM raw_data limit 5").cache()
//
//  reduced_set.map(t => "id: " + t(0)).collect().foreach(println)



}