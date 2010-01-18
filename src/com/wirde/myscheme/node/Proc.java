package com.wirde.myscheme.node;

import com.wirde.myscheme.Environment;

public abstract class Proc extends Node {

    @Override
    public Node eval(Environment env) {
        return this;
    }
    
	public abstract Node apply(Cons args);

	@Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}