package com.wirde.myscheme;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.wirde.myscheme.node.BoolLit;
import com.wirde.myscheme.node.Cons;
import com.wirde.myscheme.node.IntLit;
import com.wirde.myscheme.node.Node;
import com.wirde.myscheme.node.PrimitiveProc;

public class Primitives {
    
    private static BigInteger getInt(Node node) {
        if (node instanceof IntLit)
            return (BigInteger) ((IntLit) node).getIntVal();
        throw new EvalException("Expected int, got: " + node);
    }

    public static Map<String, 
    Node> getPrimitives() {
        Map<String, Node> primitives = new HashMap<String, Node>();

        // Primitive functions

        primitives.put("+", new PrimitiveProc(1) {
            @Override
            public Node doApply(Cons args) {
                BigInteger result = getInt(args.getFirst());
                while (!args.getRest().equals(Cons.NIL)) {
                    args = args.getRestAsCons();
                    result = result.add(getInt(args.getFirst()));
                }
                return new IntLit(result);
            }
        });

        primitives.put("-", new PrimitiveProc(1) {
            @Override
            public Node doApply(Cons args) {
                BigInteger result = getInt(args.getFirst());
                if (args.getRest().equals(Cons.NIL))
                    result = result.negate();
                else while (!args.getRest().equals(Cons.NIL)) {
                    args = args.getRestAsCons();
                    result = result.subtract(getInt(args.getFirst()));
                }
                return new IntLit(result);
            }
        });

        primitives.put("*", new PrimitiveProc(1) {
            @Override
            public Node doApply(Cons args) {
                BigInteger result = getInt(args.getFirst());
                while (!args.getRest().equals(Cons.NIL)) {
                    args = args.getRestAsCons();
                    result = result.multiply(getInt(args.getFirst()));
                }
                return new IntLit(result);
            }
        });
        
        primitives.put("=", new PrimitiveProc(2, 2) {
            @Override
            public Node doApply(Cons args) {
                if (getInt(args.getFirst()).equals(getInt(args.getSecond())))
                    return BoolLit.TRUE;
                else
                    return BoolLit.FALSE;
            }
        });
        
        primitives.put("equal?", new PrimitiveProc(2, 2) {
            @Override
            public Node doApply(Cons args) {
                if (args.getFirst().equals(args.getSecond()))
                    return BoolLit.TRUE;
                else
                    return BoolLit.FALSE;
            }
        });

        primitives.put("print", new PrimitiveProc(1) {
            @Override
            public Node doApply(Cons args) {
                while (!Cons.NIL.equals(args)) {
                    System.out.print(args.getFirst() + " ");
                    args = args.getRestAsCons();
                }
                System.out.println();
                return Cons.NIL;
            }
        });

        primitives.put("cons", new PrimitiveProc(2, 2) {
            @Override
            public Node doApply(Cons args) {
                return new Cons(args.getFirst(), args.getSecond());
            }
        });

        primitives.put("car", new PrimitiveProc(1, 1) {
            @Override
            public Node doApply(Cons args) {
                return ((Cons) args.getFirst()).getFirst();
            }
        });

        primitives.put("cdr", new PrimitiveProc(1, 1) {
            @Override
            public Node doApply(Cons args) {
                return ((Cons) args.getFirst()).getRest();
            }
        });

        // Variables
        primitives.put("nil", Cons.NIL);

        return primitives;
    }
}