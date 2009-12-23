package com.wirde.myscheme.node;

import com.wirde.myscheme.Environment;
import com.wirde.myscheme.EvalException;

public class Cons extends Node {
    public static final Cons NIL = new Cons();
    private final Node first;
    private final Node rest;
    
    public Cons() {
        this.first = this.rest = this;
    }
    
    public Cons(Node first, Node rest) {
    	this.first = first;
    	this.rest = rest;
    }
    
    public Node getFirst() {
        return first;
    }
    
    public Node getSecond() {
        return getRestAsCons().first;
    }

    public Node getThird() {
        return getRestAsCons().getRestAsCons().first;
    }
    
    public Node getFourth() {
    	return getRestAsCons().getRestAsCons().getRestAsCons().first;
    }
    
    public Cons getRestAsCons() {
        return (Cons) rest;
    }
    
    public Node getRest() {
        return rest;
    }
    
    public Node eval(Environment env) {
    	SpecialForm special = SpecialForm.toSpecialForm(this);
    	switch (special) {
		case REGULAR:
            Proc proc = (Proc) first.eval(env);        
            return proc.apply(evaluateList(getRestAsCons(), env));
		case DEFINE:
			if (Cons.NIL.equals(getRest()))
				throw new EvalException("Expected identifier, got nil");
			Node definee = getSecond();
			if (definee instanceof Ident) {
				if (Cons.NIL.equals(getRestAsCons().getRest()))
					throw new EvalException("Expected expression, got nil");
				env.define((Ident) definee, getThird().eval(env));
			}
			else if (definee instanceof Cons) {
				Cons lambdaDef = (Cons) definee;
				env.define((Ident) lambdaDef.getFirst(), new Lambda(lambdaDef.rest, getRestAsCons().getRestAsCons(), env));
			} else
				throw new EvalException("Expected Ident or Cons. Got " + definee.getClass(), this);
            return NIL;
		case LAMBDA:
			return new Lambda(getSecond(), getRestAsCons().getRestAsCons(), env);
		case IF:
			if (BoolLit.TRUE.equals(getSecond().eval(env)))
				return getThird().eval(env);
			else
				return getFourth().eval(env);
		case QUOTED:
			return getSecond();
		case SET:
//			getSecond().eval(env).setMe(getThird().eval(env));
			return Cons.NIL;
		default:
			throw new EvalException("Unkown Special form: " + special);
		}
    }
	
	private Cons evaluateList(Cons cons, Environment env) {
		if (cons == null)
			return null;
		
		if (Cons.NIL.equals(cons))
			return (Cons) Cons.NIL;
		
		return new Cons(cons.getFirst().eval(env), evaluateList(cons.getRestAsCons(), env));
	}
    
	@Override
	protected String print(int position) {
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
		String result = "'(";
		Cons currCons = this;
		while (currCons != null) {
			if (currCons.getFirst() != null) result += currCons.getFirst().toString() + " ";
			currCons = currCons.getRestAsCons();
		} 
		result = result.trim();
		return getIndent(position) + result + ")";
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
		while (currCons != Cons.NIL) {
			if (currCons.getFirst() != Cons.NIL) 
				result += currCons.getFirst().print(position + 2) + "\n";
			currCons = currCons.getRestAsCons();
		}
		return result + getIndent(position) + ")";
	}
	
	private String printRegular(int position) {
		String result = "(";
		Cons currCons = this;
		while (!Cons.NIL.equals(currCons)) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((rest == null) ? 0 : rest.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Cons))
			return false;
		Cons other = (Cons) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (rest == null) {
			if (other.rest != null)
				return false;
		} else if (!rest.equals(other.rest))
			return false;
		return true;
	}
}