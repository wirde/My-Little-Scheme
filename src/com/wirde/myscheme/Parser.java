package com.wirde.myscheme;

import java.util.StringTokenizer;

class Scanner {
	private final StringTokenizer tokenizer;
	private String originalExp;
	
	public Scanner(String exp) {
		tokenizer = new StringTokenizer(exp, " \n\t()'.", true);
		originalExp = exp;
	}
	
	public Token getNextToken() {
		if (!hasMoreTokens())
			throw new NoMoreTokensException("Error parsing expression: " + originalExp + " no more tokens.");
		String strToken = tokenizer.nextToken();
		if (strToken == null) return null;
		if (strToken.matches("\\s")) return getNextToken();		
		if (strToken.equals("(")) return new Token(TokenType.LPAREN);
		if (strToken.equals(")")) return new Token(TokenType.RPAREN);
		if (strToken.equals("#t")) return new Token(TokenType.TRUET);
		if (strToken.equals("#f")) return new Token(TokenType.FALSET);
		if (strToken.equals("'")) return new Token(TokenType.QUOTE);
		if (strToken.equals(".")) return new Token(TokenType.DOT);
		if (strToken.matches("\".*\"$")) return new StrToken(strToken.substring(1, strToken.length() - 1));
			
		//TODO: can fail..
		try {
			return new IntToken(Integer.parseInt(strToken));
		} catch (NumberFormatException e) {
			//Not an Integer
		}
		return new IdentToken(strToken);
		//return null;
	}
	
	public boolean hasMoreTokens() {
		return tokenizer.hasMoreTokens();
	}
	
	public String getOriginalExp() {
		return originalExp;
	}
}

public class Parser { 
	
	private Scanner scanner;

	public Node parseExpression(String expression) {
		scanner = new Scanner(expression);
		Node node = null;
		if (scanner.hasMoreTokens()) {
			node = parseToken(scanner.getNextToken());
		}
		return node;
	}

	//TODO: Cleanup
	private Cons parseList() {
		
		if (!scanner.hasMoreTokens())
			throw new NoMoreTokensException("Error parsing expression: " + scanner.getOriginalExp() + " no more tokens.");
		
		Token token = scanner.getNextToken();
		
		if (token.type == TokenType.RPAREN) 
			return Cons.NIL;
			
		if (token.type == TokenType.LPAREN) {
			return new Cons(parseList(), parseList());
		}
		
		return new Cons(parseToken(token), parseList());
	}
	
	private Node parseQuoted() {
		Token token = scanner.getNextToken();
		Node node;
		if (token.type == TokenType.LPAREN) {
			node = parseList();
		} else
			node = parseToken(token);
		return new Cons(new Ident("quote"), new Cons(node, Cons.NIL));
	}
	
	private Node parseToken(Token token) {
		switch (token.type) {
		case LPAREN:
			return parseList();
		case QUOTE:
			return parseQuoted();
		case INT:
			return new IntLit(((IntToken) token).intVal);
		case IDENT:
			return new Ident(((IdentToken) token).name.toLowerCase());
		case TRUET:
			return BoolLit.TRUE;
		case FALSET:
			return BoolLit.FALSE;
		case STRING:
			return new StrLit(((StrToken) token).strVal);
		}
		throw new ParseException("Unrecognized token: " + token + " when parsing expression: " + scanner.getOriginalExp());
	}
}
