package com.wirde.myscheme.tests;

import java.io.IOException;

import com.wirde.myscheme.Environment;

public class TestInterpreter {
	public static void main(String[] args) throws IOException {
		Environment env = new Environment();
		env.evalFile("src/com/wirde/myscheme/primitives.scm");
//		env.evalFile("src/com/wirde/myscheme/tests/tests.scm", System.out);
	    env.evalFile("r4rstest.scm");
	}
}
