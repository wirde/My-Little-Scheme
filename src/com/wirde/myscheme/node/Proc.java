package com.wirde.myscheme.node;

import com.wirde.myscheme.Environment;

public abstract class Proc extends Node {

	@Override
	protected String print(int position) {
		return "#<native procedure>";
	}
	
    @Override
    public Node eval(Environment env) {
        return this;
    }
    
	public abstract Node apply(Cons args);
	
}