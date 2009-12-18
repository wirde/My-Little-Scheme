package com.wirde.myscheme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.wirde.myscheme.node.Node;

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
		while (true) {
			try {
				if ("".equals(line))
				       System.out.print("> ");
				String nextLine = reader.readLine();
				if (nextLine == null) {
					break;
				}
				line += "\n" + nextLine;
				Node exp = parser.parseExpression(line);
				if (exp == null)
					continue;
				System.out.println(exp.eval(env));
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
