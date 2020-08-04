package com.deep.sqs.exception;

public class MessageLockedException extends Throwable {

	private static final long serialVersionUID = 639252489223722934L;

	public MessageLockedException() {
		super("Mensagem já está sendo processada.");
	}

}
