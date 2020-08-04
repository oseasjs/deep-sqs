package com.deep.sqs.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String messageId;

    private String groupId;

    private String content;

    private int delay;

    private boolean forceException;

    private String createdBy;

    private LocalDateTime createdAt;

    private String processedBy;

    private LocalDateTime processedAt;

}
