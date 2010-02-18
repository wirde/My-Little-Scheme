package com.wirde.myscheme.node;

import java.util.Iterator;

import com.wirde.myscheme.Environment;

public class Cons extends Node implements Iterable<Cons> {
    public static final Cons NIL = new Cons();
    //Can't be final, must be able to set using set-car! and set-cdr!
    private Node first;  
    private Node rest;
    
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
    
    public void setFirst(Node first) {
        this.first = first;
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
    
    public void setRest(Node rest) {
        this.rest = rest;
    }
    
    public Node eval(Environment env) {
        SpecialForm special = SpecialForm.toSpecialForm(this);
        return special.evalForm(this, env);
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