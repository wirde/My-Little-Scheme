package com.wirde.myscheme.node;

public class BoolLit extends Literal {
	public static final Node TRUE = new BoolLit();
	public static final Node FALSE = new BoolLit();
	
	private BoolLit() {
	}
	
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public String toString() {
        return this == BoolLit.TRUE ? "#t" : "#f";
    }

    public static boolean isTrue(Node node) {
        return node.equals(BoolLit.FALSE) ? false : true;
    }
}