package com.wirde.myscheme.node;

import java.util.Iterator;

import com.wirde.myscheme.Environment;
import com.wirde.myscheme.EvalException;

public class Cons extends Node implements Iterable<Cons> {
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
			if (Cons.NIL == getRest())
				throw new EvalException("Expected identifier, got nil");
			Node definee = getSecond();
			if (definee instanceof Ident) {
				if (Cons.NIL == getRestAsCons().getRest())
					throw new EvalException("Expected expression, got nil");
				env.bind((Ident) definee, getThird().eval(env));
			}
			else if (definee instanceof Cons) {
				Cons lambdaDef = (Cons) definee;
				env.bind((Ident) lambdaDef.getFirst(), new Lambda(lambdaDef.rest, getRestAsCons().getRestAsCons(), env));
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
			env.set((Ident) getSecond(), getThird());
			return Cons.NIL;
		case BEGIN:
		    Cons exps = getRestAsCons();
		    Node result = NIL;
		    while (!exps.equals(NIL)) {
		        result = exps.getFirst().eval(env);
		        exps = exps.getRestAsCons();
		    }
		    return result;
		case LET:
		    Cons params = Cons.NIL;
		    Cons args = Cons.NIL;
		    Cons paramList = (Cons) getSecond();
		    Cons body = getRestAsCons().getRestAsCons();
		    while (!paramList.equals(Cons.NIL)) {
		        Cons paramArgPair = (Cons) paramList.getFirst();
		        params = new Cons(paramArgPair.getFirst(), params);
		        args = new Cons(paramArgPair.getSecond(), args);
		        paramList = paramList.getRestAsCons();
		    }
		    return new Lambda(params, body, env).apply(args);
		case COND:
		    Cons condClauses = getRestAsCons();
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
            return Cons.NIL;
		case AND:
		    Node res = BoolLit.TRUE;
		    for (Cons currentCons : getRestAsCons()) {
		        res = currentCons.getFirst().eval(env);
		        if (!BoolLit.isTrue(res))
		            return BoolLit.FALSE;
		    }
		    return res;
		case OR:
		    res = BoolLit.FALSE;
		    for (Cons currentCons : getRestAsCons()) {
		        res = currentCons.getFirst().eval(env); 
                if (BoolLit.isTrue(res))
                    return res;
            }
            return BoolLit.FALSE;
		case DO:
		    //TODO: Implement
		    return Cons.NIL;
		case SET_CAR:
		    //TODO: Implement
            return Cons.NIL;
		case SET_CDR:
		    //TODO: Implement
		    return Cons.NIL;
		case CASE:
            //TODO: Implement
            return Cons.NIL;		    
		default:
			throw new EvalException("Unkown Special form: " + special);
		}
    }
	
	private Cons evaluateList(Cons cons, Environment env) {
		if (cons == null)
			return null;
		
		if (Cons.NIL == cons)
			return Cons.NIL;
		
		return new Cons(cons.getFirst().eval(env), evaluateList(cons.getRestAsCons(), env));
	}
	
	@Override
	public void accept(NodeVisitor visitor) {
	    visitor.visit(this);
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

	//FIXME!
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (this == Cons.NIL)
		    return result;
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
	
	@Override
    public String toString() {
        String result = ("(");
        Cons currCons = this;
        while (currCons != NIL) {
            result += currCons.getFirst();
            if (!(currCons.getRest() instanceof Cons)) {
                result += " . ";
                result += currCons.getRest();
                break;
            }
            if (currCons.getRestAsCons() != Cons.NIL)
                result += " ";
            currCons = currCons.getRestAsCons();
        }
        result += ")";
        return result;
    }

    @Override
    public Iterator<Cons> iterator() {
        return new ConsIterator(this);
    }
    
    private static class ConsIterator implements Iterator<Cons> {

        private Cons currentCons;

        public ConsIterator(Cons cons) {
            currentCons = cons;
        }

        @Override
        public boolean hasNext() {
            return currentCons != NIL;
        }

        @Override
        public Cons next() {
            Cons res = currentCons;
            currentCons = currentCons.getRestAsCons();
            return res;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}