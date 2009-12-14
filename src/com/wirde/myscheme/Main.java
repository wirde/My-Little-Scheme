package com.wirde.myscheme;

public class Main {

	private static final String[] expressions = {
		"(define first (cons (- 2 1) nil))",
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
//		"(cons 1 2)"
//		"(+ 1 2 (+ 1 2) (+ 2 i))",
//		"(- 7 2 1 (+ 1 2))",
//		"(print \"HELLO_WORLD\")",
//		"42",
//		"'#t",
//		"#f",
//		"\"FOO\"",
//		"(if #t \"42\" x)",
//		"(define (fac n) (if (= n 0) 1 (* n (fac (- n 1)))))",
//		"'(1 2 3)",
		};	
	
	
	public static void main(String[] args) {
		Parser parser = new Parser();
		Environment env = new Environment();
		
		for (String str : expressions) {
			Node exp = parser.parseExpression(str);
			System.out.println(exp);
			System.out.println(" -- " + exp.eval(env));
		}
	}

}
