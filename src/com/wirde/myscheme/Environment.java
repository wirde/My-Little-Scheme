package com.wirde.myscheme;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import com.wirde.myscheme.node.Cons;
import com.wirde.myscheme.node.Ident;
import com.wirde.myscheme.node.IntLit;
import com.wirde.myscheme.node.Literal;
import com.wirde.myscheme.node.Node;
import com.wirde.myscheme.node.NodeVisitor;
import com.wirde.myscheme.node.PrettyPrintVisitor;
import com.wirde.myscheme.node.PrimitiveProc;

public class Environment extends Literal {
    //TODO: investigate how the null environment should be defined...
    protected static final Environment NULL_ENV = new Environment(false);
    protected static final Environment REPORT_ENV = new Environment(true);

    private Map<String, Node> bindings = new HashMap<String, Node>();

    private Environment parent;

    public Environment(Environment parent) {
        this.parent = parent;
    }

    public Environment(boolean bindCore) {
        if (bindCore)
            bindings.putAll(Primitives.createPrimitives());
        bindings.put("scheme-report-environment", new PrimitiveProc(1, 1) {
            @Override
            public Node doApply(Cons args) {
                if (!args.getFirst().equals(new IntLit(4)))
                    throw new EvalException("Expected version 4");
                return REPORT_ENV;
            }
        });
        bindings.put("scheme-null-environment", new PrimitiveProc(1, 1) {
            @Override
            public Node doApply(Cons args) {
                if (!args.getFirst().equals(new IntLit(4)))
                    throw new EvalException("Expected version 4");
                return NULL_ENV;
            }
        });
    }
    
    public Node lookup(Ident ident) {
        Node res = bindings.get(ident.getName());
        if (res == null) {
            if (parent == null)
                throw new EvalException("Unbound identifier: " + ident);
            else
                return parent.lookup(ident);
        }
        return res;
    }

    public void bind(Ident ident, Node value) {
        bindings.put(ident.getName(), value);
    }

    public void set(Ident ident, Node value) {
        if (lookup(ident) == null)
            throw new EvalException("Unbound variable: " + ident);
        bind(ident, value);
    }

    public void evalFile(String file) throws IOException {
        evalStream(new FileInputStream(file), null);
    }

    public void evalResource(String resource) throws IOException {
        evalStream(Environment.class.getResourceAsStream(resource), null);
    }
    
    public void evalStream(InputStream is, PrintStream out) throws IOException {
        Reader reader = new InputStreamReader(is);
        Parser parser = new Parser(reader);
        try {
            while (true) {
                Node exp = parser.parseNext();
                if (exp == null)
                    break;

                Node res = exp.eval(this);
                if (out != null && res != null) {
                    res.accept(new PrettyPrintVisitor(out));
                    out.println();
                }
            }
            if (reader.read() != -1)
                throw new ParseException("Error reading stream");
        } finally {
            reader.close();
        }
    }

    public void setParent(Environment env) {
        parent = env;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}