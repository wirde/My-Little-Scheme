package com.wirde.myscheme.node;

public enum SpecialForm {
    REGULAR, DEFINE, IF, QUOTED, LAMBDA, SET, BEGIN;

    public static SpecialForm toSpecialForm(Cons cons) {
        Node first = cons.getFirst();
        if (!(first instanceof Ident))
            return REGULAR;
        String name = ((Ident) first).getName();
        if ("define".equals(name))
            return DEFINE;
        if ("if".equals(name))
            return IF;
        if ("quote".equals(name))
            return QUOTED;
        if("lambda".equals(name))
            return LAMBDA;
        if("set!".equals(name))
            return SET;
        if("begin".equals(name))
            return BEGIN;
        return REGULAR;
    }
}