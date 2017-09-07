package lense.compiler.phases;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import lense.compiler.context.SemanticContext;

public  abstract class AbstractLenseVisitor implements Visitor<AstNode>{


   

    protected abstract SemanticContext getSemanticContext();
}
