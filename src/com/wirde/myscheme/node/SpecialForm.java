package com.wirde.myscheme.node;

import com.wirde.myscheme.Environment;
import com.wirde.myscheme.EvalException;

public enum SpecialForm {

    REGULAR {
        @Override
        public Node evalForm(Cons exp, Environment env) {
          Proc proc = (Proc) exp.getFirst().eval(env);        
          return proc.apply(evaluateList(exp.getRestAsCons(), env));
        }
    }, 
    DEFINE {
        @Override
        public Node evalForm(Cons exp, Environment env) {
            if (Cons.NIL == exp.getRest())
                throw new EvalException("Expected identifier, got nil");
            Node definee = exp.getSecond();
            if (definee instanceof Ident) {
                if (Cons.NIL == exp.getRestAsCons().getRest())
                    throw new EvalException("Expected expression, got nil");
                env.bind((Ident) definee, exp.getThird().eval(env));
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
        public Node evalForm(Cons exp, Environment env) {
            if (BoolLit.TRUE.equals(exp.getSecond().eval(env)))
                return exp.getThird().eval(env);
            else {
                Node falseRes = exp.getFourth();
                if (falseRes != Cons.NIL)
                    return falseRes.eval(env);
                return null;
            }
        }
    }, 
    QUOTED {
        @Override
        public Node evalForm(Cons exp, Environment env) {
            return exp.getSecond();
        }
    }, 
    LAMBDA {
        @Override
        public Node evalForm(Cons exp, Environment env) {
            return new Lambda(exp.getSecond(), exp.getRestAsCons().getRestAsCons(), env);
        }
    }, 
    SET {
        @Override
        public Node evalForm(Cons exp, Environment env) {
            env.set((Ident) exp.getSecond(), exp.getThird().eval(env));
            return null;
        }
    }, 
    BEGIN {
        @Override
        public Node evalForm(Cons exp, Environment env) {
            Cons exps = exp.getRestAsCons();
            Node result = Cons.NIL;
            while (!exps.equals(Cons.NIL)) {
                result = exps.getFirst().eval(env);
                exps = exps.getRestAsCons();
            }
            return result;
        }
    }, 
    LET {
        @Override
        public Node evalForm(Cons exp, Environment env) {
            Cons params = Cons.NIL;
            Cons args = Cons.NIL;
            Cons paramList = (Cons) exp.getSecond();
            Cons body = exp.getRestAsCons().getRestAsCons();
            while (!paramList.equals(Cons.NIL)) {
                Cons paramArgPair = (Cons) paramList.getFirst();
                params = new Cons(paramArgPair.getFirst(), params);
                args = new Cons(paramArgPair.getSecond().eval(env), args);
                paramList = paramList.getRestAsCons();
            }
            return new Lambda(params, body, env).apply(args);
        }
    }, 
    COND {
        @Override
        public Node evalForm(Cons exp, Environment env) {
            Cons condClauses = exp.getRestAsCons();
            while (!condClauses.equals(Cons.NIL)) {
                Node predicate = ((Cons) condClauses.getFirst()).getFirst();
                if ((condClauses.getRest().equals(Cons.NIL) && predicate.equals(new Ident("else")))
                        ||
                        BoolLit.isTrue(predicate.eval(env))) {
                    Node res = Cons.NIL;
                    Cons expressions = ((Cons) condClauses.getFirst()).getRestAsCons();
                    //TODO: predicate is evaluated twice...
                    if (expressions.getFirst().equals(new Ident("=>")))
                        return ((Proc) expressions.getSecond().eval(env)).apply(new Cons(predicate.eval(env), Cons.NIL));
                    for (Cons currentCons : expressions) {
                        res = currentCons.getFirst().eval(env);
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
        public Node evalForm(Cons exp, Environment env) {
            Node res = BoolLit.TRUE;
            for (Cons currentCons : exp.getRestAsCons()) {
                res = currentCons.getFirst().eval(env);
                if (!BoolLit.isTrue(res))
                    return BoolLit.FALSE;
            }
            return res;
        }
    }, 
    OR {
        @Override
        public Node evalForm(Cons exp, Environment env) {
            Node res = BoolLit.FALSE;
            for (Cons currentCons : exp.getRestAsCons()) {
                res = currentCons.getFirst().eval(env); 
                if (BoolLit.isTrue(res))
                    return res;
            }
            return BoolLit.FALSE;
        }
    }, 
    DO {
        @Override
        public Node evalForm(Cons exp, Environment env) {
            //TODO: Implement
            return Cons.NIL;
        }
    }, 
    CASE {
        @Override
        public Node evalForm(Cons exp, Environment env) {
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

    public abstract Node evalForm(Cons exp, Environment env);
    
    private static Cons evaluateList(Cons cons, Environment env) {
        if (cons == null)
            return null;
        
        if (Cons.NIL == cons)
            return Cons.NIL;
        
        return new Cons(cons.getFirst().eval(env), evaluateList(cons.getRestAsCons(), env));
    }
}