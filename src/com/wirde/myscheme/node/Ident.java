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
    
	public String getName() {
		return name;
	}
	
	@Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
	
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Ident other = (Ident) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}