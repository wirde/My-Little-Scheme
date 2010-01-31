package com.wirde.myscheme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wirde.myscheme.node.BoolLit;
import com.wirde.myscheme.node.Cons;
import com.wirde.myscheme.node.Ident;
import com.wirde.myscheme.node.IntLit;
import com.wirde.myscheme.node.Node;
import com.wirde.myscheme.node.StrLit;

class Scanner {
	private final BufferedReader reader;
	private String currentLine;
	
	private static final Pattern identPattern = Pattern.compile("^[a-zA-Z0-9+-?\\*?/!_]+");
	private static final Pattern intPattern = Pattern.compile("^-?[0-9]+");
	private static final Pattern strPattern = Pattern.compile("^\".*?\"", Pattern.DOTALL);
	
	public Scanner(String exp) {
		this(new StringReader(exp));
	}

	public Scanner(Reader in) {
		reader = new BufferedReader(in);
	}

	public Token getNextToken() throws IOException {
		if (blank(currentLine))
			getNextLine();
		if (currentLine == null)
			return null;
		currentLine = currentLine.replaceFirst("\\s*", "");
		char firstChar = currentLine.charAt(0);
		if (firstChar == '(') {
			consumeChars(1);
			return new Token(TokenType.LPAREN);
		}
		if (firstChar == ')') {
			consumeChars(1);
			return new Token(TokenType.RPAREN);
		}
		if (firstChar == '#')
			return readBoolToken();
		if (firstChar == '\'') {
			consumeChars(1);
			return new Token(TokenType.QUOTE);
		}
		if (firstChar == '.') {
			consumeChars(1);
			return new Token(TokenType.DOT);
		}

		if (intPattern.matcher(currentLine).find()) 
			return readIntToken();
		
		if (strPattern.matcher(currentLine).find()) 
			return readStrToken();
		
		if (identPattern.matcher(currentLine).find())
			return readIdentToken();
		
		//Handle multiline strings, probably breaks for weird input
		currentLine += "\n" + reader.readLine();
		return getNextToken();
	}

	private void consumeChars(int nrChars) {
		currentLine = currentLine.substring(nrChars, currentLine.length());		
	}

	//TODO: extract common code
	
	private Token readStrToken() {
		Matcher strMatcher = strPattern.matcher(currentLine);
		if (strMatcher.find()) {
			String str = strMatcher.group();
			consumeChars(str.length());
			return new StrToken(str.substring(1, str.length() - 1));
		} else 
			throw new ParseException("Failed to read identifier. Context: " + currentLine);
	}

	private Token readIdentToken() {
		Matcher identMatcher = identPattern.matcher(currentLine);
		if (identMatcher.find()) {
			String ident = identMatcher.group();
			consumeChars(ident.length());
			return new IdentToken(ident);
		} else 
			throw new ParseException("Failed to read identifier. Context: " + currentLine);
	}

	private Token readIntToken() {
		Matcher intMatcher = intPattern.matcher(currentLine);
		if (intMatcher.find()) {
			String number = intMatcher.group();
			consumeChars(number.length());
			return new IntToken(new BigInteger(number));
		} else 
			throw new ParseException("Failed to read int. Context: " + currentLine);
	}

	private Token readBoolToken() {
		if (currentLine.startsWith("#t")) {
			consumeChars(2);
			return new Token(TokenType.TRUET);
		} else if (currentLine.startsWith("#f")) {
			consumeChars(2);
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
}

public class Parser {
	private Scanner scanner;

	public Parser(Reader reader) {
		scanner = new Scanner(reader);
	}

	public Node parseNext() throws IOException {
		return parseToken(scanner.getNextToken());
	}

	// TODO: Cleanup
	private Node parseList() throws IOException {
		Token token = scanner.getNextToken();
		if (token.type == TokenType.RPAREN)
			return Cons.NIL;
		if (token.type == TokenType.LPAREN) {
			return new Cons(parseList(), parseList());
		}
		if (token.type == TokenType.DOT) {
			Node nextNode = parseToken(scanner.getNextToken());
			if (scanner.getNextToken().type != TokenType.RPAREN)
				throw new ParseException("Expected right paren");
			return nextNode;
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
		if (token == null)
			return null;
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
