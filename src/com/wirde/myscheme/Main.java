package com.wirde.myscheme;

import java.io.IOException;
import java.io.InputStreamReader;

import com.wirde.myscheme.node.Node;
import com.wirde.myscheme.node.PrettyPrintVisitor;

public class Main {
	
	
	public static void main(String[] args) throws IOException {
		Environment env = new Environment();
		env.evalFile("src/com/wirde/myscheme/primitives.scm");
		startRepl(env);
	}

	private static void startRepl(Environment env) {
		Parser parser = new Parser(new InputStreamReader(System.in));
		while (true) {
			try {
				System.out.print("> ");
				Node exp = parser.parseNext();
				if (exp == null)
					break;
				exp.eval(env).accept(new PrettyPrintVisitor(System.out));
				System.out.println();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (RuntimeException e) {
				e.printStackTrace();				
			}
		}
	}

}
