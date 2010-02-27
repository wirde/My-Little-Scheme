package com.wirde.myscheme.node;

import com.wirde.myscheme.Environment;

public interface NodeVisitor {
    void visit(Proc proc);

    void visit(Ident ident);

    void visit(Cons cons);

    void visit(StrLit str);

    void visit(BoolLit bool);

    void visit(IntLit intLit);

    void visit(CharLit charLit);

    void visit(Environment environment);

    void visit(Vector vector);

    void visit(Thunk thunk);
}