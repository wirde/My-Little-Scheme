package com.wirde.myscheme;

import java.math.BigInteger;

enum TokenType {
	LPAREN,
	RPAREN,
	INT,
	IDENT,
	TRUET,
	FALSET,
	STRING,
	CHAR,
	QUOTE,
	DOT
}

//TODO: member variables --> private?


class Token {
	final TokenType type;
	
	public Token(TokenType type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return type.toString();
	}
}

class CharToken extends Token {

    final char charact;

    public CharToken(char charact) {
        super(TokenType.CHAR);
        this.charact = charact;
    }
    
}
class StrToken extends Token {
	final String strVal;
	
	public StrToken(String strVal) {
		super(TokenType.STRING);
		this.strVal = strVal;
	}
	
	@Override
	public String toString() {
		return strVal;
	}	
}

class IntToken extends Token {
	final BigInteger intVal;
	
	public IntToken(BigInteger intVal) {
		super(TokenType.INT);
		this.intVal = intVal;
	}
	
	@Override
	public String toString() {
		return intVal.toString();
	}	
}

class IdentToken extends Token {
	final String name;
	
	public IdentToken(String name) {
		super(TokenType.IDENT);
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}	
}