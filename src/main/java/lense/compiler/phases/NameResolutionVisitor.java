/**
 * 
 */
package lense.compiler.phases;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.CompilationError;
import lense.compiler.Import;
import lense.compiler.ast.ArgumentListNode;
import lense.compiler.ast.BlockNode;
import lense.compiler.ast.ClassInstanceCreationNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.ImplementedInterfacesNode;
import lense.compiler.ast.LiteralAssociationInstanceCreation;
import lense.compiler.ast.LiteralSequenceInstanceCreation;
import lense.compiler.ast.LiteralTupleInstanceCreation;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.ScopedVariableDefinitionNode;
import lense.compiler.ast.StaticAccessNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.TypedNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.context.SemanticContext;
import lense.compiler.context.VariableInfo;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeDefinition;
import lense.compiler.type.variable.FixedTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;

/**
 * Distinguishes names of variables and types. Also pre-sets the variable to its
 * scope
 */
public class NameResolutionVisitor implements Visitor<AstNode> {

	final ClassTypeNode ct;
	final SemanticContext semanticContext;
	final Set<String> genericNames = new HashSet<String>();

	public NameResolutionVisitor(ClassTypeNode ct) {
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

		if (node instanceof ClassTypeNode) {
			ClassTypeNode n = (ClassTypeNode) node;
			this.semanticContext.beginScope("top");

			String name = semanticContext.getCurrentPackageName() + "." + n.getName();
			Optional<TypeDefinition> type = semanticContext.resolveTypeForName(name, n.getGenericParametersCount());

			if (!type.isPresent()) {
				LenseTypeDefinition defType = new LenseTypeDefinition(name, n.getKind(), null);
				type = Optional.of(semanticContext.registerType(defType, n.getGenericParametersCount()));
			}

			this.semanticContext.currentScope().defineTypeVariable("this", new FixedTypeVariable(type.get()), node);

		} else if (node instanceof NumericValue){
			NumericValue n = (NumericValue)node;

			if (!ct.getName().equals(n.getTypeVariable().getTypeDefinition().getSimpleName())){
				Optional<Import> match = matchImports(ct, n.getTypeVariable().getTypeDefinition().getSimpleName());

				if (match.isPresent()) {
					match.get().setMemberCalled(true);
					return VisitorNext.Siblings;
				}
				// TODO auto import literal types
				//				throw new CompilationError(node,
				//						"Type " + n.getTypeVariable().getName() + " was not imported in " + ct.getName());
			} 

		} else if (node instanceof LiteralSequenceInstanceCreation) {
			LiteralSequenceInstanceCreation array = ((LiteralSequenceInstanceCreation) node);
			ArgumentListNode args = array.getArguments();

			TreeTransverser.transverse(args, this);

			TypedNode type = (TypedNode) args.getChildren().get(0);
			array.getTypeNode().addParametricType(new GenericTypeParameterNode(new TypeNode(type.getTypeVariable()),
					lense.compiler.typesystem.Variance.Invariant));

			return VisitorNext.Siblings;

		} else if (node instanceof LiteralAssociationInstanceCreation) {
			LiteralAssociationInstanceCreation map = ((LiteralAssociationInstanceCreation) node);
			ArgumentListNode args = map.getArguments();

			TypeNode typeNode = ((ClassInstanceCreationNode) args.getChildren().get(0)).getTypeNode();
			// make all arguments have the same type.
			for (int i = 1; i < args.getChildren().size(); i++) {
				final ClassInstanceCreationNode classInstanceCreation = (ClassInstanceCreationNode) args.getChildren()
						.get(i);
				classInstanceCreation.replace(classInstanceCreation.getTypeNode(), typeNode);
			}
			TypeDefinition pairType = semanticContext.resolveTypeForName(typeNode.getName(), 2).get();
			typeNode.setTypeVariable(new FixedTypeVariable(pairType));

			TreeTransverser.transverse(args, this);

			TypedNode key = (TypedNode) args.getChildren().get(1).getChildren().get(1).getChildren().get(0);
			TypedNode value = (TypedNode) args.getChildren().get(1).getChildren().get(1).getChildren().get(1);
			map.getTypeNode().addParametricType(new GenericTypeParameterNode(new TypeNode(key.getTypeVariable()),
					lense.compiler.typesystem.Variance.Invariant));
			map.getTypeNode().addParametricType(new GenericTypeParameterNode(new TypeNode(value.getTypeVariable()),
					lense.compiler.typesystem.Variance.Invariant));
			typeNode.addParametricType(new GenericTypeParameterNode(new TypeNode(key.getTypeVariable()),
					lense.compiler.typesystem.Variance.Invariant));
			typeNode.addParametricType(new GenericTypeParameterNode(new TypeNode(value.getTypeVariable()),
					lense.compiler.typesystem.Variance.Invariant));
			typeNode.setTypeVariable(new FixedTypeVariable(LenseTypeSystem.specify(typeNode.getTypeVariable(),
					key.getTypeVariable(), value.getTypeVariable())));

			return VisitorNext.Siblings;
		} else if (node instanceof MethodDeclarationNode) {
			semanticContext.beginScope(((MethodDeclarationNode) node).getName());
		} else if (node instanceof ConstructorDeclarationNode) {
			ConstructorDeclarationNode c = (ConstructorDeclarationNode) node;
			semanticContext.beginScope(c.getScopeIdentifer());

		} else if (node instanceof ClassTypeNode) {
			ClassTypeNode t = (ClassTypeNode) node;

			t.setName(semanticContext.getCurrentPackageName() + "." + t.getName());

			semanticContext.beginScope(t.getName());

		} else if (node instanceof BlockNode) {
			semanticContext.beginScope("block");
		} else if (node instanceof FieldDeclarationNode) {
			FieldDeclarationNode fieldDeclarationNode = (FieldDeclarationNode) node;

			Optional<Import> match = matchImports(ct, fieldDeclarationNode.getTypeNode().getName());

			if (match.isPresent()) {
				match.get().setMemberCalled(true);

				fieldDeclarationNode.getTypeNode().setName(match.get().getTypeName());
				return VisitorNext.Siblings;
			}

			throw new CompilationError(node,
					"Type " + fieldDeclarationNode.getTypeNode().getName() + " was not imported in " + ct.getName());
		} else if (node instanceof TypeNode) {
			TypeNode typeNode = (TypeNode) node;
			if (typeNode.getTypeVariable() != null) {
				return VisitorNext.Siblings;
			}
			// generic type variables are ignored
			if (genericNames.contains(typeNode.getName())) {
				return VisitorNext.Siblings;
			}

			// match the type to one of the imports.
			Optional<Import> match = matchImports(ct, typeNode.getName());

			if (match.isPresent()) {
				if (node.getParent() instanceof ImplementedInterfacesNode
						|| (ct.getSuperType() != null && ct.getSuperType().getName().equals(typeNode.getName()))) {
					match.get().setMemberCalled(true);
				}
				typeNode.setName(match.get().getTypeName());
				return VisitorNext.Children;
			}

			// match the type to one of the global imports, namely
			// lense.core.lang
			for (Import i : ct.imports()) {
				if (i.isContainer()) {

					Optional<TypeDefinition> libraryType = semanticContext.resolveTypeForName(
							i.getTypeName().getName() + "." + typeNode.getName(), typeNode.getTypeParametersCount());

					if (libraryType.isPresent()) {
						typeNode.setName(new QualifiedNameNode(libraryType.get().getName()));
						final Import implicitType = Import.singleType(new QualifiedNameNode(""), typeNode.getName());
						implicitType.setUsed(true);
						ct.imports().add(implicitType);
						return VisitorNext.Children;
					}

				}
			}

			// match implicit imports
			Optional<TypeDefinition> libraryType = semanticContext.resolveTypeForName(typeNode.getName(),
					typeNode.getTypeParametersCount());

			if (libraryType.isPresent()) {
				typeNode.setName(new QualifiedNameNode(libraryType.get().getName()));
				final Import implicitType = Import.singleType(new QualifiedNameNode(typeNode.getName()),
						typeNode.getName());
				implicitType.setUsed(true);
				ct.imports().add(implicitType);
				return VisitorNext.Children;
			}

			if (!ct.getName().endsWith(typeNode.getName())) {
				// its not the type it self.

				if (node.getParent() instanceof GenericTypeParameterNode) {
					genericNames.add(typeNode.getName());
					return VisitorNext.Siblings;
				}
				throw new CompilationError(node, "Type " + typeNode.getName() + " was not imported in " + ct.getName());
			}

		} else if (node instanceof MethodInvocationNode) {
			MethodInvocationNode invokeNode = (MethodInvocationNode) node;

			if (invokeNode.getAccess() instanceof QualifiedNameNode) {
				QualifiedNameNode original = ((QualifiedNameNode) invokeNode.getAccess());
				resolveName(ct, node, invokeNode.getAccess(), original);
			}

			return VisitorNext.Children;
		} else if (node instanceof FieldOrPropertyAccessNode) {
			FieldOrPropertyAccessNode fieldNode = (FieldOrPropertyAccessNode) node;

			if (fieldNode.getPrimary() instanceof QualifiedNameNode) {
				QualifiedNameNode original = ((QualifiedNameNode) fieldNode.getPrimary());
				return resolveName(ct, node, fieldNode.getPrimary(), original);
			}

			return VisitorNext.Children;
		}

		return VisitorNext.Children;
	}

