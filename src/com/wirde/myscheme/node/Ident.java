package com.wirde.myscheme.node;

import com.wirde.myscheme.Environment;

public class Ident extends Node {
	private final String name;
	
	public Ident(String name) {
		this.name = name;
	}

    @Override
    public Node eval(Environment env) {
        return env.lookup(this);
    }
    
	public String print(int position) {
		return getIndent(position) + name;
	}

	public String getName() {
		return name;
	}
}
