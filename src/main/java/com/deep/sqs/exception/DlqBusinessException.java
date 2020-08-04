package com.deep.sqs.exception;

public class DlqBusinessException extends RuntimeException {

	private static final long serialVersionUID = 1622945693270703163L;

	public DlqBusinessException() {
		super("Something went wrong :(");
	}

	public DlqBusinessException(String message) {
		super(message);
	}

}
