package lense.compiler.phases;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import lense.compiler.CompilationError;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.context.SemanticScope;
import lense.compiler.context.VariableInfo;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

public  abstract class AbstractLenseVisitor implements Visitor<AstNode>{


	protected IntervalTypeVariable resolveTypeDefinition(TypeNode t) {
		try {
			SemanticContext semanticContext = this.getSemanticContext();

			SemanticScope currentScope = semanticContext.currentScope();
			if (currentScope != null){
				VariableInfo variableInfo = currentScope.searchVariable(t.getName());

				if (variableInfo != null && variableInfo.isTypeVariable()){
					// its a generic type parameter
					t.setTypeParameter( new RangeTypeVariable(t.getName(), null, variableInfo.getTypeVariable(), variableInfo.getTypeVariable()));
					return t.getTypeParameter();
				}
			}

			TypeDefinition type = semanticContext.typeForName(t.getName(),t.getTypeParametersCount());
			t.setTypeVariable(new FixedTypeVariable(type));

			if (!type.getGenericParameters().isEmpty() && t.getTypeParametersCount() > 0) {

				TypeVariable[] genericParametersCapture = new TypeVariable[t.getTypeParametersCount()];
				int index = 0;
				for (AstNode n : t.getChildren()) {
					GenericTypeParameterNode p = (GenericTypeParameterNode) n;
					TypeNode gt = p.getTypeNode();
					TypeVariable gtype;
					if (gt!= null){
						gtype =  gt.getTypeVariable();
						if (gtype == null) {
							gtype = new FixedTypeVariable(semanticContext.typeForName(gt.getName(),gt.getTypeParametersCount()));
						}
						genericParametersCapture[index] = gtype;
						gt.setTypeVariable(genericParametersCapture[index]);
					} else {
						gtype = new FixedTypeVariable( semanticContext.resolveTypeForName("lense.core.lang.Any", 0).get());
						genericParametersCapture[index] = gtype;
					}
					index++;
				}
				type = LenseTypeSystem.specify(type,genericParametersCapture);
				t.setTypeVariable(type);
			}

			t.setTypeParameter(  new FixedTypeVariable( type).toIntervalTypeVariable());
			return t.getTypeParameter();
		} catch (lense.compiler.type.TypeNotFoundException e) {
			throw new CompilationError(t, e.getMessage());
		}
	}

	protected abstract SemanticContext getSemanticContext();
}
