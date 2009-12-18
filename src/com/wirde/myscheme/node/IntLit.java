package com.wirde.myscheme.node;

public class IntLit extends Literal {	
	private final int intVal;
	
	public IntLit(int intVal) {
		this.intVal = intVal;
	}

	@Override
	protected String print(int position) {
		return getIndent(position) + Integer.toString(intVal);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + intVal;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof IntLit))
			return false;
		IntLit other = (IntLit) obj;
		if (intVal != other.intVal)
			return false;
		return true;
	}

	public int getIntVal() {
		return intVal;
	}
	
}
