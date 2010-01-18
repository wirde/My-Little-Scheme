package com.wirde.myscheme.node;

import com.wirde.myscheme.Environment;

public abstract class Literal extends Node {
    @Override
    public Node eval(Environment env) {
        return this;
    }
}