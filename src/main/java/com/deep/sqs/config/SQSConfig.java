package com.deep.sqs.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@Primary
public class SQSConfig {

	@Value("${cloud.aws.region.static}")
	String awsRegion;

	@Value("${localstack.url.sqs}")
	String localStackUrlSqs;

	@Value("${cloud.aws.sqs.queue-fifo}")
	String fifoQueue;

	@Value("${cloud.aws.sqs.queue-fifo-dlq}")
	String fifoDlqQueue;

	@Value("${cloud.aws.sqs.queue-standard}")
	String standardQueue;

	@Value("${cloud.aws.sqs.queue-standard-dlq}")
	String standardDlqQueue;

	@Bean
	@Primary
	public AmazonSQSAsync amazonSQSAsync() {

		AmazonSQSAsync amazonSQSAsync = AmazonSQSAsyncClientBuilder.standard()
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(localStackUrlSqs, awsRegion))
				.build();

		createQueues(amazonSQSAsync);

		return amazonSQSAsync;

	}

	@Bean
	public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync) {
		return new QueueMessagingTemplate(amazonSQSAsync());
	}

	@Bean
	public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSqs,
																					   QueueMessagingTemplate queueMessagingTemplate) {
		SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
		factory.setAmazonSqs(amazonSqs);
		factory.setAutoStartup(true);

		// Setting this property to allow process only one message per time to keep the order of FIFO message queue
		factory.setMaxNumberOfMessages(1);

		return factory;

	}

	private void createQueues(AmazonSQSAsync amazonSQSAsync) {
		List<String> existingQueueList = amazonSQSAsync.listQueues().getQueueUrls();
		List<String> queueList = Arrays.asList(
				fifoQueue, fifoDlqQueue,
				standardQueue, standardDlqQueue);

		queueList.stream()
				.filter(queueName -> existingQueueList
						.stream()
						.filter(existingQueue -> existingQueue.contains(queueName))
						.findAny()
						.isEmpty())
				.forEach(queueName -> {
					log.info("### Creating required queue: {}", queueName);
					crateFifoQueue(amazonSQSAsync, queueName);
				});

	}

	private void crateFifoQueue(AmazonSQSAsync amazonSQSAsync, final String queueName) {

		final Map<String, String> attributes = new HashMap<>();

		if (queueName.contains("fifo")) {
			attributes.put("FifoQueue", "true");
			attributes.put("ContentBasedDeduplication", "true");
		}

		amazonSQSAsync
				.createQueue(new CreateQueueRequest(queueName)
						.withAttributes(attributes))
				.getQueueUrl();

	}

}
