package com.wirde.myscheme.node;

public class BoolLit extends Literal {
	public static final Node TRUE = new BoolLit(true);
	public static final Node FALSE = new BoolLit(false);
	
	private final boolean boolVal;
	
	private BoolLit(boolean boolVal) {
		this.boolVal = boolVal;
	}
	
	public String print(int position) {
		String result = boolVal ? "#t" : "#f";
		result = getIndent(position) + result;
		return result;
	}
}