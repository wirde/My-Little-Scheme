package com.wirde.myscheme.node;

import com.wirde.myscheme.Environment;
import com.wirde.myscheme.EvalException;


public class Closure extends Proc {

	private final Cons body;
	private final Node params;
	private final Environment capturedEnv;

	public Closure(Node params, Cons body, Environment env) {
		this.params = params;
		this.body = body;
		capturedEnv = env;
	}

	@Override
	public Node apply(Cons args, boolean forceEvaluation) {
		Environment frame = new Environment(capturedEnv);
		
		bindArgumentsToFrame(args, params, frame);
		
		//Evaluate the expressions in the body
		Cons currCons = body;
		while (currCons.getRest() != Cons.NIL) {
		   currCons.getFirst().eval(frame, true);
		   currCons = currCons.getRestAsCons();
		}
		//If we don't have to force the evaluation, return a thunk instead and use the trampoline "later". If 
		//we do need to force the evaluation, trampoline until we get a result.
		//Should give proper tail-calls...
		if (forceEvaluation) {
		    Node result = currCons.getFirst().eval(frame, false);
		    while (result instanceof Thunk)
		        result = result.eval(null, false); 
		    return result;
		} else 
		    return new Thunk(currCons.getFirst(), frame);
	}

	private void bindArgumentsToFrame(Cons args, Node params, Environment frame) {
		if (params instanceof Cons) {
			Cons paramsCons = (Cons) params;
			if (Cons.NIL == args) {
				if (Cons.NIL != params)
					throw new EvalException("Too few arguments, expected: " + this.params, this);
				return;
			}
			if (Cons.NIL == params) {
				if (Cons.NIL != args)
					throw new EvalException("Too many arguments, expected: " + this.params + " remaining: " + args, this);
				return;
				}
			frame.bind((Ident) paramsCons.getFirst(), args.getFirst());
			bindArgumentsToFrame(args.getRestAsCons(), paramsCons.getRest(), frame);
		} else {
			if (Cons.NIL == args && !(params instanceof Node))
				throw new EvalException("Expected argument: " + this.params);
			frame.bind((Ident) params, args);
		}
	}

	public String toString() {
		return "#<procedure>";
	}
}
