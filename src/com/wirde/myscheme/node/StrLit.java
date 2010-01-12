package com.wirde.myscheme.node;

public class StrLit extends Literal {	
	private final String strVal;
	
	public StrLit(String strVal) {
		this.strVal = strVal;
	}

	@Override
	protected String print(int position) {
		return getIndent(position) + strVal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((strVal == null) ? 0 : strVal.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof StrLit))
			return false;
		StrLit other = (StrLit) obj;
		if (strVal == null) {
			if (other.strVal != null)
				return false;
		} else if (!strVal.equals(other.strVal))
			return false;
		return true;
	}
	
}
