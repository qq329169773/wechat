package com.ray.basic.exceptions;

public class RedisOperationException extends RuntimeException{

	/**
	 */
	private static final long serialVersionUID = 1L;


	public RedisOperationException(String message){
		super(message);
	}

	public RedisOperationException(String message , Throwable e){
		super(message, e);
	}
	
}
