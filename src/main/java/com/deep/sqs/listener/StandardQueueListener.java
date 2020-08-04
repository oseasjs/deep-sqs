package com.deep.sqs.listener;

import com.deep.sqs.dto.MessageDto;
import com.deep.sqs.exception.MessageLockedException;
import com.deep.sqs.service.MessageService;
import com.deep.sqs.service.RedisService;
import com.deep.sqs.service.SqsService;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StandardQueueListener {

	@Value("${cloud.aws.sqs.queue-standard}")
	private String queue;

	@Value("${cloud.aws.sqs.queue-standard-dlq}")
	private String queueDlq;

	@Autowired
	private MessageService messageService;

	@Autowired
	private SqsService sqsService;

	@Autowired
	private RedisService redisService;

	@SqsListener(value = "${cloud.aws.sqs.queue-standard}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
	public void listen(MessageDto dto) {

		log.info("Received message from Standard: {}", dto);

		try {

			// Locka message on Redis to avoid duplicity
			redisService.lock(queue, dto);

			Try.run(() -> messageService.doSomethingVeryCoolWithTheMessage(dto))
					// In case of any exception, send the message to DLQ
					.onFailure(e -> {
						log.error("{}, sending message to DLQ Standard", e.getMessage());
						sqsService.sendStandardDlq(e, queueDlq, dto);
					})
					// With success or not, remove the message from Redis
					.andFinally(() -> redisService.unlock(queue, dto));

		}
		catch (MessageLockedException e) {

			log.error("{}, sending message to DLQ FIFO", e.getMessage());
			sqsService.sendStandardDlq(e, queueDlq, dto);

		}

	}

}
