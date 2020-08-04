package com.deep.sqs.dto;

import lombok.Data;
import lombok.Generated;

import java.time.LocalDateTime;

@Data
@Generated
public class DLQResponseDto {

	String errorType;
	String error;
	LocalDateTime createdAt;
	Object body;
	
	public DLQResponseDto(final Throwable ex, final Object body) {
		this.errorType = ex.getClass().getSimpleName();
		this.error = ex.getMessage();
		this.body = body;
		this.createdAt = LocalDateTime.now();
	}

}
