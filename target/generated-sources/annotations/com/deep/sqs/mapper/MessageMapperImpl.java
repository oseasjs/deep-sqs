package com.deep.sqs.mapper;

import com.deep.sqs.domain.Message;
import com.deep.sqs.dto.MessageDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-08-03T23:53:41-0300",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.6 (Amazon.com Inc.)"
)
@Component
public class MessageMapperImpl implements MessageMapper {

    @Override
    public Message toDomain(MessageDto dto) {
        if ( dto == null ) {
            return null;
        }

        Message message = new Message();

        message.setMessageId( dto.getMessageId() );
        message.setGroupId( dto.getGroupId() );
        message.setContent( dto.getContent() );
        message.setDelay( dto.getDelay() );
        message.setForceException( dto.isForceException() );

        return message;
    }
}
