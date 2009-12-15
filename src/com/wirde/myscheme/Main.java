package com.wirde.myscheme;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

	private static final String[] expressions = {
		"(DEFINE first (cons (- 2 1) nil))",
		"(cons 2 first)",
		"(define three (cons 3 (cons 2 first)))",
		"three",
		"(car three)",
		"(cdr three)",
		"(car (cdr three))",
		"(= 1 0)",
		"(= 1 1)",
		"(= #t #f)",
		"(= #t #t)",
		"(= \"str\" \"str\")",
		"(= + +)",
		"(define x 1)",
		"(define f (lambda (x) (+ 1 x)))",
		"(f (+ 1 2))",
		"(if #f (print \"true\") (print \"false\"))",
		"(+ 1 2 (+ 1 2) (- 1 2))",
		"(* 7 7 2)",
		"(cons 1 2)",
		"(- 7 2 1 (+ 1 2))",
		"(print \"HELLO_WORLD\")",
//		"(print \"HELLO WORLD\")",
		"42",
//		"'#t",
		"#f",
		"\"FOO\"",
		"(if #t \"42\" x)",
		"(define fac (lambda (n) (if (= n 0) 1 (* n (fac (- n 1))))))",
		"(fac 4)"
//		"'(1 2 3)",
		};	
	
	
	public static void main(String[] args) throws IOException {
		Parser parser = new Parser();
		Environment env = new Environment();
		
		readPrimitives(parser, env, "src/com/wirde/myscheme/primitives.scm");
		
		for (String str : expressions) {
			Node exp = parser.parseExpression(str);
			System.out.println(exp);
			System.out.println(" -- " + exp.eval(env));
		}
		
		startRepl(parser, env);
	}


	private static void readPrimitives(Parser parser, Environment env, String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = "";
		while (line != null) {
			line = reader.readLine();
			if ("".equals(line) || line == null)
				continue;
			parser.parseExpression(line).eval(env);
		}
	}


	private static void startRepl(Parser parser, Environment env) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		while (line != null) {
			try {
				System.out.print("> ");
				String nextLine = reader.readLine();
				if ("".equals(nextLine) || nextLine == null) {
					line = null;
					continue;
				}
				line += nextLine;
				Node result = parser.parseExpression(line);
				System.out.println(result.eval(env));
			} catch (NoMoreTokensException e) {
				continue;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (RuntimeException e) {
				e.printStackTrace();				
			}
			line = "";
		}
	}

}
