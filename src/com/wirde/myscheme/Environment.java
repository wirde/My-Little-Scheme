package com.wirde.myscheme;

import java.util.HashMap;
import java.util.Map;

public class Environment {
	
	private Map<String, Node> builtins = new HashMap<String, Node>();
	
	private Environment parent;
	
	public Environment(Environment parent) {
		this.parent = parent;
	}
	
	public Environment() {
		addBuiltins();
	}

	public Node lookup(Ident ident) {
		Node res = builtins.get(ident.getName());
		if (res == null) {
			if (parent == null)
				throw new EvalException("Unbound identifier: " + ident);
			else
				return parent.lookup(ident);
		}
		return res;
	}

	public void assoc(Ident ident, Node value) {
		builtins.put(ident.getName(), value);
	}
	
	private void addBuiltins() {
		
		//Primitive functions
		builtins.put("+", 
		new Proc() {
			@Override
			Node apply(Cons args, Environment env) {
				int result = 0;
				do {
					if (args != null && args.getFirst() != null) {
						if (args.getFirst() instanceof IntLit) {
							result += ((IntLit) args.getFirst()).getIntVal();
						} else {
							throw new EvalException("Expected int, got: " + args.getFirst());
						}
						args = args.getRestAsCons();
					} else {
						throw new EvalException("Error applying: " + args);
					}
				} while (args != Cons.NIL);
				return new IntLit(result);
			}});
		
		builtins.put("-", 
				new Proc() {
					@Override
					Node apply(Cons args, Environment env) {
						if (args == null || args.length() == 0)
							throw new EvalException("Expected at least 1 argument, got 0");
						if (args.length() == 1) {
							if (args != null && args.getFirst() != null) {
								if (args.getFirst() instanceof IntLit) {
									return new IntLit(-((IntLit) args.getFirst()).getIntVal());
								} else {
									throw new EvalException("Expected int, got: " + args.getFirst());
								}
							} else {
								throw new EvalException("Got null");
							}
						}
						int result = ((IntLit) args.getFirst()).getIntVal();
						args = args.getRestAsCons();
						do {
							if (args != null && args.getFirst() != null) {
								if (args.getFirst() instanceof IntLit) {
									result -= ((IntLit) args.getFirst()).getIntVal();
								} else {
									throw new EvalException("Expected int, got: " + args.getFirst());
								}
								args = args.getRestAsCons();
							} else {
								throw new EvalException("Error applying: " + args);
							}
						} while (args != Cons.NIL);
						return new IntLit(result);
					}});

		builtins.put("*", new Proc() {
			@Override
			Node apply(Cons args, Environment env) {
				int result = 1;
				do {
					if (args != null && args.getFirst() != null) {
						if (args.getFirst() instanceof IntLit) {
							result *= ((IntLit) args.getFirst()).getIntVal();
						} else {
							throw new EvalException("Expected int, got: " + args.getFirst());
						}
						args = args.getRestAsCons();
					} else {
						throw new EvalException("Error applying: " + args);
					}
				} while (args != Cons.NIL);
				return new IntLit(result);
			}});
				
		builtins.put("=", new Proc() {
			@Override
			Node apply(Cons args, Environment env) {
				if (args.getFirst().equals(args.getSecond()))
					return BoolLit.TRUE;
				else
					return BoolLit.FALSE;
			}});
		
		builtins.put("print", new Proc() {
            @Override
            Node apply(Cons args, Environment env) {
                while(args != Cons.NIL) {
                    System.out.print(args.getFirst() + " ");
                    args = args.getRestAsCons();
                }
                System.out.println();
                return Cons.NIL;
            }
        });
		
		builtins.put("cons", new Proc() {
			@Override
			Node apply(Cons args, Environment env) {
				return new Cons(args.getFirst(), args.getSecond());
			}});
		
		builtins.put("car", new Proc() {
			@Override
			Node apply(Cons args, Environment env) {
				return ((Cons) args.getFirst()).getFirst();
			}});
		
		builtins.put("cdr", new Proc() {
			@Override
			Node apply(Cons args, Environment env) {
				return ((Cons) args.getFirst()).getRest();
			}});
		
		//Variables
		builtins.put("nil", Cons.NIL);
	}

}
