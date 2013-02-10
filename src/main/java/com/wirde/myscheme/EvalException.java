package com.wirde.myscheme;

import com.wirde.myscheme.node.Node;

public class EvalException extends RuntimeException {

	private static final long serialVersionUID = 8417254090907978402L;

	public EvalException(String message) {
		super(message);
	}

	public EvalException(String message, Node node) {
		super(message + "\nContext:\n" + node);
	}

}
