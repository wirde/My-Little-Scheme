package com.wirde.myscheme.node;

import java.util.Arrays;

import com.wirde.myscheme.Environment;

public class Vector extends Node {
    
    private final Node[] contents;

    public Vector(int size, Node init) {
        this(size);
        for (int i = 0; i < size; i++)
            contents[i] = init; //TODO: should probably be *copy* of init...
    }
    public Vector(int size) {
        contents = new Node[size];
    }

    public Node[] getContents() {
        return contents;
    }
    
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Node eval(Environment env) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(contents);
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vector other = (Vector) obj;
        if (!Arrays.equals(contents, other.contents))
            return false;
        return true;
    }
    @Override
    public String toString() {
        String result = "#(";
        if (contents.length > 0)
            result += contents[0];
        for (int i = 1; i < contents.length; i++) {
            result += " " + contents[i];
        }
        result += ")";
        return result;
    }
}
