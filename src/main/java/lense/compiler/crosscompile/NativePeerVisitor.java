package lense.compiler.crosscompile;

import java.io.File;
import java.util.Map;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import lense.compiler.CompilationError;
import lense.compiler.ast.ArgumentListItemNode;
import lense.compiler.ast.BlockNode;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.Method;
import lense.compiler.type.MethodParameter;
import lense.compiler.type.MethodReturn;
import lense.compiler.typesystem.Visibility;

public final class NativePeerVisitor implements Visitor<AstNode>{

	Map<String, File> nativeTypes;
	
	public NativePeerVisitor(Map<String, File> nativeTypes) {
		this.nativeTypes = nativeTypes;
	}

	@Override
	public void visitAfterChildren(AstNode node) {
		
		if (node instanceof MethodDeclarationNode) {
			MethodDeclarationNode m = (MethodDeclarationNode)node;
			
			if (m.isNative() && !m.getProperty("peer", Boolean.class).orElse(false)) {
				var name = m.getMethod().getDeclaringType().getName();
				
				if (!nativeTypes.containsKey(name + "__Peer")) {
					throw new CompilationError(node, "Peer not found for class " + name);
				}
				
				var block = m.getBlock();
				
				if (block != null) {
						throw new CompilationError(node, "Constructor cannot be native and have and implementation");
				}
				
				ArgumentListItemNode[] params = new ArgumentListItemNode[m.getParameters().getChildren().size()];
				MethodParameter[] mparams = new MethodParameter[m.getParameters().getChildren().size()];
				
				int i = 0;
				for (var it : m.getParameters().getChildren(FormalParameterNode.class)) {
					params[i] = new ArgumentListItemNode(i, new VariableReadNode(it.getName()));
					mparams[i] = new MethodParameter(it.getTypeVariable());
					i++;
				}
				
				var type = new LenseTypeDefinition(name + "__Peer", LenseUnitKind.Peer, null);
				
				var method = new Method(false, Visibility.Public, m.getName(), new MethodReturn(m.getMethod().getReturningType()) );
				method.setDeclaringType(type);
				
				MethodInvocationNode invoke = new MethodInvocationNode(null, m.getName(), params);
				invoke.setTypeVariable(type);
				invoke.setTypeMember(method);
				invoke.setStaticInvocation(true);
				
				block = new BlockNode(new ReturnNode(invoke));
				
				m.setBlock(block);
				m.setProperty("peer", true);

			}
		} else if (node instanceof ConstructorDeclarationNode) {
			ConstructorDeclarationNode m = (ConstructorDeclarationNode)node;
			
			if (m.isNative() && !node.getProperty("peer", Boolean.class).orElse(false)) {
				var name = m.getConstructor().getDeclaringType().getName();
				
				if (!nativeTypes.containsKey(name + "__Peer")) {
					throw new CompilationError(node, "Peer not found for class " + name);
				}
				
				var block = m.getBlock();
				
				if (block != null) {
						throw new CompilationError(node, "Constructor cannot be native and have and implementation");
				}
				
				ArgumentListItemNode[] params = new ArgumentListItemNode[m.getParameters().getChildren().size()];
				MethodParameter[] mparams = new MethodParameter[m.getParameters().getChildren().size()];
				
				int i = 0;
				for (var it : m.getParameters().getChildren(FormalParameterNode.class)) {
					params[i] = new ArgumentListItemNode(i, new VariableReadNode(it.getName()));
					mparams[i] = new MethodParameter(it.getTypeVariable());
					i++;
				}
				
				var type = new LenseTypeDefinition(name + "__Peer", LenseUnitKind.Peer, null);
				
				var method = new Method(false, Visibility.Public, m.getName(), new MethodReturn(m.getConstructor().getDeclaringType()) );
				method.setDeclaringType(type);
				
				MethodInvocationNode invoke = new MethodInvocationNode(null, m.getName(), params);
				invoke.setTypeVariable(type);
				invoke.setTypeMember(method);
				invoke.setStaticInvocation(true);
				
				block = new BlockNode(new ReturnNode(invoke));
				node.setProperty("peer", true);
				m.setBlock(block);
			

			}
		}
	}


}
