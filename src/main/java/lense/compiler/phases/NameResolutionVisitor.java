/**
 * 
 */
package lense.compiler.phases;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import lense.compiler.CompilationError;
import lense.compiler.Import;
import lense.compiler.SemanticContext;
import lense.compiler.ast.BlockNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.ImplementedInterfacesNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.ScopedVariableDefinitionNode;
import lense.compiler.ast.StaticAccessNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.typesystem.LenseTypeDefinition;
import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import compiler.typesystem.TypeDefinition;
import compiler.typesystem.VariableInfo;

/**
 * Distinguisses names of variables and types. Also pre-sets the variable to its scope 
 */
public class NameResolutionVisitor  implements Visitor<AstNode> {

	final ClassTypeNode ct;
	final SemanticContext semanticContext;
	final Set<String> genericNames = new HashSet<String>();

	public NameResolutionVisitor (ClassTypeNode ct){
		this.ct = ct;
		this.semanticContext = ct.getSemanticContext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startVisit() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endVisit() {

	}


	/**
	 * {@inheritDoc}
	 */
	public VisitorNext visitBeforeChildren(AstNode node) {
		if (node instanceof MethodDeclarationNode){
			semanticContext.beginScope(((MethodDeclarationNode)node).getName());
		} else if (node instanceof ClassTypeNode){
			ClassTypeNode t = (ClassTypeNode)node;
			
			t.setName(semanticContext.getCurrentPackageName() + "." + t.getName());

			semanticContext.beginScope(t.getName());

		} else if (node instanceof BlockNode){
			semanticContext.beginScope("block");
		} else if (node instanceof TypeNode) {
			TypeNode typeNode = (TypeNode)node;

			//generic type variables are ignored
			if (genericNames.contains(typeNode.getName())){
				return VisitorNext.Siblings;
			}


			// match the type to one of the imports.
			Optional<Import> match = matchImports(ct, typeNode.getName());
			
			if (match.isPresent()){
				if(node.getParent() instanceof ImplementedInterfacesNode || (ct.getSuperType()!= null && ct.getSuperType().getName().equals(typeNode.getName()))){
					match.get().setMemberCalled(true);
				}
				typeNode.setName(match.get().getTypeName());
				return VisitorNext.Children; 
			}
			
			
			// match the type to one of the global imports, namely lense.core.lang
			for (Import i : ct.imports()){
				if (i.isContainer()){

					Optional<TypeDefinition> libraryType = semanticContext.resolveTypeForName( i.getTypeName().getName() + "." + typeNode.getName(), typeNode.getGenericParametersCount());

					if (libraryType.isPresent()){
						typeNode.setName(new QualifiedNameNode(libraryType.get().getName()));
						final Import implicitType = Import.singleType(new QualifiedNameNode(""), typeNode.getName());
						implicitType.setUsed(true);
						ct.imports().add(implicitType);
						return VisitorNext.Children; 
					} 

				}
			}
			
			// match implicit imports TODO test
			Optional<TypeDefinition> libraryType = semanticContext.resolveTypeForName(typeNode.getName(), typeNode.getGenericParametersCount());

			if (libraryType.isPresent()){
				typeNode.setName(new QualifiedNameNode(libraryType.get().getName()));
				final Import implicitType = Import.singleType(new QualifiedNameNode(""), typeNode.getName());
				implicitType.setUsed(true);
				ct.imports().add(implicitType);
				return VisitorNext.Children; 
			} 


			if (!ct.getName().endsWith(typeNode.getName())){
				// its not  the type it self.
				
				 if (node.getParent() instanceof GenericTypeParameterNode){
					genericNames.add(typeNode.getName());
					return VisitorNext.Siblings;
				}
				throw new CompilationError(node, "Type " + typeNode.getName() + " was not imported in " + ct.getName());
			}

		

		} else if (node instanceof MethodInvocationNode){
			MethodInvocationNode invokeNode = (MethodInvocationNode)node;

			if (invokeNode.getAccess() instanceof QualifiedNameNode){
				QualifiedNameNode original = ((QualifiedNameNode)invokeNode.getAccess());
				return resolveName(ct, node, invokeNode.getAccess(), original);
			}


			return VisitorNext.Children; 
		} else if (node instanceof FieldOrPropertyAccessNode){
			FieldOrPropertyAccessNode fieldNode = (FieldOrPropertyAccessNode)node;

			if (fieldNode.getPrimary() instanceof QualifiedNameNode){
				QualifiedNameNode original = ((QualifiedNameNode)fieldNode.getPrimary());
				return resolveName(ct, node, fieldNode.getPrimary(), original);
			}

			return VisitorNext.Children; 
		}

		return VisitorNext.Children;
	}

	private VisitorNext resolveName(ClassTypeNode ct,
			AstNode parent, AstNode child,
			QualifiedNameNode original) {


		QualifiedNameNode qn = original;
//		Optional<TypeDefinition> maybeType = semanticContext.resolveTypeForName(qn.getName(), 0);
//
//		while(!maybeType.isPresent()){
//			qn = qn.getPrevious();
//			if (qn != null){
//				maybeType = semanticContext.resolveTypeForName(qn.getName(), 0);
//			} else {
//				break;
//			}
//		}
//
//		if (maybeType.isPresent()){
//			return VisitorNext.Children; 
//
//		}
		
		Optional<Import> match = matchImports(ct, original.getName());
		
		if (match.isPresent()){
			match.get().setMemberCalled(true);
			return VisitorNext.Children; 
		}


		String name = original.getFirst().getName();

		VariableInfo varInfo = semanticContext.currentScope().searchVariable(name);

		if (varInfo == null){
			// references a type

			match = matchImports(ct, name);
			if (!match.isPresent()){
				throw new CompilationError(child, "Type " + name + " was not imported");
			}
			
			StaticAccessNode sn = new StaticAccessNode(new TypeNode(original.getFirst()));

			qn = original.getNext();

			FieldOrPropertyAccessNode fieldAccess = new FieldOrPropertyAccessNode(qn.getFirst().getName());
			fieldAccess.setPrimary(sn);
			fieldAccess.setScanPosition(child.getScanPosition());

			qn = qn.getNext();
			while (qn != null){
				FieldOrPropertyAccessNode f = new FieldOrPropertyAccessNode(qn.getFirst().getName());
				f.setPrimary(fieldAccess);
				fieldAccess = f;

				qn = qn.getNext();
			}

			parent.replace(child, fieldAccess);
			return VisitorNext.Children; 
		} else {
			// references a variable

			
			VariableReadNode vn = new VariableReadNode(varInfo.getName(), varInfo);
			qn = original.getNext();

			if (qn == null){
				parent.replace(child, vn);
			} else {
				FieldOrPropertyAccessNode fieldAccess = new FieldOrPropertyAccessNode(qn.getFirst().getName());
				fieldAccess.setPrimary(vn);

				qn = qn.getNext();
				while (qn != null){
					FieldOrPropertyAccessNode f = new FieldOrPropertyAccessNode(qn.getFirst().getName());
					f.setPrimary(fieldAccess);
					fieldAccess = f;

					qn = qn.getNext();
				}

				parent.replace(child, fieldAccess);
			}
		
			return VisitorNext.Children; 
		}



	
	}

	private Optional<Import> matchImports(ClassTypeNode ct, String name) {
		for (Import i : ct.imports()){
			if (i.getMatchAlias().equals(name)){
				i.setUsed(true);
				return Optional.of(i);
			}
		}
		
		return Optional.empty();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitAfterChildren(AstNode node) {


		if (node instanceof MethodDeclarationNode){
			semanticContext.endScope();
		} else if (node instanceof ClassTypeNode){
			semanticContext.endScope();
		} else if (node instanceof BlockNode){
			semanticContext.endScope();
		} else if (node instanceof ScopedVariableDefinitionNode){
			ScopedVariableDefinitionNode variableDeclaration = (ScopedVariableDefinitionNode)node;

			TypeNode typeNode = variableDeclaration.getTypeNode();
			LenseTypeDefinition type = new LenseTypeDefinition(typeNode.getName(), null, null);
			VariableInfo info = semanticContext.currentScope().defineVariable(variableDeclaration.getName(), type);
		
			variableDeclaration.setInfo(info);

		}  else if (node instanceof FormalParameterNode){
			FormalParameterNode variableDeclaration = (FormalParameterNode)node;

			TypeNode typeNode = variableDeclaration.getTypeNode();
			if (typeNode != null){
				LenseTypeDefinition type = new LenseTypeDefinition(typeNode.getName(), null, null);
				semanticContext.currentScope().defineVariable(variableDeclaration.getName(), type);
			}
	
		} 

	}

}
