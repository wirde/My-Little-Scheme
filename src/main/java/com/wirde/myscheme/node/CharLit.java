package com.wirde.myscheme.node;


public class CharLit extends Literal {

    private final char charact;

    public CharLit(char charact) {
        this.charact = charact;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public String toString() {
        return "#\\" + charact;
    }

}
