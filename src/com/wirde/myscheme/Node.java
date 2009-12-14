package com.wirde.myscheme;

//TODO: Immutable objects. What about set! ?

abstract class Node {	

    abstract Node eval(Environment env);
    
	abstract String print(int position);
	
	@Override
	public String toString() {
		return print(0);
	}
	
	protected String getIndent(int position) {
		String indent = "";
		for(int i = 0; i < position; i++) {
			indent += " ";
		}
		return indent;
	}
}

class Ident extends Node {
	private final String name;
	
	public Ident(String name) {
		this.name = name;
	}

    @Override
    Node eval(Environment env) {
        return env.lookup(this);
    }
    
	public String print(int position) {
		return getIndent(position) + name;
	}

	public String getName() {
		return name;
	}
	
}

abstract class Literal extends Node {
	@Override
	Node eval(Environment env) {
		return this;
	}
}

class BoolLit extends Literal {
	public static final Node TRUE = new BoolLit(true);
	public static final Node FALSE = new BoolLit(false);
	
	private final boolean boolVal;
	
	private BoolLit(boolean boolVal) {
		this.boolVal = boolVal;
	}
	
	public String print(int position) {
		String result = boolVal ? "#t" : "#f";
		result = getIndent(position) + result;
		return result;
	}
}

class StrLit extends Literal {	
	private final String strVal;
	
	public StrLit(String strVal) {
		this.strVal = strVal;
	}

	@Override
	String print(int position) {
		return getIndent(position) + "\"" + strVal + "\"";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((strVal == null) ? 0 : strVal.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof StrLit))
			return false;
		StrLit other = (StrLit) obj;
		if (strVal == null) {
			if (other.strVal != null)
				return false;
		} else if (!strVal.equals(other.strVal))
			return false;
		return true;
	}
	
}

class IntLit extends Literal {	
	private final int intVal;
	
	public IntLit(int intVal) {
		this.intVal = intVal;
	}

	@Override
	String print(int position) {
		return getIndent(position) + Integer.toString(intVal);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + intVal;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof IntLit))
			return false;
		IntLit other = (IntLit) obj;
		if (intVal != other.intVal)
			return false;
		return true;
	}

	public int getIntVal() {
		return intVal;
	}
	
}

class Cons extends Node {
    public static final Cons NIL = new Cons();
    private final Node first;
    private final Node rest;
    
    Cons() {
        this(NIL, NIL);
    }
    
    Cons(Node first, Node rest) {
    	this.first = first;
    	this.rest = rest;
    }
    
    Node getFirst() {
        return first;
    }
    
    Node getSecond() {
        return getRestAsCons().first;
    }

    Node getThird() {
        return getRestAsCons().getRestAsCons().first;
    }
    
    Node getFourth() {
    	return getRestAsCons().getRestAsCons().getRestAsCons().first;
    }
    
    Cons getRestAsCons() {
        return (Cons) rest;
    }
    
    Node getRest() {
        return rest;
    }
    
    Node eval(Environment env) {
    	SpecialForm special = SpecialForm.toSpecialForm(this);
    	switch (special) {
		case REGULAR:
            Proc proc = (Proc) first.eval(env);        
            return proc.apply(evaluateList(getRestAsCons(), env), env);
		case DEFINE:
            env.assoc(((Ident) getSecond()), getThird().eval(env));
            return NIL;
		case LAMBDA:
			return new Lambda((Cons) getSecond(), getThird());
		case IF:
			if (BoolLit.TRUE.equals(getSecond().eval(env)))
				return getThird().eval(env);
			else
				return getFourth().eval(env);
		default:
			throw new EvalException("Unkown Special form: " + special);
		}
    }
	
	private Cons evaluateList(Cons cons, Environment env) {
		if (cons == null)
			return null;
		
		if (cons == Cons.NIL)
			return (Cons) Cons.NIL;
		
		return new Cons(cons.getFirst().eval(env), evaluateList(cons.getRestAsCons(), env));
	}
    
	@Override
	String print(int position) {
		SpecialForm special = SpecialForm.toSpecialForm(this);
		switch (special) {
		case REGULAR:
			return printRegular(position);
		case DEFINE:
			return printDefine(position);
		case IF:
			return printIf(position);
		case QUOTED:
			return printQuoted(position);
		case LAMBDA:
			//TODO: Pretty print
			return printRegular(position);
		default:
			throw new EvalException("Unknown special form: " + special);
		}
	}

	private String printQuoted(int position) {
		return printRegular(position);
//		String result = "'(";
//		Cons currCons = this;
//		while (currCons != null) {
//			if (currCons.getFirst() != null) result += currCons.getFirst().toString() + " ";
//			currCons = currCons.getRestAsCons();
//		} 
//		result = result.trim();
//		return getIndent(position) + result + ")";
	}

	private String printIf(int position) {
		String result = getIndent(position) + "(" + getFirst() + "\n";
		Cons currCons =  getRestAsCons();
		while (currCons != null) {
			if (currCons.getFirst() != null) 
				result += currCons.getFirst().print(position + 2) + "\n";
			currCons = currCons.getRestAsCons();
		}
		return result + getIndent(position) + ")";
	}

	private String printDefine(int position) {
		String result = getIndent(position) + "(" + getFirst() + " ";
		result += getSecond() + "\n";
		Cons currCons = getRestAsCons().getRestAsCons();
		while (currCons != null) {
			if (currCons.getFirst() != null) 
				result += currCons.getFirst().print(position + 2) + "\n";
			currCons = currCons.getRestAsCons();
		}
		return result + getIndent(position) + ")";
	}
	
	private String printRegular(int position) {
		String result = "(";
		Cons currCons = this;
		while (currCons != Cons.NIL) {
			result += currCons.getFirst().toString() + " ";
			if (!(currCons.rest instanceof Cons)) {
				result += ". " + currCons.rest;
				break;
			}
			currCons = currCons.getRestAsCons();
		} 
		result = result.trim();
		return this.getIndent(position) + result + ")";
	}
	
	public int length() {
		int retVal = 0;
		Cons curr = this;
		while (curr != NIL) {
			retVal++;
			curr = curr.getRestAsCons();
		}
		return retVal;
	}
}

abstract class Proc extends Node {

	@Override
	String print(int position) {
		return "No toString for Func";
	}
	
    @Override
    Node eval(Environment env) {
        return this;
    }
    
	abstract Node apply(Cons args, Environment env);
}

class Lambda extends Proc {

	private final Node body;
	private final Cons params;

	public Lambda(Cons params, Node body) {
		this.params = params;
		this.body = body;
	}

	@Override
	Node apply(Cons args, Environment env) {
		Environment frame = new Environment(env);
		bindArgumentsToFrame(args, params, frame);
		return body.eval(frame);
	}

	private void bindArgumentsToFrame(Cons args, Cons params, Environment frame) {
		if (args == Cons.NIL)
			return;
		frame.assoc((Ident) params.getFirst(), args.getFirst().eval(frame));
		bindArgumentsToFrame(args.getRestAsCons(), params.getRestAsCons(), frame);
	}
	
}


enum SpecialForm {
	REGULAR, DEFINE, IF, QUOTED, LAMBDA;

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
		return REGULAR;
	}
}