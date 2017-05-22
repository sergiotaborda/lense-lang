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
import lense.compiler.type.variable.DeclaringTypeBoundedTypeVariable;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.GenericTypeBoundToDeclaringTypeVariable;
import lense.compiler.type.variable.IntervalTypeVariable;
import lense.compiler.type.variable.RangeTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Variance;

public  abstract class AbstractLenseVisitor implements Visitor<AstNode>{


	protected IntervalTypeVariable resolveTypeDefinition(TypeNode t) {
		
		if (t.getTypeParameter() != null){
			return t.getTypeParameter();
		}
		
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
			
			if ( t.getTypeParametersCount() > 0) {

				TypeVariable[] genericParametersCapture = new TypeVariable[t.getTypeParametersCount()];
				int index = 0;
				for (AstNode n : t.getChildren()) {
					TypeNode typeNode = ((GenericTypeParameterNode) n).getTypeNode();
					TypeVariable typeVariable;
					if (typeNode!= null){
						typeVariable =  typeNode.getTypeVariable();
						if (typeVariable == null) {
							VariableInfo variableInfo = currentScope.searchVariable(typeNode.getName());
							if (variableInfo.isTypeVariable()){

								typeVariable = new GenericTypeBoundToDeclaringTypeVariable(type, currentScope.getCurrentType(), index , typeNode.getName(),  Variance.Covariant);
								
							} else {
								typeVariable = new FixedTypeVariable(semanticContext.typeForName(typeNode.getName(),typeNode.getTypeParametersCount()));
							}
							
						}
						genericParametersCapture[index] = typeVariable;
						typeNode.setTypeVariable(genericParametersCapture[index]);
					} else {
						typeVariable = new FixedTypeVariable( semanticContext.resolveTypeForName("lense.core.lang.Any", 0).get());
						genericParametersCapture[index] = typeVariable;
					}
					index++;
				}
				type = LenseTypeSystem.specify(type,genericParametersCapture);
				t.setTypeVariable(type);
			}
			
			t.setTypeVariable(new FixedTypeVariable(type));
			t.setTypeParameter(  t.getTypeVariable().toIntervalTypeVariable());
		
			
			return t.getTypeParameter();
		} catch (lense.compiler.type.TypeNotFoundException e) {
			throw new CompilationError(t, e.getMessage());
		}
	}

	protected abstract SemanticContext getSemanticContext();
}
