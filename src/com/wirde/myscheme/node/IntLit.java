package com.wirde.myscheme.node;

import java.math.BigInteger;


public class IntLit extends Literal {	
	private final BigInteger intVal;
	
	public IntLit(BigInteger intVal) {
		this.intVal = intVal;
	}

	public IntLit(int intVal) {
	    this(BigInteger.valueOf(intVal));
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((intVal == null) ? 0 : intVal.hashCode());
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
		IntLit other = (IntLit) obj;
		if (intVal == null) {
			if (other.intVal != null)
				return false;
		} else if (!intVal.equals(other.intVal))
			return false;
		return true;
	}

	public BigInteger getIntVal() {
		return intVal;
	}

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public String toString() {
        return intVal.toString();
    }
}
