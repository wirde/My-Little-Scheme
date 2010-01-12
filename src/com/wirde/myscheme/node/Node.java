package com.wirde.myscheme.node;

import com.wirde.myscheme.Environment;

public abstract class Node {	

    public abstract Node eval(Environment env);
    
	protected abstract String print(int position);
	
	@Override
	public String toString() {
		return print(0);
	}
	
	protected String getIndent(int position) {
		String indent = "";
		for(int i = 0; i < position; i++) {
			indent += " ";
		}
		return indent;
	}
}


abstract class Literal extends Node {
	@Override
	public Node eval(Environment env) {
		return this;
	}
}

/*package private*/ enum SpecialForm {
	REGULAR, DEFINE, IF, QUOTED, LAMBDA, SET, BEGIN;

	public static SpecialForm toSpecialForm(Cons cons) {
		Node first = cons.getFirst();
		if (!(first instanceof Ident))
			return REGULAR;
		String name = ((Ident) first).getName();
		if ("define".equals(name))
			return DEFINE;
		if ("if".equals(name))
			return IF;
		if ("quote".equals(name))
			return QUOTED;
		if("lambda".equals(name))
			return LAMBDA;
		if("set!".equals(name))
			return SET;
		if("begin".equals(name))
            return BEGIN;
		return REGULAR;
	}
}