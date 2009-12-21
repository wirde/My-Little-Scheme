package com.wirde.myscheme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.wirde.myscheme.node.BoolLit;
import com.wirde.myscheme.node.Cons;
import com.wirde.myscheme.node.Ident;
import com.wirde.myscheme.node.IntLit;
import com.wirde.myscheme.node.Node;
import com.wirde.myscheme.node.StrLit;

class Scanner {
	private final BufferedReader reader;
	private String currentLine;
	
	public Scanner(String exp) {
		reader = new BufferedReader(new StringReader(exp));
	}
	
	public Scanner(Reader in) {
		reader = new BufferedReader(in);
	}
	
	public Token getNextToken() throws IOException {
		if (blank(currentLine))
			getNextLine();
		if (!hasMoreTokens())
			throw new NoMoreTokensException("Error parsing expression, no more tokens.");
		//Remove leading whitespace
		currentLine = currentLine.replaceFirst("\\s*", "");
		char firstChar = currentLine.charAt(0);
		if (firstChar == '(') {
			currentLine = currentLine.substring(1, currentLine.length());
			return new Token(TokenType.LPAREN);
		}
		if (firstChar == ')') {
			currentLine = currentLine.substring(1, currentLine.length());
			return new Token(TokenType.RPAREN);
		}
		if (firstChar == '#') return readBoolToken();
		if (firstChar == '\'') {
			currentLine = currentLine.substring(1, currentLine.length());
			return new Token(TokenType.QUOTE);
		}
		if (firstChar == '.') {
			currentLine = currentLine.substring(1, currentLine.length());
			return new Token(TokenType.DOT);
		}
		//TODO: get strings back in
//		if (strToken.matches("\".*\"$")) return new StrToken(strToken.substring(1, strToken.length() - 1));
			
		//TODO: can fail..
		try {
			return readIntToken(); 
		} catch (NumberFormatException e) {
			//Not an Integer
		}
		return readIdentToken();
	}
	
	private Token readIdentToken() {
		String ident = "";
		while (currentLine.matches("^[a-zA-Z0-9+-?\\*?/!].*")) {
			ident += currentLine.charAt(0);
			currentLine = currentLine.substring(1, currentLine.length());
		}
		if (ident.equals(""))
			throw new ParseException("Failed to read identifier. Context: " + currentLine);
		return new IdentToken(ident);
	}

	private Token readIntToken() {
		String number = "";
		while (currentLine.matches("^[0-9].*")) {
			number += currentLine.charAt(0);
			currentLine = currentLine.substring(1, currentLine.length());
		}
		if (number.equals("") || (!currentLine.matches("^[()\\s].*") && !currentLine.equals("")))
			throw new NumberFormatException();
		return new IntToken(Integer.parseInt(number));
	}

	private Token readBoolToken() {
		if (currentLine.startsWith("#t")) {
			currentLine = currentLine.substring(2, currentLine.length());
			return new Token(TokenType.TRUET);
		} else if (currentLine.startsWith("#f")) {
			currentLine = currentLine.substring(2, currentLine.length());
			return new Token(TokenType.FALSET);
		} else
			throw new ParseException("Expected #t or #f, in context: " + currentLine);
	}

	private boolean blank(String line) {
		return line == null || line.matches("^\\s*;;.*") || line.matches("^\\s*$");
	}

	private void getNextLine() throws IOException {
		while (blank(currentLine)) {
			currentLine = reader.readLine();
			if (currentLine == null)
				break;
		}
	}

	public boolean hasMoreTokens() throws IOException {
		if (!blank(currentLine))
			return true;
		reader.mark(1);
		boolean endOfStream = -1 == reader.read();
		reader.reset();
		return !endOfStream;
	}
}

public class Parser { 
	
	private Scanner scanner;

	public Parser(Reader reader) {
		scanner = new Scanner(reader);
	}
	
	public Node parseNext() throws IOException {
		Node node = null;
		if (scanner.hasMoreTokens()) {
			node = parseToken(scanner.getNextToken());
		}
		return node;
	}

	//TODO: Cleanup
	private Cons parseList() throws IOException {
		
		if (!scanner.hasMoreTokens())
			throw new NoMoreTokensException("Error parsing expression, no more tokens.");
		Token token = scanner.getNextToken();
		
		if (token.type == TokenType.RPAREN) 
			return Cons.NIL;
			
		if (token.type == TokenType.LPAREN) {
			return new Cons(parseList(), parseList());
		}
		
		return new Cons(parseToken(token), parseList());
	}
	
	private Node parseQuoted() throws IOException {
		Token token = scanner.getNextToken();
		Node node;
		if (token.type == TokenType.LPAREN) {
			node = parseList();
		} else
			node = parseToken(token);
		return new Cons(new Ident("quote"), new Cons(node, Cons.NIL));
	}
	
	private Node parseToken(Token token) throws IOException {
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
		throw new ParseException("Unrecognized token: " + token + " when parsing expression.");
	}
}
