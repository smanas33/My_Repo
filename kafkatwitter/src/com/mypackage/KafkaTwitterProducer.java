package com.mypackage;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import twitter4j.FilterQuery;
import twitter4j.HashtagEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.URLEntity;
import twitter4j.conf.ConfigurationBuilder;

public class KafkaTwitterProducer {

	public static void main(String[] args) {
		LinkedBlockingQueue<Status> queue = new LinkedBlockingQueue<>(1000);

		if (args.length < 5) {
			System.out.println("Usage: KafkaTwitterProducer " + "<twitter-consumer-key>" + "<twitter-consumer-secret>"
					+ "<twitter-access-token>" + "<twitter-access-token-secret>" + "<topic-name>"
					+ "<twitter-search-keywords>");
			return;
		}

		String consumerKey = args[0].toString();
		String consumerSecret = args[1].toString();
		String accessToken = args[2].toString();
		String accessTokenSecret = args[3].toString();
		String topicName = args[4].toString();
		String[] arguments = args.clone();
		String[] keyWords = Arrays.copyOfRange(arguments, 5, arguments.length);

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret)
				.setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(accessTokenSecret);

		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		StatusListener listener = new StatusListener() {

			@Override
			public void onException(Exception exception) {
				System.out.println(exception);
			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
			}

			@Override
			public void onStatus(Status status) {
				queue.offer(status);

				System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
				for (URLEntity entity : status.getURLEntities()) {
					System.out.println(entity.getDisplayURL());
				}
			}

			@Override
			public void onStallWarning(StallWarning warning) {
				System.out.println("Got stall warning:" + warning);
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				System.out.println("Got scrub_geo event userId:" + userId + "upToStatusId:" + upToStatusId);
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice deletionNotice) {
				System.out.println("Got a status deletion notice id: " + deletionNotice.getStatusId());
			}
		};

		twitterStream.addListener(listener);

		FilterQuery filterQuery = new FilterQuery().track(keyWords);
		twitterStream.filter(filterQuery);

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Add Kafka producer config settings
		Properties properties = new Properties();
		properties.put("bootstrap.servers", "localhost:9092");
		properties.put("acks", "all");
		properties.put("retries", 0);
		properties.put("batch.size", 16384);
		properties.put("linger.ms", 1);
		properties.put("buffer.memory", 33554432);

		properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		Producer<String, String> producer = new KafkaProducer<String, String>(properties);
		int i = 0;
		int j = 0;

		while (i < 10) {
			Status ret = queue.poll();

			if (ret == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				i++;
			} else {
				for (HashtagEntity hashtagEntity : ret.getHashtagEntities()) {
					System.out.println("Hashtag: " + hashtagEntity.getText());
					producer.send(new ProducerRecord<String, String>(topicName, Integer.toString(j++),
							hashtagEntity.getText()));
				}
			}
		}
		producer.close();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		twitterStream.shutdown();
	}

}
