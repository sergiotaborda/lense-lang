/**
 * 
 */
package lense.compiler.crosscompile.java.ast;

import java.util.stream.Collectors;

import compiler.typesystem.MethodParameter;


/**
 * 
 */
public class ArgumentListNode extends JavaAstNode {

	
	public MethodParameter[] asMethodParameters(){
		return this.getChildren().stream().map(v -> new MethodParameter(((TypedNode)v).getTypeDefinition()))
		.collect(Collectors.toList())
		.toArray(new MethodParameter[0]);
	}
}
