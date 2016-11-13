package com.ray.basic.exceptions;

public class SystemParamsInvalidateException extends RuntimeException{

	/**
	 */
	private static final long serialVersionUID = 1L;
	
	
	public SystemParamsInvalidateException(String message){
		super(message);
	}
	
	public SystemParamsInvalidateException(String message , Throwable e){
		super(message, e);
	}
	
}