	private VisitorNext resolveName(ClassTypeNode ct, AstNode parent, AstNode child, QualifiedNameNode original) {

		QualifiedNameNode qn = original;
		// Optional<TypeDefinition> maybeType =
		// semanticContext.resolveTypeForName(qn.getName(), 0);
		//
		// while(!maybeType.isPresent()){
		// qn = qn.getPrevious();
		// if (qn != null){
		// maybeType = semanticContext.resolveTypeForName(qn.getName(), 0);
		// } else {
		// break;
		// }
		// }
		//
		// if (maybeType.isPresent()){
		// return VisitorNext.Children;
		//
		// }

		Optional<Import> match = matchImports(ct, original.getName());

		if (match.isPresent()) {
			match.get().setMemberCalled(true);
			return VisitorNext.Children;
		}

		String name = original.getFirst().getName();

		VariableInfo varInfo = semanticContext.currentScope().searchVariable(name);

		if (varInfo == null) {
			// references a type

			match = matchImports(ct, name);
			if (!match.isPresent()) {
				VariableInfo varSelf = semanticContext.currentScope().searchVariable("this");

				if (!varSelf.getTypeVariable().getName().equals(name)) {
					// throw new CompilationError(child, "Type " + name + " was
					// not imported");
				}

			}

			StaticAccessNode sn = new StaticAccessNode(new TypeNode(original.getFirst()));

			qn = original.getNext();

			if (qn != null) {
				FieldOrPropertyAccessNode fieldAccess = new FieldOrPropertyAccessNode(qn.getFirst().getName());
				fieldAccess.setPrimary(sn);
				fieldAccess.setScanPosition(child.getScanPosition());

				qn = qn.getNext();
				while (qn != null) {
					FieldOrPropertyAccessNode f = new FieldOrPropertyAccessNode(qn.getFirst().getName());
					f.setPrimary(fieldAccess);
					fieldAccess = f;

					qn = qn.getNext();
				}

				TypeVariable type = fieldAccess.getTypeVariable();
				VariableInfo varSelf = semanticContext.currentScope().searchVariable("this");

				if (!type.equals(varSelf.getTypeVariable())) {
					match = matchImports(ct, type);

					if (match.isPresent()) {
						match.get().setMemberCalled(true);
					}
				}
			}

			return VisitorNext.Children;
		} else {
			// references a variable

			// This is maybe a field.
			VariableReadNode vn = new VariableReadNode(varInfo.getName(), varInfo);
			qn = original.getNext();

			if (qn == null) {
				TypeVariable type = vn.getTypeVariable();
				VariableInfo varSelf = semanticContext.currentScope().searchVariable("this");

				if (!type.equals(varSelf.getTypeVariable())) {
					match = matchImports(ct, type);

					if (match.isPresent()) {
						match.get().setMemberCalled(true);
					}
				}

			} else {
				FieldOrPropertyAccessNode fieldAccess = new FieldOrPropertyAccessNode(qn.getFirst().getName());
				fieldAccess.setPrimary(vn);

				qn = qn.getNext();
				while (qn != null) {
					FieldOrPropertyAccessNode f = new FieldOrPropertyAccessNode(qn.getFirst().getName());
					f.setPrimary(fieldAccess);
					fieldAccess = f;

					qn = qn.getNext();
				}

				TypeVariable type = fieldAccess.getTypeVariable();
				VariableInfo varSelf = semanticContext.currentScope().searchVariable("this");

				if (!type.equals(varSelf.getTypeVariable())) {
					match = matchImports(ct, type);

					if (match.isPresent()) {
						match.get().setMemberCalled(true);
					}
				}
			}

			return VisitorNext.Children;
		}

	}

