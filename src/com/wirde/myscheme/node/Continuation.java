package com.wirde.myscheme.node;

import com.wirde.myscheme.Environment;

public class Continuation extends Node {

    private final Node expression;
    private final Environment frame;

    public Continuation(Node expression, Environment frame) {
        this.expression = expression;
        this.frame = frame;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Node eval(Environment env, boolean forceEvaluation) {
        return expression.eval(frame, forceEvaluation);
    }
    
    @Override
    public String toString() {
        return "<continuation>";
    }
}