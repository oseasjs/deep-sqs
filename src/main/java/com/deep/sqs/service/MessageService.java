package com.deep.sqs.service;

import com.deep.sqs.domain.Message;
import com.deep.sqs.dto.MessageDto;
import com.deep.sqs.exception.DlqBusinessException;
import com.deep.sqs.mapper.MessageMapper;
import com.deep.sqs.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
public class MessageService {

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private MessageMapper messageMapper;

	@Autowired
	private Environment environment;

	@Transactional
	public void doSomethingVeryCoolWithTheMessage(MessageDto dto) throws Exception {

		if (dto.getDelay() > 0) {
			Thread.sleep(dto.getDelay());
		}

		if (dto.isForceException()) {
			throw new RuntimeException("Forced Exception");
		}

		if (StringUtils.isEmpty(dto.getMessageId())) {
			throw new DlqBusinessException("messageId is required");
		}

		// Save message on DB
		Message message = messageRepository.findByMessageId(dto.getMessageId()).get(0);
		message.setProcessedBy(instancePort());
		message.setProcessedAt(LocalDateTime.now());
		message.setContent(message.getContent());
		messageRepository.save(message);

	}

	@Transactional
	public void save(MessageDto dto) {

		// Save
		Message message = messageMapper.toDomain(dto);
		message.setCreatedBy(instancePort());
		message.setCreatedAt(LocalDateTime.now());
		message.setContent(message.getContent());
		messageRepository.save(message);

	}

	private String instancePort() {
		return environment.getProperty("local.server.port");
	}

}
