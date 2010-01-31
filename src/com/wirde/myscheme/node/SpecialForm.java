package com.wirde.myscheme.node;

public enum SpecialForm {
    REGULAR, DEFINE, IF, QUOTED, LAMBDA, SET, BEGIN, LET, COND, AND, OR, DO, SET_CDR, SET_CAR, CASE;

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
        if("let".equals(name))
            return LET;
        //TODO: for now...
        if("let*".equals(name))
            return LET;
        //TODO: for now...
        if("letrec".equals(name))
            return LET;
        if("cond".equals(name))
            return COND;
        if("and".equals(name))
            return AND;
        if("or".equals(name))
            return OR;
        if("do".equals(name))
            return DO;
        if("set-car!".equals(name))
            return SET_CAR;
        if("set-cdr!".equals(name))
            return SET_CDR;
        if("case".equals(name))
            return CASE;        
        return REGULAR;
    }
}