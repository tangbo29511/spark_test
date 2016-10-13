package com.tangbo.spark;

import com.esotericsoftware.kryo.Kryo;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

/**
 * Created by tangbo on 16/9/19.
 */
public class UserViewStatis {

	static String convertScanToString(Scan scan) throws IOException {
		ClientProtos.Scan proto = ProtobufUtil.toScan(scan);
		return Base64.encodeBytes(proto.toByteArray());
	}

	public static void main(String[] args) {

		if (args.length < 2) {
			System.err.println("Usage: UserViewStatis <tablename> <family>");
			System.exit(1);
		}
		String table = args[0];
		String family = args[1];

		SparkConf conf = new SparkConf().setAppName("uv_statis")/*.setMaster("local[4]")*/.set("spark.scheduler.pool", "production");
		JavaSparkContext sc = new JavaSparkContext(conf);

		Configuration hConf = HBaseConfiguration.create();
		Scan scan = new Scan();
		scan.addFamily(Bytes.toBytes(family));
		scan.setStartRow(Bytes.toBytes("0"));
		try {
			hConf.set(TableInputFormat.INPUT_TABLE, table);
			hConf.set(TableInputFormat.SCAN, convertScanToString(scan));
			JavaPairRDD<ImmutableBytesWritable, Result> hbaseRDD =
					sc.newAPIHadoopRDD(hConf,  TableInputFormat.class,
							ImmutableBytesWritable.class, Result.class);

			/*hbaseRDD.map(immutableBytesWritableResultTuple2 -> immutableBytesWritableResultTuple2._2()).collect().forEach(result -> {
				System.out.println(new String(result.getValue(Bytes.toBytes(family), Bytes.toBytes("visit_time"))));
			});*/
			JavaPairRDD<String, Integer> pairRDD = hbaseRDD.map(immutableBytesWritableResultTuple2 -> immutableBytesWritableResultTuple2._2())
					.mapToPair(result -> {
						Long dayZero = getUvDayZero(Long.valueOf(new String(result.getValue(Bytes.toBytes(family), Bytes.toBytes("visit_time")))));
						String phoneId = new String(result.getValue(Bytes.toBytes(family), Bytes.toBytes("phone_id")));
						String visitPage = new String(result.getValue(Bytes.toBytes(family), Bytes.toBytes("visit_page")));
						String pageId = new String(result.getValue(Bytes.toBytes(family), Bytes.toBytes("page_id")));
						String sourceType = new String(result.getValue(Bytes.toBytes(family), Bytes.toBytes("source_type")));
						String sourceId = new String(result.getValue(Bytes.toBytes(family), Bytes.toBytes("source_id")));

						return new Tuple2<>(dayZero + "," + phoneId + "," + visitPage + "," + pageId + ",_"+ sourceType + "," + sourceId + ",", 1);
					}).reduceByKey((a, b) -> a + b);

			JavaPairRDD<String, Integer> result = pairRDD.mapToPair(tuple -> {
				String[] temp = tuple._1().split(",");
				return new Tuple2<>(temp[0] + "," + temp[2] + "," + temp[3] + "," + temp[4] + "," + temp[5], 1);
			}).reduceByKey((a, b) -> a + b);

			result.collect().forEach(System.out::println);
			System.out.println(result.collect().size());
//			hbaseRDD.foreachPartition(tuple2Iterator -> {
//				while (tuple2Iterator.hasNext()) {
//					Tuple2<ImmutableBytesWritable, Result> next = tuple2Iterator.next();
//					System.out.println(new String(next._1().copyBytes()));
//					System.out.println(new String(Optional.ofNullable(next._2().getValue(Bytes.toBytes("f"), Bytes.toBytes("visit_page"))).orElse(new byte[0])));
//				}
//			});

		} catch (IOException e) {
			e.printStackTrace();
		}

		/*JavaRDD<String> dataVisits = sc.textFile("/home/hadoop/spark-2.0.0-bin-hadoop2.7/examples/jars/query_result.csv");
		JavaPairRDD<String, Integer> pairRDD = dataVisits.map(s -> s.split(",")).filter(data_visit ->
				!"id".equals(data_visit[0])
		).mapToPair(data_visit -> {
			Long dayZero = getUvDayZero(Long.valueOf(data_visit[5]));
			String phoneId = data_visit[1];
			String visitPage = data_visit[3];
			String pageId = data_visit[4];
//			String sourceType = data_visit[17];
//			String sourceId = data_visit[18];

			return new Tuple2<>(dayZero + "," + phoneId + "," + visitPage + "," + pageId + ",_"+ sourceType + "," + sourceId + ",", 1);
		}).reduceByKey((a, b) -> a + b);

		System.out.println(pairRDD.collect().size());
		pairRDD.collect().forEach(System.out::println);

		JavaPairRDD<String, Integer> result = pairRDD.mapToPair(tuple -> {
			String[] temp = tuple._1().split(",");
			return new Tuple2<>(temp[0] + "," + temp[2] + "," + temp[3], 1);
		}).reduceByKey((a, b) -> a + b);

		System.out.println(result.collect().size());
		result.collect().forEach(System.out::println);*/
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
