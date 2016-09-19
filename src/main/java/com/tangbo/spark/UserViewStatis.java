package com.tangbo.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tangbo on 16/9/19.
 */
public class UserViewStatis {

	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setAppName("uv").setMaster("local[4]");
		JavaSparkContext sc = new JavaSparkContext(conf);

		JavaRDD<String> dataVisits = sc.textFile("/Users/tangbo/Desktop/query_result.csv");
		JavaPairRDD<String, Integer> pairRDD = dataVisits.map(s -> s.split(",")).filter(data_visit ->
				!"id".equals(data_visit[0])
		).mapToPair(data_visit -> {
			Long dayZero = getUvDayZero(Long.valueOf(data_visit[5]));
			String phoneId = data_visit[1];
			String visitPage = data_visit[3];
			String pageId = data_visit[4];
//			String sourceType = data_visit[17];
//			String sourceId = data_visit[18];

			return new Tuple2<>(dayZero + "," + phoneId + "," + visitPage + "," + pageId + ",_"/*+ sourceType + "," + sourceId + ","*/, 1);
		}).reduceByKey((a, b) -> a + b);

		System.out.println(pairRDD.collect().size());
		pairRDD.collect().forEach(System.out::println);

		JavaPairRDD<String, Integer> result = pairRDD.mapToPair(tuple -> {
			String[] temp = tuple._1().split(",");
			return new Tuple2<>(temp[0] + "," + temp[2] + "," + temp[3], 1);
		}).reduceByKey((a, b) -> a + b);

		System.out.println(result.collect().size());
		result.collect().forEach(System.out::println);
	}

	public static long getUvDayZero(long t) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(t));
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR_OF_DAY, 0);
		return c.getTimeInMillis() / 1000;
	}

}
