package com.deep.sqs.service;

import com.deep.sqs.dto.DLQResponseDto;
import com.deep.sqs.dto.MessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.core.SqsMessageHeaders;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SqsService {

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
		queueMessagingTemplate.convertAndSend(queueStandard, dto, getHeaderWithId());
	}

	public void sendToFifo(MessageDto dto) {
		messageService.save(dto);
		queueMessagingTemplate.convertAndSend(queueFifo, dto, getHeader(dto.getGroupId(), dto.getMessageId()));
	}

	public void sendStandardDlq(Throwable ex, String queueDlq, MessageDto dto) {
		queueMessagingTemplate.convertAndSend(queueDlq, new DLQResponseDto(ex, dto), getHeaderWithId());
	}

	public void sendFifoDlq(Throwable ex, String queueDlq, MessageDto dto) {
		queueMessagingTemplate.convertAndSend(queueDlq, new DLQResponseDto(ex, dto),
				getHeader(dto.getGroupId(), dto.getMessageId()));
	}

	private Map<String, Object> getHeader(String groupId, String deduplicationId) {
		Map<String, Object> headers = getHeaderWithId();
		headers.put(SqsMessageHeaders.SQS_GROUP_ID_HEADER, groupId);
		headers.put(SqsMessageHeaders.SQS_DEDUPLICATION_ID_HEADER, deduplicationId);
		return headers;
	}

	private Map<String, Object> getHeaderWithId() {
		Map<String, Object> headers = new HashMap<>();
		headers.put(SqsMessageHeaders.ID, UUID.randomUUID());
		return headers;
	}

}
