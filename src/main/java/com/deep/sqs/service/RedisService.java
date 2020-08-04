package com.deep.sqs.service;

import com.deep.sqs.dto.MessageDto;
import com.deep.sqs.exception.MessageLockedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
public class RedisService {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	private SetOperations<String, String> operations;
	
	@PostConstruct
	public void postConstruct() {
		operations = redisTemplate.opsForSet();
	}

	public void lock(String queue, MessageDto dto) throws MessageLockedException {

		String hash = getHash(dto);

		/**
		 * Try to add the key on Redis:
		 *
		 * a) If key do not exists on Redis, operations.add == 1
		 * b) If key already exists on Redis, operations.add == 0
		 *
		 * Reference: https://redis.io/commands/sadd
		 */

		if (operations.add(queue, hash) == 0) {
			throw new MessageLockedException();
		}

	}
	
	public void unlock(String queue, MessageDto dto) {

		String hash = getHash(dto);
		operations.remove(queue, hash);
		
	}

	private String getHash(MessageDto dto) {

		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			String stringJson = mapper.writeValueAsString(dto);
			return new String(algorithm.digest(stringJson.getBytes(StandardCharsets.UTF_8)));
		}
		catch (JsonProcessingException | NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

	}

}
