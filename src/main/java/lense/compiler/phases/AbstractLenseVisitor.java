package lense.compiler.phases;

import java.util.Optional;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import lense.compiler.CompilationError;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.NewInstanceCreationNode;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.TypedNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.type.Constructor;
import lense.compiler.type.ConstructorParameter;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

public  abstract class AbstractLenseVisitor implements Visitor<AstNode>{


	@Override
	public void startVisit() {
		// no-op
	}

	@Override
	public void endVisit() {
		// no-op
	}

    protected abstract SemanticContext getSemanticContext();
    
}
