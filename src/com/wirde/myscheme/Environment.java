package com.wirde.myscheme;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.wirde.myscheme.node.BoolLit;
import com.wirde.myscheme.node.Cons;
import com.wirde.myscheme.node.Ident;
import com.wirde.myscheme.node.IntLit;
import com.wirde.myscheme.node.Node;
import com.wirde.myscheme.node.Proc;

public class Environment {

    private Map<String, Node> bindings = new HashMap<String, Node>();

    private Environment parent;

    public Environment(Environment parent) {
        this.parent = parent;
    }

    public Environment() {
        addBuiltins();
    }

    public Node lookup(Ident ident) {
        Node res = bindings.get(ident.getName());
        if (res == null) {
            if (parent == null)
                throw new EvalException("Unbound identifier: " + ident);
            else
                return parent.lookup(ident);
        }
        return res;
    }

    public void bind(Ident ident, Node value) {
        bindings.put(ident.getName(), value);
    }

    public void set(Ident ident, Node value) {
        if (lookup(ident) == null)
            throw new EvalException("Unbound variable: " + ident);
        bind(ident, value);
    }

    public void evalFile(String file) throws IOException {
        evalFile(file, null);
    }

    public void evalFile(String file, PrintStream out) throws IOException {
        Reader reader = new FileReader(file);
        Parser parser = new Parser(reader);
        while (true) {
            Node exp = parser.parseNext();
            if (exp == null)
                break;

            Node res = exp.eval(this);
            if (out != null)
                out.println(res);
        }
        if (reader.read() != -1)
            throw new ParseException("Error reading file: " + file);
    }

    public void setParent(Environment env) {
        parent = env;
    }

    private void addBuiltins() {

        // Primitive functions
        bindings.put("+", new Proc() {
            @Override
            public Node apply(Cons args) {

                BigInteger result = BigInteger.valueOf(0);
                do {
                    if (args != null && args.getFirst() != null) {
                        if (args.getFirst() instanceof IntLit) {
                            result = result.add(((IntLit) args.getFirst()).getIntVal());
                        } else {
                            throw new EvalException("Expected int, got: " + args.getFirst());
                        }
                        args = args.getRestAsCons();
                    } else {
                        throw new EvalException("Error applying: " + args);
                    }
                } while (!Cons.NIL.equals(args));
                return new IntLit(result);
            }
        });

        bindings.put("-", new Proc() {
            @Override
            public Node apply(Cons args) {
                if (args == null || args.length() == 0)
                    throw new EvalException("Expected at least 1 argument, got 0");
                if (args.length() == 1) {
                    if (args != null && args.getFirst() != null) {
                        if (args.getFirst() instanceof IntLit) {
                            return new IntLit(((IntLit) args.getFirst()).getIntVal().negate());
                        } else {
                            throw new EvalException("Expected int, got: " + args.getFirst());
                        }
                    } else {
                        throw new EvalException("Got null");
                    }
                }
                BigInteger result = ((IntLit) args.getFirst()).getIntVal();
                args = args.getRestAsCons();
                do {
                    if (args != null && args.getFirst() != null) {
                        if (args.getFirst() instanceof IntLit) {
                            result = result.subtract(((IntLit) args.getFirst()).getIntVal());
                        } else {
                            throw new EvalException("Expected int, got: " + args.getFirst());
                        }
                        args = args.getRestAsCons();
                    } else {
                        throw new EvalException("Error applying: " + args);
                    }
                } while (!Cons.NIL.equals(args));
                return new IntLit(result);
            }
        });

        bindings.put("*", new Proc() {
            @Override
            public Node apply(Cons args) {
                BigInteger result = BigInteger.valueOf(1);
                do {
                    if (args != null && args.getFirst() != null) {
                        if (args.getFirst() instanceof IntLit) {
                            result = result.multiply(((IntLit) args.getFirst()).getIntVal());
                        } else {
                            throw new EvalException("Expected int, got: " + args.getFirst());
                        }
                        args = args.getRestAsCons();
                    } else {
                        throw new EvalException("Error applying: " + args);
                    }
                } while (!Cons.NIL.equals(args));
                return new IntLit(result);
            }
        });

        bindings.put("=", new Proc() {
            @Override
            public Node apply(Cons args) {
                if (args.getFirst().equals(args.getSecond()))
                    return BoolLit.TRUE;
                else
                    return BoolLit.FALSE;
            }
        });

        bindings.put("print", new Proc() {
            @Override
            public Node apply(Cons args) {
                while (!Cons.NIL.equals(args)) {
                    System.out.print(args.getFirst() + " ");
                    args = args.getRestAsCons();
                }
                System.out.println();
                return Cons.NIL;
            }
        });

        bindings.put("cons", new Proc() {
            @Override
            public Node apply(Cons args) {
                if (args.length() != 2)
                    throw new EvalException("Expected 2 arguments, got: " + args.length(), this);
                return new Cons(args.getFirst(), args.getSecond());
            }
        });

        bindings.put("car", new Proc() {
            @Override
            public Node apply(Cons args) {
                if (args.length() != 1)
                    throw new EvalException("Expected 1 arguments, got: " + args.length(), this);
                return ((Cons) args.getFirst()).getFirst();
            }
        });

        bindings.put("cdr", new Proc() {
            @Override
            public Node apply(Cons args) {
                if (args.length() != 1)
                    throw new EvalException("Expected 1 arguments, got: " + args.length(), this);
                return ((Cons) args.getFirst()).getRest();
            }
        });

        // Variables
        bindings.put("nil", Cons.NIL);
    }
}