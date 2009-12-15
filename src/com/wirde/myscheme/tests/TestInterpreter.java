package com.wirde.myscheme.tests;

import java.io.IOException;

import com.wirde.myscheme.Environment;
import com.wirde.myscheme.Parser;

public class TestInterpreter {
	public static void main(String[] args) throws IOException {
		Parser parser = new Parser();
		Environment env = new Environment();
		env.evalFile(parser, "src/com/wirde/myscheme/primitives.scm");
		env.evalFile(parser, "src/com/wirde/myscheme/tests/tests.scm");
	}
}
