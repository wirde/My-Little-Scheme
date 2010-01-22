package com.wirde.myscheme.node;

import java.io.PrintStream;

public class PrettyPrintVisitor implements NodeVisitor {

    private int position = 0;
    private final PrintStream out;

    public PrettyPrintVisitor(PrintStream out) {
        this.out = out;
    }

    private void print(String str) {
        out.print(str);
    }
    
    @Override
    public void visit(StrLit str) {
        print("\"" + str + "\"");
    }

    @Override
    public void visit(BoolLit bool) {
        print(bool.toString());
    }

    @Override
    public void visit(Proc proc) {
        print(proc.toString());
    }

    @Override
    public void visit(Ident ident) {
        print(ident.toString());
    }

    @Override
    public void visit(Cons cons) {
        SpecialForm special = SpecialForm.toSpecialForm(cons);
        switch (special) {
        case REGULAR:
            printRegular(cons);
            break;
        case DEFINE:
            printDefine(cons);
            break;
        case IF:
            printIf(cons);
            break;
        case QUOTED:
            printQuoted(cons);
            break;
        case LAMBDA:
            // TODO: Pretty print
            // fall-through
        default:
            printRegular(cons);
        }
    }

    private void printQuoted(Cons cons) {
        print("'(");
        for (Cons currCons : cons) {
            currCons.getFirst().accept(this);
            if (!currCons.getRestAsCons().equals(Cons.NIL))
                print(" ");
        }
        print(")");
    }

    private void printIf(Cons cons) {
        print("(");
        cons.getFirst().accept(this);
        print("\n");
        position += 2;
        for (Cons currCons : cons.getRestAsCons()) {
            indent();
            currCons.getFirst().accept(this);
            if (currCons.getRestAsCons() != Cons.NIL)
                print("\n");
        }
        position -=2;
        print(")");
    }

    private void printDefine(Cons cons) {
        print("(");
        cons.getFirst().accept(this);
        print(" ");
        cons.getSecond().accept(this);
        print("\n");
        position += 2;
        for (Cons currCons : cons.getRestAsCons().getRestAsCons()) {
            indent();
            currCons.getFirst().accept(this);
            if (!currCons.getRestAsCons().equals(Cons.NIL))
                print("\n");
        }
        position -= 2;
        print(")");
    }

    private void printRegular(Cons cons) {
        print(cons.toString());
    }

    private void indent() {
        String indent = "";
        for (int i = 0; i < position; i++) {
            indent += " ";
        }
        print(indent);
    }

    @Override
    public void visit(IntLit intLit) {
        print(intLit.toString());
    }
}