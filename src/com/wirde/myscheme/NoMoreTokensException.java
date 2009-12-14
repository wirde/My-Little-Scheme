package com.wirde.myscheme;


public class NoMoreTokensException extends ParseException {

	private static final long serialVersionUID = 8025795017375745507L;

	public NoMoreTokensException(String message) {
		super(message);
	}
	
}