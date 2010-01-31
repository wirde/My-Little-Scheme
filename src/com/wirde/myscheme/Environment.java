package com.wirde.myscheme;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import com.wirde.myscheme.node.Ident;
import com.wirde.myscheme.node.Node;
import com.wirde.myscheme.node.PrettyPrintVisitor;

public class Environment {

    private Map<String, Node> bindings = new HashMap<String, Node>();

    private Environment parent;

    public Environment(Environment parent) {
        this.parent = parent;
    }

    public Environment() {
        bindings.putAll(Primitives.getPrimitives());
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
        evalFile(file, null);
    }

    public void evalFile(String file, PrintStream out) throws IOException {
        Reader reader = new FileReader(file);
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
                throw new ParseException("Error reading file: " + file);
        } finally {
            reader.close();
        }
    }

    public void setParent(Environment env) {
        parent = env;
    }
}