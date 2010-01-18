package com.wirde.myscheme.node;

import com.wirde.myscheme.EvalException;

public abstract class PrimitiveProc extends Proc {
    
    private final int minArgs;
    private final int maxArgs;

    public PrimitiveProc(int minArgs) {
        this.minArgs = minArgs;
        maxArgs = Integer.MAX_VALUE;
    }
            
    public PrimitiveProc(int minArgs, int maxArgs) {
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
    }

    @Override
    public Node apply(Cons args) {
        int nrArgs = args.length();
        if (nrArgs < minArgs)
            throw new EvalException("Too few arguments. Expected: " + minArgs + ", got: " + nrArgs);
        if (nrArgs > maxArgs)
            throw new EvalException("Too many arguments. Expected: " + maxArgs + ", got: " + nrArgs);
        return doApply(args);
    }

    public abstract Node doApply(Cons args);
    
    @Override
    public String toString() {
        return "#<native-procedure>";
    }
}
