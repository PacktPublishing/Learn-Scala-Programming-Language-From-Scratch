package com.logistic;

import com.google.common.collect.Lists;
import com.logistic.artifacts.DuplicateBag;
import com.logistic.artifacts.KeyValueBag;
import com.logistic.artifacts.LabeledKeyValueBag;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import java.util.List;
import java.util.StringJoiner;

/**
 * Supervised Ml Simple Binary classification
 */
public class BinClassRunner {

    public static void main(String[] args) {

        SparkConf sparkConf = new SparkConf().setAppName("BinClassRunner").setMaster("local[*]");
        JavaSparkContext ctx = new JavaSparkContext(sparkConf);
        SQLContext sqlContext = new SQLContext(ctx);

        JavaRDD<String> addressDataWithHeader = ctx
               .textFile(BinClassRunner.class.getResource("/ComercialBanks10k.csv").toExternalForm());

        JavaRDD<DuplicateBag> addressData =  getDistinctValues(addressDataWithHeader);

        // Apply a schema to an RDD of Java Beans and register it as a table.
        DataFrame schemaUnStruct = sqlContext.createDataFrame(addressData, DuplicateBag.class);
        schemaUnStruct.registerTempTable("raw_data");

        DataFrame reduced_set = sqlContext.sql("SELECT id, name, billingCity FROM raw_data limit 5");
        showResults(reduced_set);

        JavaRDD<KeyValueBag> keyValueData = getReducedValues(addressDataWithHeader);

        DataFrame schemaKeyValuetruct = sqlContext.createDataFrame(keyValueData, KeyValueBag.class);
        schemaKeyValuetruct.registerTempTable("keyValueData");

        DataFrame reduced_KVSet = sqlContext.sql("SELECT key, value  FROM keyValueData limit 5");

        showResults(reduced_KVSet);

        List<LabeledKeyValueBag> localTraining = Lists.newArrayList(
                new LabeledKeyValueBag(0L, "JP Morgan", 1.0),
                new LabeledKeyValueBag(1L, "Labranche Financial", 0.0),
                new LabeledKeyValueBag(2L, "JP Morgan Invest Morgan Stanley", 1.0),
                new LabeledKeyValueBag(3L, "Piper Jaffray", 0.0));


        Tokenizer tokenizer = new Tokenizer()
                .setInputCol("value")
                .setOutputCol("words");
        HashingTF hashingTF = new HashingTF()
                .setNumFeatures(1000)
                .setInputCol(tokenizer.getOutputCol())
                .setOutputCol("features");
        LogisticRegression lr = new LogisticRegression()
                .setMaxIter(10)
                .setRegParam(0.001);
        Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[]{tokenizer, hashingTF, lr});

        DataFrame training = sqlContext.createDataFrame(ctx.parallelize(localTraining), LabeledKeyValueBag.class);

        PipelineModel model = pipeline.fit(training);

        DataFrame predictions = model.transform(reduced_KVSet);
        for (Row r: predictions.select("key", "value", "probability", "prediction").collect()) {
            System.out.println("(" + r.get(0) + ", " + r.get(1) + ") --> prob=" + r.get(2)
                    + ", prediction=" + r.get(3));
        }

    }


    static JavaRDD<KeyValueBag> getReducedValues(JavaRDD<String> addressDataWithHeader ) {

        String  header = addressDataWithHeader.first();

        addressDataWithHeader = addressDataWithHeader.filter( x-> !x.equals(header));

        JavaRDD<KeyValueBag> addressData = addressDataWithHeader
                .map( line -> {
                            String[] parts = line.split("\t");
                            StringJoiner stringJoiner = new StringJoiner(" ");
                            String valueString = stringJoiner
                                    .add(parts[1]).add(parts[2]).add(parts[3])
                                    .add(parts[4]).add(parts[5]).add(parts[6])
                                    .add(parts[7]).toString();
                            return  new KeyValueBag(parts[0],valueString);
                        }
                );

        return addressData;
    }


    static JavaRDD<DuplicateBag> getDistinctValues(JavaRDD<String> addressDataWithHeader ) {

        String  header = addressDataWithHeader.first();

        addressDataWithHeader = addressDataWithHeader.filter( x-> !x.equals(header));

        JavaRDD<DuplicateBag> addressData = addressDataWithHeader
                .map( line -> {
                            String[] parts = line.split("\t");
                            return  new DuplicateBag(parts[0], parts[1], parts[2], parts[3],
                                    parts[4], parts[5], parts[6], parts[7]);
                        }
                );

        return addressData;
    }


    static void showResults(DataFrame df) {


        List<String> reduced_Set = df.toJavaRDD().map(new Function<Row, String>() {
            @Override
            public String call(Row row) {
                return "value : " + row.getString(1);
            }
        }).collect();
        for (String valueString: reduced_Set) {
            System.out.println(valueString);
        }

    }


}
