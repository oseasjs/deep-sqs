package com.deep.sqs.mapper;

import com.deep.sqs.domain.Message;
import com.deep.sqs.dto.MessageDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    public Message toDomain(MessageDto dto);

}
