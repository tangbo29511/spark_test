package com.tangbo.spark;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import com.tangbo.vo.DataPhoneMqMessage;
import com.tangbo.vo.DataVisitVo;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.apache.spark.streaming.rabbitmq.RabbitMQUtils;
import scala.Tuple2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * Created by tangbo on 16/9/12.
 */
public class JavaRabbitMqReceiver {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static void main(String[] args) throws InterruptedException, IOException, TimeoutException, NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
		SparkConf sparkConf = new SparkConf().setAppName("JavaRabbitMq")/*.setMaster("local[20]")*/;

		JavaStreamingContext ssc = new JavaStreamingContext(sparkConf, Durations.seconds(60));

		List<JavaDStream<String>> rabbitMqStreams = new ArrayList<>();
		for (int i = 0; i < 16; i++) {
			String queueName = "wzm_data_phone" + i;
			rabbitMqStreams.add(
				RabbitMQUtils.createJavaStream(ssc, String.class,
					new HashMap(){
						{
							put("queueName", queueName);
							put("hosts", "localhost");
							put("userName", "guest");
							put("password", "guest");
						}
					}, String::new));
		}
		JavaDStream<String> rabbitMqDStream = ssc.union(rabbitMqStreams.get(0), rabbitMqStreams.subList(1, rabbitMqStreams.size()));
//		rabbitMqDStream.dstream().saveAsTextFiles("hdfs://10.0.5.56:8020/user/spark/rabbitmq/", "hadoop");

		JavaDStream<String> dataVisits = rabbitMqDStream.filter(message -> {
			try {
				DataPhoneMqMessage dataPhoneMqMessage = objectMapper.readValue(message, DataPhoneMqMessage.class);
				return "visit".equals(dataPhoneMqMessage.getType());
			} catch (Exception e) {
				e.printStackTrace();
				//TODO
				return false;
			}
		});

//		dataVisits.foreachRDD((Function2<JavaRDD<String>, Time, Void>) (rdd, time) -> {
//			SQLContext sqlContext = new SQLContext(ssc.sparkContext());
//			DataFrame schemaDataVisit = sqlContext.createDataFrame(rdd, DataVisitVo.class);
//			schemaDataVisit.registerTempTable("data_visit");
//			DataFrame test = sqlContext.sql("select * from data_visit");
//			test.javaRDD().map(row -> {
//				System.out.println(row);
//				return row;
//			}).saveAsTextFile("hdfs://10.0.5.56:8020/user/spark/rabbitmq/");
//			return null;
//		});

		rabbitMqDStream.map(str -> {
			return str;
		}).filter(s -> true)
				.print();

		ssc.start();
		ssc.awaitTermination();

//		ConnectionFactory factory = new ConnectionFactory();
//		factory.setUri("amqp://guest:guest@127.0.0.1:5672");
//		Connection connection = factory.newConnection();
//
//		factory.setAutomaticRecoveryEnabled(true);
//		factory.setNetworkRecoveryInterval(10000);
//
//		Channel channel = connection.createChannel();
//		channel.basicConsume("wzm_data_phone6", false, "tangbo_consumer_tag", new DefaultConsumer(channel) {
//			@Override
//			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//				System.out.println(new String(body));
//				channel.basicAck(envelope.getDeliveryTag(), false);
//			}
//		});
	}

}
