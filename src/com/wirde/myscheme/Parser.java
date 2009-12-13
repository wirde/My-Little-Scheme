package com.wirde.myscheme;

import java.util.StringTokenizer;

class Scanner {
	private final StringTokenizer tokenizer;
	
	public Scanner(String code) {
		tokenizer = new StringTokenizer(code, " \n\t()'.", true);
	}
	
	public Token getNextToken() {
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
}

public class Parser { 
	
	private Scanner scanner;

	public Node parseExpression(String expression) {
		scanner = new Scanner(expression.toLowerCase());
		Node node = null;
		if (scanner.hasMoreTokens()) {
			node = parseToken(scanner.getNextToken());
		}
		return node;
	}

	//TODO: Cleanup
	private Cons parseList() {
		
		if (!scanner.hasMoreTokens())
			throw new ParseException("Ran out of tokens");
		
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
		if (token.type == TokenType.LPAREN) {
			return parseList();
		} 
		if (token.type == TokenType.QUOTE) {
			return parseQuoted();
		}		
		if (token.type == TokenType.INT) {
			return new IntLit(((IntToken) token).intVal);
		} 
		if (token.type == TokenType.IDENT) {
			return new Ident(((IdentToken) token).name);
		}
		if (token.type == TokenType.TRUET) {
			return BoolLit.TRUE;
		}
		if (token.type == TokenType.FALSET) {
			return BoolLit.FALSE;
		}
		if (token.type == TokenType.STRING) {
			return new StrLit(((StrToken) token).strVal);
		}
		throw new ParseException("Unrecognized token: " + token);
	}
}
