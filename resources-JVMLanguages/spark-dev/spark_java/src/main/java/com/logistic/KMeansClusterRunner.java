package com.logistic;

import com.logistic.artifacts.KeyValueBag;
import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.clustering.*;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;

import java.io.File;
import java.io.IOException;
import java.util.StringJoiner;


/**
 * http://spark.apache.org/docs/latest/mllib-clustering.html
 */
public class KMeansClusterRunner {


    public static void main(String[] args) {
        try {
            File file = new File("myModelPath") ;
            if(FileUtils.getFile(file).exists()) {

                FileUtils.forceDelete(file); // delete output directory directory
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        SparkConf sparkConf = new SparkConf().setAppName("KMeansClusterRunner").setMaster("local[*]");
        JavaSparkContext ctx = new JavaSparkContext(sparkConf);
        JavaRDD<String> addressDataWithHeader = ctx
                .textFile(KMeansClusterRunner.class.getResource("/ComercialBanks10k.csv").toExternalForm());

        JavaRDD<KeyValueBag> addressData =  getReducedValues(addressDataWithHeader);

//        addressData.foreach(
//               kvBag ->    System.out.println(kvBag.value() )
//        );

        JavaRDD<Vector> parsedDataL = addressData.map(
                kVBag -> {

                    double[] values = new double[1];
                    values[0] = kVBag.value().hashCode();
                    return Vectors.dense(values);
                }
        );

//        parsedDataL.foreach(
//               kvBag ->    System.out.println(kvBag.toArray().length )
//        );


        parsedDataL.cache();

        // Cluster the data into two classes using KMeans
        int numClusters = 2;
        int numIterations = 20;
        // clustering is unsupervised so no labels
        KMeansModel clusters = KMeans.train(parsedDataL.rdd(), numClusters, numIterations);

        // Evaluate clustering by computing Within Set Sum of Squared Errors
        double WSSSE = clusters.computeCost(parsedDataL.rdd());
        System.out.println("Within Set Sum of Squared Errors = " + WSSSE);

        Vector[] clusterCenters =  clusters.clusterCenters();
         for (Vector v: clusterCenters ) {

             System.out.println(v);
         }

        // Index documents with unique IDs
        JavaPairRDD<Long, Vector> corpusL = JavaPairRDD.fromJavaRDD(parsedDataL.zipWithIndex().map(
                doc_id ->  doc_id.swap()
        ));

        corpusL.cache();

        LDA alloc = new LDA();
        alloc.setK(3);
        LDAModel ldaModel =  alloc.run(corpusL);

        // Output topics. Each is a distribution over words (matching word count vectors)
        System.out.println("Learned topics (as distributions over vocab of " + ldaModel.vocabSize()
                + " words):");

        Matrix topics = ldaModel.topicsMatrix();
        for (int topic = 0; topic < 3; topic++) {
            System.out.print("Topic " + topic + ":");
            for (int word = 0; word < ldaModel.vocabSize(); word++) {
                System.out.print(" " + topics.apply(word, topic));
            }
            System.out.println();
        }

    }


    static JavaRDD<KeyValueBag> getReducedValues(JavaRDD<String> addressDataWithHeader ) {

        String  header = addressDataWithHeader.first();

        addressDataWithHeader = addressDataWithHeader.filter( x-> !x.equals(header));

        JavaRDD<KeyValueBag> addressData = addressDataWithHeader
                .map( line -> {
                            StringJoiner stringJoiner = new StringJoiner(" ");
                            String[] parts = line.split("\t");
                            if(parts.length > 6) {
                                String valueString = stringJoiner
                                        .add(parts[1]).add(parts[2]).add(parts[3])
                                        .add(parts[4]).add(parts[5]).add(parts[6]).toString();
                                return  new KeyValueBag(parts[0],valueString);
                            }else {
                                String valueString = stringJoiner
                                        .add(parts[1]).toString();
                                return  new KeyValueBag(parts[0], valueString);
                            }

                        }
                );

        return addressData;
    }

}
