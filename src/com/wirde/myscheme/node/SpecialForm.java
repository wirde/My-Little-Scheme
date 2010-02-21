package com.wirde.myscheme.node;

import com.wirde.myscheme.Environment;
import com.wirde.myscheme.EvalException;

public enum SpecialForm {

    REGULAR {
        @Override
        public Node evalForm(Cons exp, Environment env, boolean forceEvaluation) {
            Proc proc = (Proc) exp.getFirst().eval(env, true);
            return proc.apply(evaluateList(exp.getRestAsCons(), env), forceEvaluation);
        }
    }, 
    DEFINE {
        @Override
        public Node evalForm(Cons exp, Environment env, boolean forceEvaluation) {
            if (Cons.NIL == exp.getRest())
                throw new EvalException("Expected identifier, got nil");
            Node definee = exp.getSecond();
            if (definee instanceof Ident) {
                if (Cons.NIL == exp.getRestAsCons().getRest())
                    throw new EvalException("Expected expression, got nil");
                env.bind((Ident) definee, exp.getThird().eval(env, true));
            }
            else if (definee instanceof Cons) {
                Cons lambdaDef = (Cons) definee;
                env.bind((Ident) lambdaDef.getFirst(), new Lambda(lambdaDef.getRest(), exp.getRestAsCons().getRestAsCons(), env));
            } else
                throw new EvalException("Expected Ident or Cons. Got " + definee.getClass(), exp);
            return null;
        }
    }, 
    IF {
        @Override
        public Node evalForm(Cons exp, Environment env, boolean forceEvaluation) {
            if (BoolLit.TRUE.equals(exp.getSecond().eval(env, true)))
                return exp.getThird().eval(env, forceEvaluation);
            else {
                Node falseRes = exp.getFourth();
                if (falseRes != Cons.NIL)
                    return falseRes.eval(env, forceEvaluation);
                return null;
            }
        }
    }, 
    QUOTED {
        @Override
        public Node evalForm(Cons exp, Environment env, boolean forceEvaluation) {
            return exp.getSecond();
        }
    }, 
    LAMBDA {
        @Override
        public Node evalForm(Cons exp, Environment env, boolean forceEvaluation) {
            return new Lambda(exp.getSecond(), exp.getRestAsCons().getRestAsCons(), env);
        }
    }, 
    SET {
        @Override
        public Node evalForm(Cons exp, Environment env, boolean forceEvaluation) {
            env.set((Ident) exp.getSecond(), exp.getThird().eval(env, true));
            return null;
        }
    }, 
    BEGIN {
        @Override
        public Node evalForm(Cons exp, Environment env, boolean forceEvaluation) {
            //TODO: create lambda instead...
            Cons exps = exp.getRestAsCons();
            Node result = Cons.NIL;
            while (!exps.equals(Cons.NIL)) {
                result = exps.getFirst().eval(env, true);
                exps = exps.getRestAsCons();
            }
            return result;
        }
    }, 
    LET {
        @Override
        public Node evalForm(Cons exp, Environment env, boolean forceEvaluation) {
            Cons params = Cons.NIL;
            Cons args = Cons.NIL;
            Cons paramList = (Cons) exp.getSecond();
            Cons body = exp.getRestAsCons().getRestAsCons();
            while (!paramList.equals(Cons.NIL)) {
                Cons paramArgPair = (Cons) paramList.getFirst();
                params = new Cons(paramArgPair.getFirst(), params);
                args = new Cons(paramArgPair.getSecond().eval(env, true), args);
                paramList = paramList.getRestAsCons();
            }
            return new Lambda(params, body, env).apply(args, forceEvaluation);
        }
    }, 
    COND {
        @Override
        public Node evalForm(Cons exp, Environment env, boolean forceEvaluation) {
            Cons condClauses = exp.getRestAsCons();
            while (!condClauses.equals(Cons.NIL)) {
                Node predicate = ((Cons) condClauses.getFirst()).getFirst();
                if ((condClauses.getRest().equals(Cons.NIL) && predicate.equals(new Ident("else")))
                        ||
                        BoolLit.isTrue(predicate.eval(env, true))) {
                    Node res = Cons.NIL;
                    Cons expressions = ((Cons) condClauses.getFirst()).getRestAsCons();
                    //TODO: predicate is evaluated twice...
                    if (expressions.getFirst().equals(new Ident("=>")))
                        return ((Proc) expressions.getSecond().eval(env, true)).apply(new Cons(predicate.eval(env, true), Cons.NIL), forceEvaluation);
                    for (Cons currentCons : expressions) {
                        res = currentCons.getFirst().eval(env, forceEvaluation);
                    }
                    return res;
                }
                condClauses = condClauses.getRestAsCons();
            }
            return null;
        }
    }, 
    AND {
        @Override
        public Node evalForm(Cons exp, Environment env, boolean forceEvaluation) {
            Node res = BoolLit.TRUE;
            for (Cons currentCons : exp.getRestAsCons()) {
                res = currentCons.getFirst().eval(env, true);
                if (!BoolLit.isTrue(res))
                    return BoolLit.FALSE;
            }
            return res;
        }
    }, 
    OR {
        @Override
        public Node evalForm(Cons exp, Environment env, boolean forceEvaluation) {
            Node res = BoolLit.FALSE;
            for (Cons currentCons : exp.getRestAsCons()) {
                res = currentCons.getFirst().eval(env, true);
                if (BoolLit.isTrue(res))
                    return res;
            }
            return BoolLit.FALSE;
        }
    }, 
    DO {
        @Override
        public Node evalForm(Cons exp, Environment env, boolean forceEvaluation) {
            //TODO: Implement
            return Cons.NIL;
        }
    }, 
    CASE {
        @Override
        public Node evalForm(Cons exp, Environment env, boolean forceEvaluation) {
            //TODO: Implement
            return Cons.NIL;
        }
    };

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
        if("case".equals(name))
            return CASE;        
        return REGULAR;
    }

    public abstract Node evalForm(Cons exp, Environment env, boolean forceEvaluation);
    
    private static Cons evaluateList(Cons cons, Environment env) {
        if (cons == null)
            return null;
        
        if (Cons.NIL == cons)
            return Cons.NIL;
        
        return new Cons(cons.getFirst().eval(env, true), evaluateList(cons.getRestAsCons(), env));
    }
}