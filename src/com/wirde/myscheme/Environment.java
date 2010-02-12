package com.wirde.myscheme;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
}