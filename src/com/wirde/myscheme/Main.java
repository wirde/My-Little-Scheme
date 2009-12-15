package com.wirde.myscheme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
	
	
	public static void main(String[] args) throws IOException {
		Parser parser = new Parser();
		Environment env = new Environment();
		env.evalFile(parser, "src/com/wirde/myscheme/primitives.scm");
		startRepl(parser, env);
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
