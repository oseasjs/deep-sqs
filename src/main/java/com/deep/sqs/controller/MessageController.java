package com.deep.sqs.controller;

import com.deep.sqs.dto.MessageDto;
import com.deep.sqs.sender.MessageQueueSender;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/send")
public class MessageController {

	@Autowired
	private MessageQueueSender sender;

	@ApiOperation(value = "Send message to Standard Queue")
	@ApiResponse(code = 200, message = "Message sended sucessful")
	@PostMapping("/standard")
	public void sendToStandardQueue(@RequestBody MessageDto dto) {
		sender.sendToStandard(dto);
	}

	@ApiOperation(value = "Send message to FIFO queue")
	@ApiResponse(code = 200, message = "Message sended sucessful")
	@PostMapping("/fifo")
	public void sendToFifoQueue(@RequestBody MessageDto dto) {
		sender.sendToFifo(dto);
	}

	@ApiOperation(value = "Send 10 messages to Standard queue, 1 failed because exception was forced on message 05")
	@ApiResponse(code = 200, message = "Messages sended sucessful")
	@PostMapping("/standard/several")
	public void sendSeveralToStandardQueue() {

		for (int i = 1; i <= 10; i++) {

			sender.sendToStandard(MessageDto.builder()
					.messageId("" + System.nanoTime())
					.delay(i == 3 ? 15000 : 0)
					.content("Message standard | " + lp2(i))
					.forceException(i == 5 ? true : false) // Exception forced on message 05
					.build());

		}

	}

	@ApiOperation(value = "Send 10 messages to Standard queue, 1 success and 10 failed because are duplicated")
	@ApiResponse(code = 200, message = "Messages sended sucessful")
	@PostMapping("/standard/several/duplicated")
	public void sendSeveralDuplicatedToStandardQueue() throws Exception {

		MessageDto dto = MessageDto.builder()
				.messageId("" + System.nanoTime())
				.delay(5000) // 5 sec delay to allow duplication rejected messages
				.content("Message standard duplicated ")
				.build();

		sender.sendToStandard(dto);

		Thread.sleep(1000); // 1 sec delay to allow messages from some consumer before send duplicated messages
		for (int i = 1; i <= 10; i++) {
			sender.sendToStandard(dto); // Duplicated message
		}

	}

	@ApiOperation(value = "Send 10 messages to Standard queue, 2 failed messages (goes to dlq) and others proccessed successful")
	@ApiResponse(code = 200, message = "Messages sended sucessful")
	@PostMapping("/standard/several/fails")
	public void sendSeveralFailBusinessRulesToStandardQueue() throws Exception {

		for (int i = 1; i <= 10; i++) {

			// Message 02: violate business rules (messageId == null) - DLQ
			// Message 04: forces NPE - DLQ
			// Other messages: Success

			sender.sendToStandard(MessageDto.builder()
					.messageId(i == 2 ? null : "" + System.nanoTime())
					.content("Message standard fail | " + lp2(i))
					.forceException(i == 4 ? true : false)
					.build());

		}

	}

	@ApiOperation(value = "Send 30 messages to Fifo queue, keeping message order on group and processing messages of different groups on different instances" +
				"The messages 2 and 6 should have 3 sec delay, messages 4 and 8 of each group should fail")
	@ApiResponse(code = 200, message = "Messages sended sucessful")
	@PostMapping("/fifo/several")
	public void sendSeveralToFifoQueue() throws Exception {

		// Lista desordenada
		Set<String> groupIdSet = new HashSet<>();

		for (int i = 1; i < 4; i++) {
			groupIdSet.add("group_id_" + lp2(i) + "_" + System.nanoTime());
		}

		// Ordered list
		List<MessageDto> messageList = new ArrayList<>();

		for (String groupId : groupIdSet) {

			for (int i = 1; i <= 10; i++) {

				messageList.add(MessageDto.builder()
						.messageId("" + System.nanoTime())
						.groupId(groupId)
						.delay((i ==  2 || i == 6) ? 3000 : 0)
						.content("Message fifo | " + lp2(i))
						.forceException((i == 4 || i == 8) ? true : false)
						.build());

			}

		}

		for (MessageDto dto : messageList) {
			sender.sendToFifo(dto);
		}

	}

	private String lp2(long i) {
		return StringUtils.leftPad("" + i, 2, "0");
	}

}
