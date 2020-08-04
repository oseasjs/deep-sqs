package com.deep.sqs.sender;

import com.deep.sqs.dto.MessageDto;
import com.deep.sqs.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.core.SqsMessageHeaders;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class MessageQueueSender {

	@Value("${cloud.aws.sqs.queue-standard}")
	private String queueStandard;
	
	@Value("${cloud.aws.sqs.queue-fifo}")
	private String queueFifo;

	@Autowired
	private QueueMessagingTemplate queueMessagingTemplate;

	@Autowired
	private MessageService messageService;

	public void sendToStandard(MessageDto dto) {
		messageService.save(dto);
		queueMessagingTemplate.convertAndSend(queueStandard, dto);
	}

	public void sendToFifo(MessageDto dto) {
		messageService.save(dto);
		queueMessagingTemplate.convertAndSend(queueFifo, dto, getHeader(dto.getGroupId(), dto.getMessageId()));
	}

	private Map<String, Object> getHeader(String groupId, String deduplicationId) {
		Map<String, Object> headers = new HashMap<>();
		headers.put(SqsMessageHeaders.SQS_GROUP_ID_HEADER, groupId);
		headers.put(SqsMessageHeaders.SQS_DEDUPLICATION_ID_HEADER, deduplicationId);
		return headers;
	}

}
