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
import lense.compiler.ast.ArgumentListNode;
import lense.compiler.ast.BlockNode;
import lense.compiler.ast.ClassInstanceCreation;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.ImplementedInterfacesNode;
import lense.compiler.ast.LiteralTupleInstanceCreation;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.NativeArrayInstanceCreation;
import lense.compiler.ast.NativeAssociationInstanceCreation;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.ScopedVariableDefinitionNode;
import lense.compiler.ast.StaticAccessNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.TypedNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.typesystem.LenseTypeDefinition;
import lense.compiler.typesystem.LenseTypeSystem;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
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
		
		if (node instanceof NativeArrayInstanceCreation){
			NativeArrayInstanceCreation array = ((NativeArrayInstanceCreation) node);
			ArgumentListNode args = array.getArguments();
			
			TreeTransverser.tranverse(args, this);
			
			TypedNode type = (TypedNode) args.getChildren().get(0);
			array.getTypeNode().addParametricType(new GenericTypeParameterNode( new TypeNode(type.getTypeDefinition()), compiler.typesystem.Variance.Invariant));
			
			return VisitorNext.Siblings;
			
		} else if (node instanceof NativeAssociationInstanceCreation){
			NativeAssociationInstanceCreation map = ((NativeAssociationInstanceCreation) node);
			ArgumentListNode args = map.getArguments();
			
			TypeNode typeNode = ((ClassInstanceCreation)args.getChildren().get(0)).getTypeNode();
			// make all arguments have the same type.
			for (int i =1 ; i < args.getChildren().size();i++){
				final ClassInstanceCreation classInstanceCreation = (ClassInstanceCreation)args.getChildren().get(i);
				classInstanceCreation.replace(classInstanceCreation.getTypeNode(), typeNode);
			}
			TypeDefinition pairType = semanticContext.resolveTypeForName(typeNode.getName(), 2).get();
			typeNode.setTypeDefinition(pairType);
			
			TreeTransverser.tranverse(args, this);
			
			TypedNode key = (TypedNode) args.getChildren().get(1).getChildren().get(1).getChildren().get(0);
			TypedNode value = (TypedNode) args.getChildren().get(1).getChildren().get(1).getChildren().get(1);
			map.getTypeNode().addParametricType(new GenericTypeParameterNode( new TypeNode(key.getTypeDefinition()), compiler.typesystem.Variance.Invariant));
			map.getTypeNode().addParametricType(new GenericTypeParameterNode( new TypeNode(value.getTypeDefinition()), compiler.typesystem.Variance.Invariant));
			typeNode.addParametricType(new GenericTypeParameterNode( new TypeNode(key.getTypeDefinition()), compiler.typesystem.Variance.Invariant));
			typeNode.addParametricType(new GenericTypeParameterNode( new TypeNode(value.getTypeDefinition()), compiler.typesystem.Variance.Invariant));
			typeNode.setTypeDefinition( LenseTypeSystem.specify( typeNode.getTypeDefinition(), key.getTypeDefinition(), value.getTypeDefinition()));
			
			return VisitorNext.Siblings;
		} else if (node instanceof MethodDeclarationNode){
			semanticContext.beginScope(((MethodDeclarationNode)node).getName());
		} else if (node instanceof ClassTypeNode){
			ClassTypeNode t = (ClassTypeNode)node;
			
			t.setName(semanticContext.getCurrentPackageName() + "." + t.getName());

			semanticContext.beginScope(t.getName());

		} else if (node instanceof BlockNode){
			semanticContext.beginScope("block");
		} else if (node instanceof TypeNode) {
			TypeNode typeNode = (TypeNode)node;
			if(typeNode.getTypeDefinition() != null){
				return VisitorNext.Siblings;
			}
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
			
			// match implicit imports 
			Optional<TypeDefinition> libraryType = semanticContext.resolveTypeForName(typeNode.getName(), typeNode.getGenericParametersCount());

			if (libraryType.isPresent()){
				typeNode.setName(new QualifiedNameNode(libraryType.get().getName()));
				final Import implicitType = Import.singleType(new QualifiedNameNode(typeNode.getName()), typeNode.getName());
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
		else if (node instanceof LiteralTupleInstanceCreation){
			LiteralTupleInstanceCreation tuple = ((LiteralTupleInstanceCreation)node);
			TypedNode value = (TypedNode) tuple.getChildren().get(1).getChildren().get(0);
			
			TypeNode typeNode =  new TypeNode(new QualifiedNameNode("lense.core.collections.Tuple"));
			typeNode.addParametricType(new GenericTypeParameterNode(new TypeNode(value.getTypeDefinition())));
			
			TypedNode nextTuple;
			if (tuple.getChildren().get(1).getChildren().size() == 2){
				nextTuple = (TypedNode) tuple.getChildren().get(1).getChildren().get(1);
				typeNode.addParametricType(new GenericTypeParameterNode(new TypeNode(new QualifiedNameNode("lense.core.lang.Any"))));
				
			} else {
				nextTuple = new TypeNode(LenseTypeSystem.Nothing());
				typeNode.addParametricType(new GenericTypeParameterNode(new TypeNode(nextTuple.getTypeDefinition())));
			}
		
				
			tuple.replace(tuple.getTypeNode(), typeNode);
		}

	}

}
