package com.wirde.myscheme.node;

import com.wirde.myscheme.Environment;
import com.wirde.myscheme.EvalException;


public class Lambda extends Proc {

	private final Cons body;
	private final Node params;
	private final Environment capturedEnv;

	public Lambda(Node params, Cons body, Environment env) {
		this.params = params;
		this.body = body;
		capturedEnv = env;
	}

	@Override
	public Node apply(Cons args) {
		Environment frame = new Environment(capturedEnv);
		
		bindArgumentsToFrame(args, params, frame);
		
		//Evaluate the expressions in the body
		Cons next = body;
		Node result = null;
		while (!Cons.NIL.equals(next)) {
			result = next.getFirst().eval(frame);
			next = next.getRestAsCons();
		}
		return result;
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
					throw new EvalException("Too many arguments, expected: " + this.params, this);
				return;
				}
			frame.bind((Ident) paramsCons.getFirst(), args.getFirst());
			bindArgumentsToFrame(args.getRestAsCons(), paramsCons.getRest(), frame);
		} else {
			if (Cons.NIL.equals(args))
				throw new EvalException("Expected argument: " + this.params);
			frame.bind((Ident) params, args);
		}
	}

	public String toString() {
		return "#<procedure>";
	}
}
