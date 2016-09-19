//package com.tangbo;
//
//import org.apache.spark.SparkConf;
//import org.apache.spark.api.java.JavaPairRDD;
//import org.apache.spark.api.java.JavaRDD;
//import org.apache.spark.api.java.JavaSparkContext;
//import org.apache.spark.streaming.Durations;
//import org.apache.spark.streaming.api.java.JavaStreamingContext;
//import scala.Tuple2;
//
//import java.util.Arrays;
//import java.util.regex.Pattern;
//
///**
// * Created by tangbo on 16/9/6.
// */
//public class JavaWordCount {
//
//	private static final Pattern SPACE = Pattern.compile(" ");
//
//	public static void main(String[] args) {
//		SparkConf conf = new SparkConf().setAppName("JavaWordCount")/*.setMaster("spark://10.0.5.56:7077")*/;
//		JavaSparkContext sc = new JavaSparkContext(conf);
////		JavaStreamingContext jssc = new JavaStreamingContext(sc, Durations.seconds(1));
//
////		JavaRDD<String> lines = sc.textFile("/usr/local/Cellar/apache-spark/2.0.0/libexec/conf/spark-defaults.conf.template");
//		JavaRDD<String> lines = sc.textFile("/home/tangbo/spark-2.0.0-bin-hadoop2.7/README.md");
//		JavaRDD<String> words = lines.flatMap(line -> Arrays.asList(line.split(" ")).iterator());
//		JavaPairRDD<String, Integer> ones = words.mapToPair(word -> new Tuple2(word, 1));
//		JavaPairRDD<String, Integer> counts = ones.reduceByKey((v1, v2) -> v1 + v2);
//		counts.collect().forEach(System.out::println);
//		sc.close();
//	}
//
//}
