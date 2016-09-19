//package com.tangbo;
//
//import org.apache.spark.api.java.function.FlatMapFunction;
//import org.apache.spark.api.java.function.MapFunction;
//import org.apache.spark.sql.Dataset;
//import org.apache.spark.sql.Encoders;
//import org.apache.spark.sql.Row;
//import org.apache.spark.sql.SparkSession;
//import org.apache.spark.sql.streaming.StreamingQuery;
//import org.apache.spark.sql.expressions.javalang.typed;
//
//import java.util.Arrays;
//import java.util.Iterator;
//
///**
// * Created by tangbo on 16/9/9.
// */
//public class JavaStructuredNetworkWordCount {
//
//	public static void main(String[] args) throws Exception {
//
//		String host = "localhost";
//		int port = 9999;
//
//		SparkSession spark = SparkSession
//				.builder()
//				.appName("JavaStructuredNetworkWordCount")
//				.master("local[4]")
//				.getOrCreate();
//
//		// Create DataFrame representing the stream of input lines from connection to host:port
//		Dataset<String> lines = spark
//				.readStream()
//				.format("socket")
//				.option("host", host)
//				.option("port", port)
//				.load().as(Encoders.STRING());
//
//		// Split the lines into words
//		Dataset<String> words = lines.flatMap((FlatMapFunction<String, String>) x -> Arrays.asList(x.split(" ")).iterator(), Encoders.STRING());
//
//		// Generate running word count
//		Dataset<Row> wordCounts = words.groupBy("value").count();
////		words.groupByKey((MapFunction<String, String>) word -> {return null;}, Encoders.STRING()).agg(typed.avg(value -> ));
//
//		// Start running the query that prints the running counts to the console
//		StreamingQuery query = wordCounts.writeStream()
//				.outputMode("complete")
//				.format("console")
//				.start();
//
//		query.awaitTermination();
//
//		spark.stop();
//	}
//
//}
