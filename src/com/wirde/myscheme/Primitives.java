package com.wirde.myscheme;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.wirde.myscheme.node.BoolLit;
import com.wirde.myscheme.node.Cons;
import com.wirde.myscheme.node.IntLit;
import com.wirde.myscheme.node.Node;
import com.wirde.myscheme.node.PrettyPrintVisitor;
import com.wirde.myscheme.node.PrimitiveProc;
import com.wirde.myscheme.node.Proc;

public class Primitives {
    
    private static BigInteger getInt(Node node) {
        if (node instanceof IntLit)
            return (BigInteger) ((IntLit) node).getIntVal();
        throw new EvalException("Expected int, got: " + node);
    }

    public static Map<String, Node> getPrimitives() {
        Map<String, Node> primitives = new HashMap<String, Node>();

        // Primitive functions

        primitives.put("+", new PrimitiveProc(1) {
            @Override
            public Node doApply(Cons args) {
                BigInteger result = getInt(args.getFirst());
                for (Cons currentCons : args.getRestAsCons()) {
                    result = result.add(getInt(currentCons.getFirst()));
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
                else for (Cons currentCons : args.getRestAsCons()) {
                    result = result.subtract(getInt(currentCons.getFirst()));
                }
                return new IntLit(result);
            }
        });

        primitives.put("*", new PrimitiveProc(1) {
            @Override
            public Node doApply(Cons args) {
                BigInteger result = getInt(args.getFirst());
                for (Cons currentCons : args.getRestAsCons()) {
                    result = result.multiply(getInt(currentCons.getFirst()));
                }
                return new IntLit(result);
            }
        });
        
        primitives.put("not", new PrimitiveProc(1, 1) {
            @Override
            public Node doApply(Cons args) {
                return args.getFirst() == BoolLit.FALSE ? BoolLit.TRUE : BoolLit.FALSE;
            }
        });
        
        primitives.put("=", new PrimitiveProc(2) {
            @Override
            public Node doApply(Cons args) {
                BigInteger first = getInt(args.getFirst());
                for (Cons currentCons : args.getRestAsCons()) {
                    if (!getInt(currentCons.getFirst()).equals(first))
                        return BoolLit.FALSE;
                }
                return BoolLit.TRUE;
            }
        });
        
        primitives.put(">", new PrimitiveProc(2, 2) {
            @Override
            public Node doApply(Cons args) {
                return getInt(args.getFirst()).compareTo(getInt(args.getSecond())) == 1 ? BoolLit.TRUE : BoolLit.FALSE;
            }
        });
        
        primitives.put("<", new PrimitiveProc(2, 2) {
            @Override
            public Node doApply(Cons args) {
                return getInt(args.getFirst()).compareTo(getInt(args.getSecond())) == -1 ? BoolLit.TRUE : BoolLit.FALSE;
            }
        });
        
        primitives.put("equal?", new PrimitiveProc(2, 2) {
            @Override
            public Node doApply(Cons args) {
                return args.getFirst().equals(args.getSecond()) ? BoolLit.TRUE : BoolLit.FALSE;
            }
        });

        primitives.put("eq?", new PrimitiveProc(2, 2) {
            @Override
            public Node doApply(Cons args) {
                return args.getFirst() == args.getSecond() ? BoolLit.TRUE : BoolLit.FALSE;
            }
        });
        
        //TODO: Not correct, but will have to do for now
        primitives.put("eqv?", new PrimitiveProc(2, 2) {
            @Override
            public Node doApply(Cons args) {
                return args.getFirst() == args.getSecond() ? BoolLit.TRUE : BoolLit.FALSE;
            }
        });
        
        primitives.put("procedure?", new PrimitiveProc(1, 1) {
            @Override
            public Node doApply(Cons args) {
                return args.getFirst() instanceof Proc ? BoolLit.TRUE : BoolLit.FALSE;
            }
        });
        
        primitives.put("apply", new PrimitiveProc(2, 2) {
            @Override
            public Node doApply(Cons args) {
                return ((Proc) args.getFirst()).apply((Cons) args.getSecond());
            }
        });
        
        primitives.put("write", new PrimitiveProc(1, 1) {
            @Override
            public Node doApply(Cons args) {
                args.getFirst().accept(new PrettyPrintVisitor(System.out));
                return Cons.NIL;
            }
        });
        
        primitives.put("display", new PrimitiveProc(1, 1) {
            @Override
            public Node doApply(Cons args) {
                System.out.print(args.getFirst());
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

        primitives.put("length", new PrimitiveProc(1, 1) {
            @Override
            public Node doApply(Cons args) {
                return new IntLit(((Cons) args.getFirst()).length());
            }
        });
        primitives.put("quit", new PrimitiveProc(0, 0) {
            @Override
            public Node doApply(Cons args) {
                System.exit(0);
                return Cons.NIL;
            }
        });
        // Variables
        primitives.put("nil", Cons.NIL);

        return primitives;
    }
}