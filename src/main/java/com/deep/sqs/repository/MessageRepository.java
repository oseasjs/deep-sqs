package com.deep.sqs.repository;

import com.deep.sqs.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    public List<Message> findByMessageId(String messageId);

}
