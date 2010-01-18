package com.wirde.myscheme.node;

public interface NodeVisitor {
    void visit(Proc proc);

    void visit(Ident ident);

    void visit(Cons cons);

    void visit(StrLit str);

    void visit(BoolLit bool);

    void visit(IntLit intLit);
}
