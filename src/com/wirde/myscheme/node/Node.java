package com.wirde.myscheme.node;

import com.wirde.myscheme.Environment;

public abstract class Node {	

    public abstract Node eval(Environment env);
    
	public abstract void accept(NodeVisitor visitor);
}