	private Optional<Import> matchImports(ClassTypeNode ct, String nameAlias) {
		for (Import i : ct.imports()) {
			if (i.getMatchAlias().equals(nameAlias)) {
				i.setUsed(true);
				return Optional.of(i);
			}
		}
		int pos = nameAlias.lastIndexOf('.');
		if (pos > 0){
			nameAlias = nameAlias.substring(pos + 1);
			return matchImports(ct, nameAlias);
		}
		return Optional.empty();
	}

	private Optional<Import> matchImports(ClassTypeNode ct, TypeVariable type) {
		for (Import i : ct.imports()) {
			if (i.getTypeName().toString().equals(type.getName())) {
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

		if (node instanceof MethodDeclarationNode) {
			MethodDeclarationNode m = (MethodDeclarationNode)node;
			semanticContext.endScope();
			
			if (!genericNames.contains(m.getReturnType().getName()) && !semanticContext.currentScope().getCurrentType().getName().equals(m.getReturnType().getName())){
				Optional<Import> match = matchImports(ct, m.getReturnType().getName());

				if (match.isPresent()) {
					match.get().setMemberCalled(true);

					m.getReturnType().setName(match.get().getTypeName());
					return;
				}

				throw new CompilationError(node,
						"Type " + m.getReturnType().getName() + " was not imported in " + ct.getName());
			}

			
		} else if (node instanceof ClassTypeNode) {
			semanticContext.endScope();
		} else if (node instanceof BlockNode) {
			semanticContext.endScope();
		} else if (node instanceof ConstructorDeclarationNode) {

		} else if (node instanceof ScopedVariableDefinitionNode) {
			ScopedVariableDefinitionNode variableDeclaration = (ScopedVariableDefinitionNode) node;

			TypeNode typeNode = variableDeclaration.getTypeNode();
			Optional<TypeDefinition> type = semanticContext.resolveTypeForName(typeNode.getName(),
					typeNode.getTypeParametersCount());

			if (!type.isPresent()) {
				LenseTypeDefinition defType = new LenseTypeDefinition(typeNode.getName(), null, null);
				type = Optional.of(semanticContext.registerType(defType, typeNode.getTypeParametersCount()));
			}
			VariableInfo info = semanticContext.currentScope().defineVariable(variableDeclaration.getName(),
					new FixedTypeVariable(type.get()), node);

			variableDeclaration.setInfo(info);

		} else if (node instanceof FormalParameterNode) {
			FormalParameterNode variableDeclaration = (FormalParameterNode) node;

			TypeNode typeNode = variableDeclaration.getTypeNode();
			if (typeNode != null) {
				LenseTypeDefinition type = new LenseTypeDefinition(typeNode.getName(), null, null);
				semanticContext.currentScope().defineVariable(variableDeclaration.getName(),
						new FixedTypeVariable(type), node);
			}

		} else if (node instanceof LiteralTupleInstanceCreation) {
			LiteralTupleInstanceCreation tuple = ((LiteralTupleInstanceCreation) node);
			TypedNode value = (TypedNode) tuple.getChildren().get(1).getChildren().get(0);

			TypeNode typeNode = new TypeNode(new QualifiedNameNode("lense.core.collections.Tuple"));
			typeNode.addParametricType(new GenericTypeParameterNode(new TypeNode(value.getTypeVariable())));

			TypedNode nextTuple;
			if (tuple.getChildren().get(1).getChildren().size() == 2) {
				nextTuple = (TypedNode) tuple.getChildren().get(1).getChildren().get(1);
				typeNode.addParametricType(
						new GenericTypeParameterNode(new TypeNode(new QualifiedNameNode("lense.core.lang.Any"))));

			} else {
				nextTuple = new TypeNode(LenseTypeSystem.Nothing());
				typeNode.addParametricType(new GenericTypeParameterNode(new TypeNode(nextTuple.getTypeVariable())));
			}

			tuple.replace(tuple.getTypeNode(), typeNode);
		}

	}

}
