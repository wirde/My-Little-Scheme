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
import com.wirde.myscheme.node.StrLit;

public class Primitives {
    
    private static BigInteger getInt(Node node) {
        if (node instanceof IntLit)
            return (BigInteger) ((IntLit) node).getIntVal();
        throw new EvalException("Expected int, got: " + node);
    }

    public static Map<String, Node> getPrimitives() {
        Map<String, Node> primitives = new HashMap<String, Node>();

        //Numerical functions

        primitives.put("+", new PrimitiveProc(0) {
            @Override
            public Node doApply(Cons args) {
                if (args == Cons.NIL)
                    return new IntLit(0);
                BigInteger result = getInt(args.getFirst());
                for (Cons currentCons : args.getRestAsCons()) {
                    result = result.add(getInt(currentCons.getFirst()));
                }
                return new IntLit(result);
            }
        });

        primitives.put("-", new PrimitiveProc(0) {
            @Override
            public Node doApply(Cons args) {
                if (args == Cons.NIL)
                    return new IntLit(0);
                BigInteger result = getInt(args.getFirst());
                if (args.getRest().equals(Cons.NIL))
                    result = result.negate();
                else for (Cons currentCons : args.getRestAsCons()) {
                    result = result.subtract(getInt(currentCons.getFirst()));
                }
                return new IntLit(result);
            }
        });

        primitives.put("*", new PrimitiveProc(0) {
            @Override
            public Node doApply(Cons args) {
                if (args == Cons.NIL)
                    return new IntLit(1);
                BigInteger result = getInt(args.getFirst());
                for (Cons currentCons : args.getRestAsCons()) {
                    result = result.multiply(getInt(currentCons.getFirst()));
                }
                return new IntLit(result);
            }
        });

        primitives.put("max", new PrimitiveProc(1) {
            @Override
            public Node doApply(Cons args) {
                Node first = args.getFirst();
                for (Cons currentCons : args.getRestAsCons()) {
                    Node second = currentCons.getFirst(); 
                    if (getInt(first).compareTo(getInt(second)) < 0)
                        first = second;
                }
                return first;
            }
        });
        
        primitives.put("min", new PrimitiveProc(1) {
            @Override
            public Node doApply(Cons args) {
                Node first = args.getFirst();
                for (Cons currentCons : args.getRestAsCons()) {
                    Node second = currentCons.getFirst(); 
                    if (getInt(first).compareTo(getInt(second)) > 0)
                        first = second;
                }
                return first;
            }
        });
        
        primitives.put("abs", new PrimitiveProc(1, 1) {
            @Override
            public Node doApply(Cons args) {
                return new IntLit(getInt(args.getFirst()).abs());
            }
        });
        
        primitives.put("remainder", new PrimitiveProc(2, 2) {
            @Override
            public Node doApply(Cons args) {
                return new IntLit(getInt(args.getFirst()).remainder(getInt(args.getSecond())));
            }
        });
        
        primitives.put("quotient", new PrimitiveProc(2, 2) {
            @Override
            public Node doApply(Cons args) {
                return new IntLit(getInt(args.getFirst()).divide(getInt(args.getSecond())));
            }
        });
        
        primitives.put("modulo", new PrimitiveProc(2, 2) {
            @Override
            public Node doApply(Cons args) {
                BigInteger first = getInt(args.getFirst());
                BigInteger second = getInt(args.getSecond());
                return new IntLit(first.mod(second));
            }
        });
        
        primitives.put("gcd", new PrimitiveProc(0) {
            @Override
            public Node doApply(Cons args) {
                if (args == Cons.NIL)
                    return new IntLit(0);
                
                BigInteger result = getInt(args.getFirst());
                for (Cons next : args.getRestAsCons()) {
                    BigInteger second = getInt(next.getFirst());
                    result = result.gcd(second);
                }
                    return new IntLit(result);
            }
        });
        
        //Predicates
        
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
        
        primitives.put(">", new NumCompProc(1));
        primitives.put("<", new NumCompProc(-1));
        primitives.put(">=", new NumCompProc(1, 0));
        primitives.put("<=", new NumCompProc(-1, 0));
        
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
                return args.getFirst().equals(args.getSecond()) ? BoolLit.TRUE : BoolLit.FALSE;
            }
        });
        
        primitives.put("procedure?", new PrimitiveProc(1, 1) {
            @Override
            public Node doApply(Cons args) {
                return args.getFirst() instanceof Proc ? BoolLit.TRUE : BoolLit.FALSE;
            }
        });
        
        primitives.put("boolean?", new TypeCompProc(BoolLit.class)); 
        primitives.put("number?", new TypeCompProc(IntLit.class));
        primitives.put("integer?", new TypeCompProc(IntLit.class));
        primitives.put("exact?", new TypeCompProc(IntLit.class));
        
        primitives.put("inexact?", new PrimitiveProc(1, 1) {
            @Override
            public Node doApply(Cons args) {
                return args.getFirst() instanceof IntLit ? BoolLit.FALSE : BoolLit.TRUE;
            }
        });
        
        primitives.put("pair?", new PrimitiveProc(1, 1) {
            @Override
            public Node doApply(Cons args) {
                Node arg = args.getFirst();
                return arg instanceof Cons && arg != Cons.NIL ? BoolLit.TRUE : BoolLit.FALSE;
            }
        });
        
        //Display
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

        //List functions
        
        primitives.put("cons", new PrimitiveProc(2, 2) {
            @Override
            public Node doApply(Cons args) {
                return new Cons(args.getFirst(), args.getSecond());
            }
        });

        primitives.put("car", new PrimitiveProc(1, 1) {
            @Override
            public Node doApply(Cons args) {
                Cons first = (Cons) args.getFirst();
                if (first == Cons.NIL)
                    throw new EvalException("Can't take car of nil");
                return first.getFirst();
            }
        });

        primitives.put("cdr", new PrimitiveProc(1, 1) {
            @Override
            public Node doApply(Cons args) {
                Cons first = (Cons) args.getFirst();
                if (first == Cons.NIL)
                    throw new EvalException("Can't take cdr of nil");                
                return first.getRest();
            }
        });

        primitives.put("length", new PrimitiveProc(1, 1) {
            @Override
            public Node doApply(Cons args) {
                return new IntLit(((Cons) args.getFirst()).length());
            }
        });
        
        //Misc
        
        primitives.put("apply", new PrimitiveProc(2, 2) {
            @Override
            public Node doApply(Cons args) {
                return ((Proc) args.getFirst()).apply((Cons) args.getSecond());
            }
        });
        
        primitives.put("make-string", new PrimitiveProc(1) {
            @Override
            public Node doApply(Cons args) {
                String result = "";
                for (Cons currentCons : args) {
                    result += currentCons.getFirst();
                }
                return new StrLit(result);
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
 
    private static class TypeCompProc extends PrimitiveProc {

        private final Class<?> clazz;

        public TypeCompProc(Class<?> clazz) {
            super(1, 1);
            this.clazz = clazz;
        }

        @Override
        public Node doApply(Cons args) {
            return clazz.isInstance(args.getFirst()) ? BoolLit.TRUE : BoolLit.FALSE;
        }
    }
    
    //TODO: Improve impl
    private static class NumCompProc extends PrimitiveProc {
        
        private final int compArg1;
        private final int compArg2;

        public NumCompProc(int compArg1) {
            this(compArg1, compArg1);
        }
        
        public NumCompProc(int compArg1, int compArg2) {
            super(2);
            this.compArg1 = compArg1;
            this.compArg2 = compArg2;
        }

        @Override
        public Node doApply(Cons args) {
            BigInteger first = getInt(args.getFirst());
            for (Cons currentCons : args.getRestAsCons()) {
                BigInteger second = getInt(currentCons.getFirst());
                int res = first.compareTo(second);
                if (res != compArg1 && res != compArg2)
                    return BoolLit.FALSE;
                first = second;
            }
            return BoolLit.TRUE; 
        }
    }
}