package com.ray.basic.exceptions;

public class DBUtilException extends RuntimeException{

	/**
	 */
	private static final long serialVersionUID = 1L;
	
	
	public DBUtilException(String message){
		super(message);
	}
	
	public DBUtilException(String message , Throwable e){
		super(message, e);
	}
	
}
