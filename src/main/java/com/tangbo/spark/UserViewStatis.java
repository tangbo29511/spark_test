package com.tangbo.spark;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.hive.HiveContext;
import scala.Tuple2;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by tangbo on 16/9/19.
 */
public class UserViewStatis {

	private static Logger logger = Logger.getLogger(UserViewStatis.class);

	public static final String SEPRATOR = ",";

	public static void main(String[] args) {

		if (args.length < 4) {
			System.err.println("Usage: UserViewStatis <hdfsPath> <database> <table> <date>");
			System.exit(1);
		}
		String path = args[0];
		String database = args[1];
		String table = args[2];
		String part_key = args[3];

		SparkConf conf = new SparkConf().setAppName("uv_statis")/*.setMaster("local[4]")*/.set("spark.scheduler.pool", "production");
		JavaSparkContext sc = new JavaSparkContext(conf);

		HiveContext sqlContext = new HiveContext(sc);
		try {
			JavaRDD<Row> hiveRDD = sqlContext.sql("select * from " + database + "." + table + " where part_key in (" + part_key + ")").javaRDD();

			JavaPairRDD<String, Integer> pairRDD = hiveRDD.mapToPair(row -> {
						Long dayZero = getUvDayZero(row.getLong(5));
						Integer phoneId = row.getInt(1);
						String visitPage = row.getString(3);
						String pageId = row.getString(4);
						String sourceType = row.getString(17);
						String sourceId = row.getString(18);

						return new Tuple2<>(dayZero + SEPRATOR + phoneId + SEPRATOR + visitPage + SEPRATOR + pageId + SEPRATOR + sourceType + SEPRATOR + sourceId + SEPRATOR + "&", 1);
					}).reduceByKey((a, b) -> a + b);

			JavaPairRDD<String, Integer> result = pairRDD.mapToPair(tuple -> {
				String[] temp = tuple._1().split(SEPRATOR);
				return new Tuple2<>(temp[0] + SEPRATOR + temp[2] + SEPRATOR + temp[3] + SEPRATOR + temp[4] + SEPRATOR + temp[5], 1);
			}).reduceByKey((a, b) -> a + b);

			result.sortByKey().map(tuple2 -> tuple2._1() + SEPRATOR + tuple2._2()).saveAsTextFile("hdfs://hadoop01-dev.xish.com:9000" + path);
			result.map(tuple2 -> tuple2._1() + SEPRATOR + tuple2._2()).collect().forEach(System.out::println);
		} catch (Exception e) {
			e.printStackTrace();
		}

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
