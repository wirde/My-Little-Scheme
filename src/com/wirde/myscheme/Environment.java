package com.wirde.myscheme;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
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
	
	public void evalFile(Parser parser, String file) throws IOException {
		evalFile(parser, file, null);
	}
	
	public void evalFile(Parser parser, String file, PrintStream out) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = "";
		while (true) {
			String nextLine = reader.readLine();
			if (nextLine == null) {
				break;
			}
			line += nextLine;
			try {
				Node exp = parser.parseExpression(line);
				if (exp == null)
					continue;
				
				Node res = exp.eval(this);
				if (out != null)
					out.println(res);
				line = "";
			} catch (NoMoreTokensException e) {
				//Read more
			}
		}
		if (!"".equals(line))
			throw new ParseException("Error parsing: " + line);
	}

	public void setParent(Environment env) {
		parent = env;
	}
	
	private void addBuiltins() {
		
		//Primitive functions
		builtins.put("+", 
		new Proc() {
			@Override
			Node apply(Cons args) {
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
					Node apply(Cons args) {
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
			Node apply(Cons args) {
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
			Node apply(Cons args) {
				if (args.getFirst().equals(args.getSecond()))
					return BoolLit.TRUE;
				else
					return BoolLit.FALSE;
			}});
		
		builtins.put("print", new Proc() {
            @Override
            Node apply(Cons args) {
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
			Node apply(Cons args) {
				return new Cons(args.getFirst(), args.getSecond());
			}});
		
		builtins.put("car", new Proc() {
			@Override
			Node apply(Cons args) {
				return ((Cons) args.getFirst()).getFirst();
			}});
		
		builtins.put("cdr", new Proc() {
			@Override
			Node apply(Cons args) {
				return ((Cons) args.getFirst()).getRest();
			}});
		
		//Variables
		builtins.put("nil", Cons.NIL);
	}
}
