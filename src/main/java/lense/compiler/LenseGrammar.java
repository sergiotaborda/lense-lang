/**
 * 
 */
package lense.compiler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import compiler.SymbolBasedToken;
import compiler.TokenSymbol;
import compiler.lexer.ScanPosition;
import compiler.lexer.Scanner;
import compiler.lexer.Token;
import compiler.parser.IdentifierNode;
import compiler.parser.SemanticAction;
import compiler.parser.Symbol;
import compiler.syntax.AstNode;
import lense.compiler.ast.AccessorNode;
import lense.compiler.ast.AnnotationListNode;
import lense.compiler.ast.AnnotationNode;
import lense.compiler.ast.ArgumentListItemNode;
import lense.compiler.ast.ArgumentListNode;
import lense.compiler.ast.ArithmeticNode;
import lense.compiler.ast.ArithmeticOperation;
import lense.compiler.ast.AssertNode;
import lense.compiler.ast.AssignmentNode;
import lense.compiler.ast.BlockNode;
import lense.compiler.ast.BooleanOperation;
import lense.compiler.ast.BooleanOperatorNode;
import lense.compiler.ast.BooleanValue;
import lense.compiler.ast.BreakNode;
import lense.compiler.ast.CatchOptionNode;
import lense.compiler.ast.CatchOptionsNode;
import lense.compiler.ast.ChildTypeNode;
import lense.compiler.ast.ChildTypesListNode;
import lense.compiler.ast.ClassBodyNode;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ComparisonNode;
import lense.compiler.ast.ComparisonNode.Operation;
import lense.compiler.ast.ConstructorDeclarationNode;
import lense.compiler.ast.ConstructorExtentionNode;
import lense.compiler.ast.ContinueNode;
import lense.compiler.ast.CreationTypeNode;
import lense.compiler.ast.DecisionNode;
import lense.compiler.ast.ExpressionNode;
import lense.compiler.ast.FieldDeclarationNode;
import lense.compiler.ast.FieldOrPropertyAccessNode;
import lense.compiler.ast.ForEachNode;
import lense.compiler.ast.FormalParameterNode;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.GivenGenericConstraint;
import lense.compiler.ast.GivenGenericConstraintList;
import lense.compiler.ast.ImplementationModifierNode;
import lense.compiler.ast.ImplementedInterfacesNode;
import lense.compiler.ast.ImportDeclarationsListNode;
import lense.compiler.ast.ImportTypesListNode;
import lense.compiler.ast.ImutabilityNode;
import lense.compiler.ast.IndexedPropertyReadNode;
import lense.compiler.ast.IndexerPropertyDeclarationNode;
import lense.compiler.ast.InferableTypeNode;
import lense.compiler.ast.InstanceOfNode;
import lense.compiler.ast.InvocableDeclarionNode;
import lense.compiler.ast.LambdaExpressionNode;
import lense.compiler.ast.LiteralAssociationInstanceCreation;
import lense.compiler.ast.LiteralIntervalNode;
import lense.compiler.ast.LiteralSequenceInstanceCreation;
import lense.compiler.ast.LiteralTupleInstanceCreation;
import lense.compiler.ast.MethodCallNode;
import lense.compiler.ast.MethodDeclarationNode;
import lense.compiler.ast.MethodInvocationNode;
import lense.compiler.ast.ModifierNode;
import lense.compiler.ast.ModuleExportNode;
import lense.compiler.ast.ModuleImportNode;
import lense.compiler.ast.ModuleMembersNode;
import lense.compiler.ast.ModuleNode;
import lense.compiler.ast.NewInstanceCreationNode;
import lense.compiler.ast.NoneValue;
import lense.compiler.ast.NumericValue;
import lense.compiler.ast.ParametersListNode;
import lense.compiler.ast.PosExpression;
import lense.compiler.ast.PreBooleanUnaryExpression;
import lense.compiler.ast.PreExpression;
import lense.compiler.ast.PropertyDeclarationNode;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.RangeNode;
import lense.compiler.ast.ReturnNode;
import lense.compiler.ast.SingleImportNode;
import lense.compiler.ast.StringValue;
import lense.compiler.ast.SwitchNode;
import lense.compiler.ast.SwitchOption;
import lense.compiler.ast.SwitchOptions;
import lense.compiler.ast.TernaryConditionalExpressionNode;
import lense.compiler.ast.ThowNode;
import lense.compiler.ast.TryStatement;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.TypeOfInvocation;
import lense.compiler.ast.TypeParametersListNode;
import lense.compiler.ast.TypesListNode;
import lense.compiler.ast.UnitTypes;
import lense.compiler.ast.UnitaryOperation;
import lense.compiler.ast.VariableDeclarationNode;
import lense.compiler.ast.VariableReadNode;
import lense.compiler.ast.VariableWriteNode;
import lense.compiler.ast.VarianceNode;
import lense.compiler.ast.VersionNode;
import lense.compiler.ast.VisibilityNode;
import lense.compiler.ast.WhileNode;
import lense.compiler.type.Constructor;
import lense.compiler.type.LenseUnitKind;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.Imutability;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.Variance;
import lense.compiler.typesystem.Visibility;

/**
 * 
 */
public class LenseGrammar extends AbstractLenseGrammar {

	public LenseGrammar() {
		super();
	}

	public Scanner scanner() {
		return new LenseScanner(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isIgnore(char c) {
		return c == '\t' || c == '\r' || c == ' ' || c == '\n';
	}

	protected boolean isStartInlineComent(String text) {
		return "//".equals(text);
	}

	protected boolean isStartMultilineComment(String text) {
		return "/{".equals(text);
	}

	protected boolean isEndMultilineComment(String text) {
		return "}/".equals(text);
	}

	protected void addStopCharacters(Set<Character> stopCharacters) {
		stopCharacters.add('"');
		stopCharacters.add('(');
		stopCharacters.add(')');
		stopCharacters.add('{');
		stopCharacters.add('}');
		stopCharacters.add('[');
		stopCharacters.add(']');
		stopCharacters.add(';');
		stopCharacters.add(',');
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStringLiteralDelimiter(char c) {
		return c == '"';
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Token> terminalMatch(ScanPosition pos, String text) {
		if (text.trim().length() == 0) {
			return Optional.empty();
		}

		if (text.length() > 1 && text.startsWith("\"") && text.endsWith("\"")) {
			return Optional
					.of(new SymbolBasedToken(pos, text.substring(1, text.length() - 1), TokenSymbol.LiteralString));
		} else if (matchNatural(text)) {
			return Optional.of(new SymbolBasedToken(pos, text, TokenSymbol.LiteralWholeNumber));
		} else if (matchDecimal(text)) {
			return Optional.of(new SymbolBasedToken(pos, text, TokenSymbol.LiteralDecimalNumber));
		} else if (isVersionLiteral(text)) {
			return Optional.of(new VersionLiteralToken(pos, text));
		} 

		return super.terminalMatch(pos, text);

	}

	/**
	 * @param text
	 * @return
	 */
	public boolean isVersionLiteral(String text) {
		return text.length() > 1 && text.contains(".");
	}

	private boolean matchNatural(String text) {
		return text.matches("^\\d+[NSZLdfkmi]?$") || text.matches("^#([A-Fa-f0-9_]+)$")
				|| text.matches("^\\$([01_]+)$");
	}

	private boolean matchDecimal(String text) {
		return text.matches("[0-9]+\\.?[0-9]+([eE][-+]?[0-9]+)?[NSZLdfkmi]?");
	}

	public boolean isDigit(char c) {
		return isNumberStarter(c) || c == '_' || c == '.' 
				|| c == 'e' || c == 'E' // scientific notation
				|| c == 'i' // imaginary unit
				;
	}

	public boolean isNumberStarter(char c) {
		return super.isNumberStarter(c) || c == '0' || c == '#' || c == '$';
	}

	/**
	 * @param text
	 * @return
	 */
	private TypeDefinition determineNumberType(String literalNumber) {
		if (literalNumber.startsWith("#")) {
			return LenseTypeSystem.Natural();
		} else if (literalNumber.startsWith("$")) {
			return LenseTypeSystem.Binary();
		}

		char end = literalNumber.charAt(literalNumber.length() - 1);
		if (literalNumber.contains(".") && matchDecimal(literalNumber)) {
			// decimal
			if (Character.isDigit(end) || end == 'm') {
				return LenseTypeSystem.Rational();
			} else if (end == 'N') {
				throw new CompilationError("A decimal number cannot end with N");
			} else if (end == 'S') {
				throw new CompilationError("A decimal number cannot end with S");
			} else if (end == 'Z') {
				throw new CompilationError("A decimal number cannot end with Z");
			} else if (end == 'L') {
				throw new CompilationError("A decimal number cannot end with L");
			} else if (end == 'd') {
				return LenseTypeSystem.Float64();
			} else if (end == 'f') {
				return LenseTypeSystem.Float32();
			} else if (end == 'm') {
				return LenseTypeSystem.Float();
			} else if (end == 'i') {
				return LenseTypeSystem.Imaginary();
			}
		} else if (matchNatural(literalNumber)) {
			// whole
			if (Character.isDigit(end) || end == 'N') {
				return LenseTypeSystem.Natural();
			} else if (end == 'S') {
				return LenseTypeSystem.Short();
			} else if (end == 'Z') {
				return LenseTypeSystem.Int32();
			} else if (end == 'L') {
				return LenseTypeSystem.Int64();
			} else if (end == 'd') {
				return LenseTypeSystem.Float64();
			} else if (end == 'f') {
				return LenseTypeSystem.Float32();
			} else if (end == 'm') {
				return LenseTypeSystem.Float();
			} else if (end == 'i') {
				return LenseTypeSystem.Imaginary();
			}
		}

		throw new CompilationError("'" + literalNumber + "' is not reconized as a Number");

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Token> stringLiteralMath(ScanPosition pos, String text) {
		return Optional.of(new SymbolBasedToken(pos, text, TokenSymbol.LiteralString));
	}

	protected void posInit() {

		//this.keywords.add("/**");
		//this.keywords.add("//");
		//this.keywords.add("*/");

		installSemanticActions();
	}

	protected void installSemanticActions() {

		getNonTerminal("qualifiedName").addSemanticAction((p, r) -> {

			QualifiedNameNode name = new QualifiedNameNode();

			for (int i = 0; i < r.size(); i += 2) {
				if (r.get(i).getAstNode().isPresent()) {
					AstNode node = r.get(i).getAstNode().get();
					if (node instanceof QualifiedNameNode) {
						name = (QualifiedNameNode) node;
					} else if (node instanceof IdentifierNode) {
						name.append(((IdentifierNode) node).getName());
					}

				} else {
					name.append((String) r.get(i).getSemanticAttribute("lexicalValue").get());
				}
			}

			p.setSemanticAttribute("node", name);
		});

		getNonTerminal("unit").addSemanticAction((p, r) -> {

			UnitTypes types;
			if (r.size() == 1) {
				AstNode n = r.get(0).getAstNode().get();
				if (n instanceof UnitTypes) {
					p.setAstNode(n);
				} else {
					types = new UnitTypes();
					types.add(n);
					p.setAstNode(types);
				}

			} else if (r.isEmpty()) {
				types = new UnitTypes();
				p.setSemanticAttribute("node", types);
			} else {

				types = new UnitTypes();
				String packageName= null;
				ImportDeclarationsListNode imports = new ImportDeclarationsListNode();
				
				for (int index = 0 ; index < r.size(); index++) {
					Optional<AstNode> node = r.get(index).getAstNode();
					if (node.isPresent() && node.get() instanceof ImportDeclarationsListNode) {
						
						imports = (ImportDeclarationsListNode) r.get(index).getSemanticAttribute("node").get();
					} else if (node.isPresent() && node.get() instanceof QualifiedNameNode){
						packageName = ((QualifiedNameNode)node.get()).getName();
					} else if (node.isPresent() && node.get() instanceof ClassTypeNode){
						types.add((ClassTypeNode) node.get());
					} else if (node.isPresent() && node.get() instanceof UnitTypes){
						for(var f : ((UnitTypes)node.get()).getChildren()) {
							types.add(f);
						}
						
					}

				}
				
				if(packageName != null) {
					for(var t : types.getChildren(ClassTypeNode.class)) {
						t.setPackageName(packageName);
					}
				}
				
			
				
				types.add(imports);

				p.setSemanticAttribute("node", types);

			}

		});

		getNonTerminal("moduleDeclaration").addSemanticAction((p, r) -> {
			ModuleNode module = new ModuleNode();
			module.setName(ensureQualifiedName(r.get(1).getAstNode().get()));
			module.setVersion(new VersionNode(r.get(3).getLexicalValue(), false));

			Optional<AstNode> all = r.get(5).getAstNode();
			ModuleMembersNode imports = new ModuleMembersNode();
			ModuleMembersNode exports = new ModuleMembersNode();

			module.setImports(imports);
			module.setExports(exports);

			if (all.isPresent()) {
				for (AstNode n : all.get().getChildren()) {
					if (n instanceof ModuleImportNode) {
						imports.add(n);
					} else {
						exports.add(n);
					}
				}
			}

			p.setAstNode(module);
		});

		getNonTerminal("versionMatchLiteral").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				p.setAstNode(new VersionNode(r.get(0).getLexicalValue(), true));
			} else {
				p.setAstNode(new VersionNode(r.get(0).getLexicalValue(), false));
			}
		});

		getNonTerminal("moduleBody").addSemanticAction((p, r) -> {
			if (r.size()> 2) {
				p.setAstNode(r.get(1).getAstNode().get());
			}

		});


		getNonTerminal("moduleMemberDeclarations").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				if (r.get(0).getAstNode().get() instanceof ModuleMembersNode) {
					p.setAstNode(r.get(0).getAstNode().get());
				} else {
					ModuleMembersNode in = new ModuleMembersNode();
					in.add(r.get(0).getAstNode().get());
					p.setAstNode(in);
				}

			} else {
				AstNode node = r.get(0).getAstNode().get();
				ModuleMembersNode in;
				if (node instanceof ModuleMembersNode) {
					in = (ModuleMembersNode) node;
					in.add(r.get(1).getAstNode().get());
				} else {
					in = new ModuleMembersNode();
					in.add(r.get(0).getAstNode().get());
					in.add(r.get(1).getAstNode().get());
				}

				p.setAstNode(in);
			}

		});

		getNonTerminal("moduleMemberDeclaration").addSemanticAction((p, r) -> {
			p.setAstNode(r.get(0).getAstNode().get());

		});

		getNonTerminal("moduleImport").addSemanticAction((p, r) -> {
			if (r.size() == 4) {
				p.setAstNode(new ModuleImportNode(r.get(1).getAstNode(QualifiedNameNode.class).get(),
						r.get(2).getAstNode(VersionNode.class).get()));
			}

		});

		getNonTerminal("moduleExport").addSemanticAction((p, r) -> {
			if (r.size() == 5) {
				p.setAstNode(new ModuleExportNode(r.get(1).getAstNode(QualifiedNameNode.class).get(), true));
			} else if (r.size() == 3) {
				p.setAstNode(new ModuleExportNode(r.get(1).getAstNode(QualifiedNameNode.class).get(), false));
			}

		});

		getNonTerminal("importDeclarations").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				if (r.get(0).getAstNode().get() instanceof ImportDeclarationsListNode) {
					p.setAstNode(r.get(0).getAstNode().get());
				} else {
					ImportDeclarationsListNode in = new ImportDeclarationsListNode();
					in.add(r.get(0).getAstNode().get());
					p.setAstNode(in);
				}

			} else {
				AstNode node = r.get(0).getAstNode().get();
				ImportDeclarationsListNode in;
				if (node instanceof ImportDeclarationsListNode) {
					in = (ImportDeclarationsListNode) node;
					AstNode other = r.get(1).getAstNode().get();
					if (other instanceof ImportDeclarationsListNode) {
						for (AstNode ast : other.getChildren()) {
							in.add(ast);
						}
					} else {
						in.add(other);
					}

				} else {
					in = new ImportDeclarationsListNode();
					in.add(r.get(0).getAstNode().get());
					in.add(r.get(1).getAstNode().get());
				}

				p.setAstNode(in);
			}
		});

		getNonTerminal("importDeclaration").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				if (r.get(0).getAstNode(ImportDeclarationsListNode.class).isPresent()) {
					p.setAstNode(r.get(0).getAstNode().get());
				} else {
					ImportDeclarationsListNode list = new ImportDeclarationsListNode();
					list.add(r.get(0).getAstNode().get());
					p.setAstNode(list);
				}
			} else if (r.size() == 5) {

				if ("as".equals(r.get(2).getLexicalValue())) {
					ImportDeclarationsListNode list = new ImportDeclarationsListNode();

					SingleImportNode q = new SingleImportNode(r.get(1).getAstNode(QualifiedNameNode.class).get(),
							r.get(3).getLexicalValue());

					q.setScanPosition(r.get(0).getParserTreeNode().getScanPosition());
					list.add(q);

					p.setAstNode(list);
				} else {
					QualifiedNameNode qn = ensureQualifiedName(r.get(1).getAstNode().get());

					ImportTypesListNode names = r.get(3).getAstNode(ImportTypesListNode.class).get();
					ImportDeclarationsListNode list = new ImportDeclarationsListNode();

					for (AstNode ast : names.getChildren()) {

						QualifiedNameNode qnt = ensureQualifiedName(ast);

						SingleImportNode q = new SingleImportNode(qn.concat(qnt));
						q.setScanPosition(r.get(0).getParserTreeNode().getScanPosition());
						list.add(q);
					}

					p.setAstNode(list);
				}

			} else {
				ImportDeclarationsListNode list = new ImportDeclarationsListNode();

				SingleImportNode q = new SingleImportNode(r.get(1).getAstNode(QualifiedNameNode.class).get());

				q.setScanPosition(r.get(1).getParserTreeNode().getScanPosition());

				list.add(q);

				p.setAstNode(list);
			}

		});
		getNonTerminal("importTypes").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				if (r.get(0).getAstNode(ImportTypesListNode.class).isPresent()) {
					p.setAstNode(r.get(0).getAstNode().get());
				} else {
					ImportTypesListNode list = new ImportTypesListNode();
					list.add(r.get(0).getAstNode().get());
					p.setAstNode(list);
				}
			} else {
				ImportTypesListNode list = r.get(0).getAstNode(ImportTypesListNode.class).get();
				list.add(r.get(2).getAstNode().get());
				p.setAstNode(list);
			}

		});

		getNonTerminal("importName").addSemanticAction((p, r) -> {
			p.setAstNode(new IdentifierNode(r.get(0).getLexicalValue()));
		});

		getNonTerminal("packageDeclaration").addSemanticAction((p, r) -> {
			p.setAstNode(r.get(1).getAstNode().get());
		});

		getNonTerminal("importDeclarations");
		getNonTerminal("importDeclaration");
		getNonTerminal("singleTypeImportDeclaration");
		getNonTerminal("typeImportOnDemandDeclaration");

		getNonTerminal("typeDeclarations").addSemanticAction((p, r) -> {

			Optional<UnitTypes> units = r.get(0).getAstNode(UnitTypes.class);

			UnitTypes types;
			if (units.isPresent()) {
				types = units.get();

				r.stream().skip(1).forEach(s -> {
					if (s.getSemanticAttribute("node").isPresent()) {
						types.add((AstNode) s.getSemanticAttribute("node").get());
					}
				});

			} else {
				types = new UnitTypes();

				for (Symbol s : r) {
					if (s.getSemanticAttribute("node").isPresent()) {
						types.add((AstNode) s.getSemanticAttribute("node").get());
					}
				}
			}


			p.setSemanticAttribute("node", types);

		});

		getNonTerminal("typeDeclaration").addSemanticAction((p, r) -> {
			p.setSemanticAttribute("node", r.get(0).getAstNode().get());
		});


		getNonTerminal("sealedDeclaration").addSemanticAction((p, r) -> {
			p.setAstNode(r.get(1).getAstNode().get());
		});

		getNonTerminal("sealedChildTypes").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				AstNode node = r.get(0).getAstNode().get();
				if (node instanceof ChildTypesListNode) {
					p.setAstNode(node);
				} else {
					ChildTypesListNode list = new ChildTypesListNode();
					list.add(node);
					p.setAstNode(list);
				}
			} else {
				ChildTypesListNode node;

				if (r.get(0).getAstNode().get() instanceof ChildTypesListNode) {
					node = r.get(0).getAstNode(ChildTypesListNode.class).get();
					node.add(r.get(2).getAstNode().get());
				} else {
					node = new ChildTypesListNode();
					node.add(r.get(0).getAstNode().get());
					node.add(r.get(2).getAstNode().get());
				}

				p.setAstNode(node);
			}
		});

		getNonTerminal("sealedChildType").addSemanticAction((p, r) -> {
			//if (r.get(0).getAstNode().isPresent()) {
			AstNode type = r.get(0).getAstNode().get();
			if (type instanceof BooleanValue) {

				if (((BooleanValue)type).isValue()) {
					p.setAstNode(new ChildTypeNode(new TypeNode("lense.core.lang.true"))); 
				} else {
					p.setAstNode(new ChildTypeNode(new TypeNode("lense.core.lang.false"))); 
				}
			} else if (type instanceof NoneValue){
				p.setAstNode(new ChildTypeNode(new TypeNode("lense.core.lang.none"))); 
			} else if (type instanceof TypeNode){
				p.setAstNode(new ChildTypeNode((TypeNode) type));
			} else {
				p.setAstNode(new ChildTypeNode((TypeNode) type));
			}

			//        	} else {
			//        		  p.setAstNode(new ChildTypeNode(r.get(0).getLexicalValue()));
			//        	}

		});

		getNonTerminal("annotations").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				AstNode node = r.get(0).getAstNode().get();
				if (node instanceof AnnotationListNode) {
					p.setAstNode(node);
				} else {
					AnnotationListNode list = new AnnotationListNode();
					list.add(node);
					p.setAstNode(list);
				}
			} else {
				AnnotationListNode node;

				if (r.get(0).getAstNode().get() instanceof AnnotationListNode) {
					node = r.get(0).getAstNode(AnnotationListNode.class).get();
					node.add(r.get(1).getAstNode().get());
				} else {
					node = new AnnotationListNode();
					node.add(r.get(0).getAstNode().get());
					node.add(r.get(1).getAstNode().get());
				}

				p.setAstNode(node);
			}

		});

		getNonTerminal("annotation").addSemanticAction((p, r) -> {

			AnnotationNode node = new AnnotationNode();

			Symbol symbol = r.get(1);
			String op;
			if (symbol.getSemanticAttribute("lexicalValue").isPresent()) {
				op = (String) symbol.getSemanticAttribute("lexicalValue").get();
			} else {
				op = (String) ((Symbol) symbol.getParserTreeNode().getChildren().get(0))
						.getSemanticAttribute("lexicalValue").get();
			}

			node.setName(op);
			p.setAstNode(node);

		});


		//    	enhancementDeclaration = annotations?, visibilityModifier?, 'enhancement' , qualifiedName , 'extends' , type, genericTypesDeclaration, enhancementBody;
		//		enhancementBody = '{',enhancementBodyDeclarations ,'}';
		//		enhancementBodyDeclarations = enhancementBodyDeclaration | enhancementBodyDeclarations, enhancementBodyDeclaration;
		//			enhancementBodyDeclaration = enhancementMemberDeclaration;
		//			enhancementMemberDeclaration = methodDeclaration;
		//			
		getNonTerminal("enhancementDeclaration").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {

				ClassTypeNode n = new ClassTypeNode(LenseUnitKind.Enhancement);

				Modifiers modifiers = new Modifiers();

				int nextNodeIndex = readModifiers(r, modifiers, QualifiedNameNode.class);

				QualifiedNameNode qname = r.get(nextNodeIndex).getAstNode(QualifiedNameNode.class).get();

				n.setScanPosition(r.get(nextNodeIndex).getParserTreeNode().getScanPosition());
				n.setSimpleName(qname.getName());

				if (modifiers.getAnnotations() != null) {
					n.setAnnotations(modifiers.getAnnotations());
				}

				n.setNative(modifiers.getImplementationModifier().isNative());
				n.setVisibility(modifiers.getVisibility().getVisibility(Visibility.Private));

				// Generics
				Optional<TypeParametersListNode> generics = r.get(nextNodeIndex + 1).getAstNode(TypeParametersListNode.class);

				if (generics.isPresent()) {
					n.setGenerics(generics.get());
				}
				
				// extends
				nextNodeIndex += 3;
				Optional<TypeNode> ext = r.get(nextNodeIndex).getAstNode(TypeNode.class);

				if (ext.isPresent()) {
					n.setSuperType(ensureTypeNode(ext.get()));
					nextNodeIndex += 1;
				} else {
					Optional<QualifiedNameNode> sup = r.get(nextNodeIndex).getAstNode(QualifiedNameNode.class);

					if (ext.isPresent()) {
						n.setSuperType(ensureTypeNode(sup.get()));
						nextNodeIndex += 3;
					}
				}
				
				nextNodeIndex++;
				
				if (nextNodeIndex < r.size()) {
					Optional<GivenGenericConstraintList> givens = r.get(nextNodeIndex)
							.getAstNode(GivenGenericConstraintList.class);

					if (givens.isPresent()) {
						n.setGivens(givens.get());
						nextNodeIndex++;
					}
				}
				
				AstNode b = r.get(r.size() - 1).getAstNode().get();

				if (b instanceof ClassBodyNode) {
					n.setBody((ClassBodyNode) b);
				} else {
					ClassBodyNode c = new ClassBodyNode();
					c.add(b);
					n.setBody(c);
				}

				p.setAstNode(n);
			}

		});

		
		
		getNonTerminal("givenConstraintItem").addSemanticAction((p, r) -> {
			if (r.size() == 4) {
				var identifier = r.get(1).getLexicalValue();
				
				p.setAstNode(new GivenGenericConstraint(identifier, r.get(3).getAstNode(TypeNode.class).get()));
			} 
			
		});
		
		getNonTerminal("givenConstraints").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				var list = new GivenGenericConstraintList();
				list.add( r.get(0).getAstNode().get());
				p.setAstNode(list);
			} else {
				var list = r.get(0).getAstNode(GivenGenericConstraintList.class).get();
				list.add( r.get(2).getAstNode().get());
				p.setAstNode(list);
			}
			
		});
		
		
		getNonTerminal("classModifiers").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				var isValue = r.get(0).getLexicalValue().equals("value");
				var isType = r.get(0).getLexicalValue().equals("type");
				var node = new ImplementationModifierNode();
				node.setValueClass(isValue);
				node.setTypeClass(isType);
				p.setAstNode(node);
			}
		});
		
		getNonTerminal("classDeclaration").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {

			
				Modifiers modifiers = new Modifiers();

				int nextNodeIndex = readModifiers(r, modifiers, QualifiedNameNode.class);

				QualifiedNameNode qname = r.get(nextNodeIndex).getAstNode(QualifiedNameNode.class).get();

			    ClassTypeNode n = new ClassTypeNode(modifiers.getImplementationModifier().isValueClass() 
			    		? LenseUnitKind.ValueClass 
			    		: modifiers.getImplementationModifier().isTypeClass() 
			    					? LenseUnitKind.TypeClass
			    					: LenseUnitKind.Class
			    );

				n.setScanPosition(r.get(nextNodeIndex).getParserTreeNode().getScanPosition());
				n.setSimpleName(qname.getName());

				if (modifiers.getAnnotations() != null) {
					n.setAnnotations(modifiers.getAnnotations());
				}

				n.setAbstract(modifiers.getImplementationModifier().isAbstract());
				n.setNative(modifiers.getImplementationModifier().isNative());
				n.setVisibility(modifiers.getVisibility().getVisibility(Visibility.Private));
				n.setImmutable(modifiers.getImplementationModifier().isImmutable());


				Optional<TypeParametersListNode> generics = r.get(++nextNodeIndex).getAstNode(TypeParametersListNode.class);

				if (generics.isPresent()) {
					n.setGenerics(generics.get());

				}

				nextNodeIndex++;


				Optional<ChildTypesListNode> algebricChildren = r.get(nextNodeIndex).getAstNode(ChildTypesListNode.class);

				if (algebricChildren.isPresent()) {
					nextNodeIndex++;
					n.setAlgebric(true);
					n.setAlgebricChildren(algebricChildren.get());

				}
				// extends
				Optional<TypeNode> ext = r.get(nextNodeIndex).getAstNode(TypeNode.class);

				if (ext.isPresent()) {
					n.setSuperType(ensureTypeNode(ext.get()));
					nextNodeIndex += 3;
				} else {

					Optional<QualifiedNameNode> sup = r.get(nextNodeIndex).getAstNode(QualifiedNameNode.class);

					if (ext.isPresent()) {
						n.setSuperType(ensureTypeNode(sup.get()));
						nextNodeIndex += 3;
					}
				}

				nextNodeIndex = readModifiers(r, modifiers, ImplementedInterfacesNode.class);

				if (nextNodeIndex >0){

					// implement
					Optional<ImplementedInterfacesNode> implement = r.get(nextNodeIndex)
							.getAstNode(ImplementedInterfacesNode.class);

					if (implement.isPresent()) {
						n.setInterfaces(implement.get());
						nextNodeIndex++;
					}
				} else {
					nextNodeIndex = readModifiers(r, modifiers, GivenGenericConstraintList.class);
				}

				if (nextNodeIndex >0 && nextNodeIndex < r.size()) {
					Optional<GivenGenericConstraintList> givens = r.get(nextNodeIndex)
							.getAstNode(GivenGenericConstraintList.class);

					if (givens.isPresent()) {
						n.setGivens(givens.get());
						nextNodeIndex++;
					}
				}

				var b = r.get(r.size() - 1).getAstNode().get();

				if (b instanceof ClassBodyNode) {
					n.setBody((ClassBodyNode) b);
				} else {
					ClassBodyNode c = new ClassBodyNode();
					c.add(b);
					n.setBody(c);
				}

				p.setAstNode(n);
			}

		});

		getNonTerminal("interfaceDeclaration").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {

				ClassTypeNode n = new ClassTypeNode(LenseUnitKind.Interface);

				Modifiers modifiers = new Modifiers();

				int nextNodeIndex = readModifiers(r, modifiers, QualifiedNameNode.class);

				QualifiedNameNode qname = r.get(nextNodeIndex).getAstNode(QualifiedNameNode.class).get();

				n.setScanPosition(r.get(nextNodeIndex).getParserTreeNode().getScanPosition());
				n.setSimpleName(qname.getName());

				if (modifiers.getAnnotations() != null) {
					n.setAnnotations(modifiers.getAnnotations());
				}

				n.setAbstract(true);
				n.setNative(false);
				n.setVisibility(modifiers.getVisibility().getVisibility(Visibility.Private));
				

				n.setSuperType(new TypeNode(LenseTypeSystem.Any()));

				Optional<TypeParametersListNode> generics = r.get(++nextNodeIndex).getAstNode(TypeParametersListNode.class);

				if (generics.isPresent()) {
					n.setGenerics(generics.get());
					nextNodeIndex++;
				}


				if (nextNodeIndex >0 && nextNodeIndex < r.size()) {
					// implements

					Optional<ImplementedInterfacesNode> implement = scan(r, nextNodeIndex, ImplementedInterfacesNode.class);

					if (implement.isPresent()) {
						n.setInterfaces(implement.get());
						nextNodeIndex++;
					}
				} else {
					nextNodeIndex = readModifiers(r, modifiers, GivenGenericConstraintList.class);
				}

				if (nextNodeIndex >0 && nextNodeIndex < r.size()) {
					Optional<GivenGenericConstraintList> givens = r.get(nextNodeIndex)
							.getAstNode(GivenGenericConstraintList.class);

					if (givens.isPresent()) {
						n.setGivens(givens.get());
						nextNodeIndex++;
					}
				}
				
				AstNode b = r.get(r.size() - 1).getAstNode().get();

				if (b instanceof ClassBodyNode) {
					n.setBody((ClassBodyNode) b);
				} else {
					ClassBodyNode c = new ClassBodyNode();
					c.add(b);
					n.setBody(c);
				}

				p.setAstNode(n);
			}

		});

		getNonTerminal("objectDeclaration").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {

				ClassTypeNode n = new ClassTypeNode(LenseUnitKind.Object);

				Modifiers modifiers = new Modifiers();

				int nextNodeIndex = readModifiers(r, modifiers, QualifiedNameNode.class);

				QualifiedNameNode qname = r.get(nextNodeIndex).getAstNode(QualifiedNameNode.class).get();

				n.setScanPosition(r.get(nextNodeIndex).getParserTreeNode().getScanPosition());
				n.setSimpleName(qname.getName());

				if (modifiers.getAnnotations() != null) {
					n.setAnnotations(modifiers.getAnnotations());
				}
				
				n.setAbstract(modifiers.getImplementationModifier().isAbstract());
				n.setNative(modifiers.getImplementationModifier().isNative());
				n.setVisibility(modifiers.getVisibility().getVisibility(Visibility.Private));

				Optional<TypeParametersListNode> generics = r.get(++nextNodeIndex).getAstNode(TypeParametersListNode.class);

				if (generics.isPresent()) {
					n.setGenerics(generics.get());
					nextNodeIndex++;
				}

				// extends
				Optional<TypeNode> ext = r.get(nextNodeIndex).getAstNode(TypeNode.class);

				if (ext.isPresent()) {
					n.setSuperType(ensureTypeNode(ext.get()));
					nextNodeIndex += 3;
				} else {

					Optional<QualifiedNameNode> sup = r.get(nextNodeIndex).getAstNode(QualifiedNameNode.class);

					if (ext.isPresent()) {
						n.setSuperType(ensureTypeNode(sup.get()));
						nextNodeIndex += 3;
					}
				}

			
				if (nextNodeIndex >0 && nextNodeIndex< r.size()){

					// implement
					Optional<ImplementedInterfacesNode> implement = r.get(nextNodeIndex)
							.getAstNode(ImplementedInterfacesNode.class);

					if (implement.isPresent()) {
						n.setInterfaces(implement.get());
						nextNodeIndex++;
					}
				} else {
					nextNodeIndex = readModifiers(r, modifiers, GivenGenericConstraintList.class);
				}


				if (nextNodeIndex >0 &&  nextNodeIndex < r.size()) {
					Optional<GivenGenericConstraintList> givens = r.get(nextNodeIndex)
							.getAstNode(GivenGenericConstraintList.class);

					if (givens.isPresent()) {
						n.setGivens(givens.get());
						nextNodeIndex++;
					}
				}

				AstNode b = r.get(r.size() - 1).getAstNode().get();

				if (b instanceof ClassBodyNode) {
					n.setBody((ClassBodyNode) b);
				} else {
					ClassBodyNode c = new ClassBodyNode();
					c.add(b);
					n.setBody(c);
				}



				p.setAstNode(n);
			}

		});

		getNonTerminal("genericTypesDeclaration").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				TypeParametersListNode ptn;
				AstNode node = r.get(1).getAstNode().get();
				if (node instanceof TypeNode) {
					ptn = new TypeParametersListNode();
					ptn.add(node);

				} else {
					ptn = (TypeParametersListNode) node;
				}
				p.setAstNode(ptn);
			}

		});

		getNonTerminal("classBody", "interfaceBody", "enhancementBody").addSemanticAction((p, r) -> {
			if (r.size() == 2) {
				p.setAstNode(new ClassBodyNode());
			} else {
				p.setAstNode(r.get(1).getAstNode().get());
			}

		});
//
//		getNonTerminal("interfaceBody").addSemanticAction((p, r) -> {
//			if (r.size() == 2) {
//				p.setAstNode(new ClassBodyNode());
//			} else {
//				p.setAstNode(r.get(1).getAstNode().get());
//			}
//
//		});

		getNonTerminal("interfaceMemberDeclarations").addSemanticAction((p, r) -> {
			if (r.size() == 1 && r.get(0).getAstNode().get() instanceof ClassBodyNode) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else if (r.size() == 2 && r.get(0).getAstNode().get() instanceof ClassBodyNode) {

				ClassBodyNode body = r.get(0).getAstNode(ClassBodyNode.class).get();

				body.add(r.get(1).getAstNode().get());
				p.setAstNode(body);
			} else {
				ClassBodyNode body = new ClassBodyNode();

				body.add(r.get(0).getAstNode().get());
				if (r.size() > 1) {
					body.add(r.get(1).getAstNode().get());
				}

				p.setAstNode(body);
			}

		});

		getNonTerminal("interfaceMemberDeclaration", "enhancementMemberDeclaration", "enhancementBodyDeclaration").addSemanticAction((p, r) -> {
			p.setAstNode(r.get(0).getAstNode().get());
		});

		getNonTerminal("extendsInterfaces").addSemanticAction((p, r) -> {
			p.setAstNode(r.get(1).getAstNode().get());
		});

		getNonTerminal("extendsInterfaceType").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				ImplementedInterfacesNode list = new ImplementedInterfacesNode();
				list.add(r.get(0).getAstNode().get());
				p.setAstNode(list);
			} else {
				ImplementedInterfacesNode list = (ImplementedInterfacesNode) r.get(0).getAstNode().get();
				list.add(r.get(2).getAstNode().get());
				p.setAstNode(list);
			}

		});

		getNonTerminal("implementsInterfaces").addSemanticAction((p, r) -> {
			p.setAstNode(r.get(1).getAstNode().get());
			// ImplementedInterfacesNode interfaces = new
			// ImplementedInterfacesNode();
			//
			// if (r.size() == 2){
			// interfaces.add(r.get(1).getAstNode().get());
			//
			// p.setAstNode(interfaces);
			// } else {
			// AstNode node = r.get(0).getAstNode().get();
			// if ( node instanceof ImplementedInterfacesNode){
			// interfaces = (ImplementedInterfacesNode)node;
			//
			// interfaces.add(r.get(2).getAstNode().get());
			// }
			// p.setAstNode(interfaces);
			// }

		});

		getNonTerminal("classBodyDeclarations", "enhancementBodyDeclarations").addSemanticAction((p, r) -> {

			ClassBodyNode body = new ClassBodyNode();
			AstNode node = r.get(0).getAstNode().get();
			if (node instanceof ClassBodyNode) {
				body = (ClassBodyNode) node;

				body.add(r.get(1).getAstNode().get());
			} else {
				for (Symbol s : r) {
					body.add(s.getAstNode().get());
				}
			}
			p.setAstNode(body);

		});

		getNonTerminal("classBodyDeclaration").addSemanticAction((p, r) -> {

			p.setSemanticAttribute("node", r.get(0).getSemanticAttribute("node").get());

		});

		getNonTerminal("interfaceMemberDeclaration").addSemanticAction((p, r) -> {

			p.setAstNode(r.get(0).getAstNode().get());

		});
		getNonTerminal("classMemberDeclaration").addSemanticAction((p, r) -> {

			p.setAstNode(r.get(0).getAstNode().get());

		});

		getNonTerminal("imutabilityModifier").addSemanticAction((p, r) -> {

			
			final Optional<Object> mutableOrLet = r.get(0).getSemanticAttribute("lexicalValue");
			final Optional<Object> letOrEmpty = r.size() > 1 ? r.get(1).getSemanticAttribute("lexicalValue") : Optional.empty();
			
			if (!letOrEmpty.isPresent() && mutableOrLet.get().equals("let")) {
				p.setAstNode(new ImutabilityNode(Imutability.Imutable));
			} else {
				p.setAstNode(new ImutabilityNode(Imutability.Mutable));
			}

		});

		getNonTerminal("fieldDeclaration").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				FieldDeclarationNode n = new FieldDeclarationNode();

				Modifiers modifiers = new Modifiers();

				int nextNodeIndex = readModifiers(r, modifiers, IdentifierNode.class);


				if (modifiers.getAnnotations()!=null) {
					n.setAnnotations(modifiers.getAnnotations());
				}

				n.setImutability(modifiers.getImutability());

				n.setName(r.get(nextNodeIndex).getAstNode(IdentifierNode.class).get().getName());

				nextNodeIndex++;

				String separator = r.get(nextNodeIndex).getLexicalValue();

				if (":".equals(separator)) {
					n.setTypeNode(ensureTypeNode(r.get(nextNodeIndex+ 1).getAstNode().get()));
				}

				Optional<AstNode> last = r.get(r.size() - 2).getAstNode();
				if (last.isPresent()){
					AstNode u = last.get();
					if (u instanceof IdentifierNode) {
						u = new VariableReadNode(((IdentifierNode) u).getName());
					} else if (u instanceof ExpressionNode){
						n.setInitializer((ExpressionNode) u);
					}
				}





				p.setAstNode(n);
			}
		});

		getNonTerminal("type").addSemanticAction((p, r) -> {
			if (r.size() == 2) {
				TypeNode maybe = new TypeNode("lense.core.lang.Maybe");
				maybe.addParametricType(new GenericTypeParameterNode((TypeNode)	r.get(0).getAstNode().get() ));

				p.setAstNode(maybe);
			} else  {
				p.setAstNode(r.get(0).getAstNode().get());
			}
		});

		getNonTerminal("nonNullType").addSemanticAction((p, r) -> {

			if (r.size() == 3) {

				List<AstNode> list = r.get(1).getAstNode(TypesListNode.class).get().getChildren();

				TypeNode next = new TypeNode("lense.core.collections.Tuple");
				next.addParametricType(new GenericTypeParameterNode((TypeNode) list.get(list.size() - 1)));
				next.addParametricType(new GenericTypeParameterNode(new TypeNode(LenseTypeSystem.Nothing())));

				for (int i = list.size() - 2; i >= 0; i--) {
					TypeNode current = new TypeNode("lense.core.collections.Tuple");
					current.addParametricType(new GenericTypeParameterNode((TypeNode) list.get(i)));
					current.addParametricType(new GenericTypeParameterNode(next));

					next = current;
				}
				p.setAstNode(next);

			} else {
				AstNode node = new QualifiedNameNode(r.get(0).getLexicalValue());

				QualifiedNameNode name = null;
				if (node instanceof QualifiedNameNode) {
					name = (QualifiedNameNode) node;
				} else if (node instanceof IdentifierNode) {
					name = new QualifiedNameNode();
					name.append(((IdentifierNode) node).getName());
				}

				TypeNode type;
				if (name.getName().endsWith("?")) {
					name = new QualifiedNameNode(name.getName().substring(0, name.getName().length() - 1));
					TypeNode paramter = new TypeNode(name);
					type = new TypeNode(new QualifiedNameNode("lense.core.lang.Maybe"));
					type.addParametricType(new GenericTypeParameterNode(paramter, Variance.Covariant));
				} else {
					type = new TypeNode(name);
				}

				type.setScanPosition(r.get(0).getParserTreeNode().getScanPosition());

				p.setAstNode(type);

				if (r.size() > 3) {

					AstNode n = r.get(2).getAstNode().get();

					if (n instanceof GenericTypeParameterNode) {
						type.addParametricType((GenericTypeParameterNode) n);
					} else if (n instanceof TypeParametersListNode) {
						for (AstNode a : n.getChildren()) {
							type.addParametricType((GenericTypeParameterNode) a);
						}
					} else {
						GenericTypeParameterNode generic = new GenericTypeParameterNode();
						generic.add(n);

						type.addParametricType(generic);
					}
				}
			}
		});

		getNonTerminal("varianceModifier").addSemanticAction((p, r) -> {
			final Optional<Object> semanticAttribute = r.get(0).getSemanticAttribute("lexicalValue");
			if (semanticAttribute.isPresent() && semanticAttribute.get().equals("in")) {
				p.setAstNode(new VarianceNode(Variance.ContraVariant));
			} else if (semanticAttribute.isPresent() && semanticAttribute.get().equals("out")) {
				p.setAstNode(new VarianceNode(Variance.Covariant));
			} else {
				p.setAstNode(new VarianceNode(Variance.Invariant));
			}

		});

		getNonTerminal("parametricType").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				AstNode node = r.get(0).getAstNode().get();
				if (node instanceof GenericTypeParameterNode) {
					p.setAstNode(node);
				} else {
					GenericTypeParameterNode ptn = new GenericTypeParameterNode();
					ptn.add(node);
					p.setAstNode(ptn);
				}
			} else {
				VarianceNode variance = r.get(0).getAstNode(VarianceNode.class)
						.orElse(new VarianceNode(Variance.Invariant));

				TypeNode typeNode = r.get(1).getAstNode(TypeNode.class).get();

				GenericTypeParameterNode node = new GenericTypeParameterNode();
				node.setTypeNode(typeNode);
				node.setVariance(variance.getVariance());
				p.setAstNode(node);

			}

		});

		getNonTerminal("parametricTypes").addSemanticAction((p, r) -> {

			if (r.get(0).getAstNode(TypeParametersListNode.class).isPresent()) {
				TypeParametersListNode list = r.get(0).getAstNode(TypeParametersListNode.class).get();
				for (int i = 2; i < r.size(); i += 2) {
					AstNode node = r.get(i).getAstNode().get();
					list.add(node);
				}
				p.setAstNode(list);
			} else {
				TypeParametersListNode list = new TypeParametersListNode();
				for (int i = 0; i < r.size(); i += 2) {
					AstNode node = r.get(i).getAstNode().get();
					list.add(node);
				}
				p.setAstNode(list);
			}

		});

		getNonTerminal("returnType").addSemanticAction((p, r) -> {

			if (r.get(0).getAstNode().get() instanceof TypeNode) {
				p.setAstNode(r.get(0).getAstNode().get());
			}

		});

		getNonTerminal("visibilityModifier").addSemanticAction((p, r) -> {
			final Optional<Object> semanticAttribute = r.get(0).getSemanticAttribute("lexicalValue");
			if (!semanticAttribute.isPresent()) {
				p.setAstNode(new VisibilityNode(Visibility.Undefined));
			} else if (semanticAttribute.get().equals("public")) {
				p.setAstNode(new VisibilityNode(Visibility.Public));
			} else if (semanticAttribute.get().equals("protected")) {
				p.setAstNode(new VisibilityNode(Visibility.Protected));
			} else if (semanticAttribute.get().equals("private")) {
				p.setAstNode(new VisibilityNode(Visibility.Private));
			}

		});


		getNonTerminal("implementationModifiers").addSemanticAction((p, r) -> {
			if (r.size() == 1){
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				ImplementationModifierNode node = r.get(0).getAstNode(ImplementationModifierNode.class).get();
				ImplementationModifierNode other = r.get(1).getAstNode(ImplementationModifierNode.class).get();

				var it = node.merge(other);
				
				
				if (it.isValueClass() && it.isMutable()) {
					throw new CompilationError(node, "A value type cannot be mutable. Remove one of the constraints.");
				}
				
				p.setAstNode(it);
				
			}

		});

		getNonTerminal("implementationModifier").addSemanticAction((p, r) -> {
			final Optional<Object> semanticAttribute = r.get(0).getSemanticAttribute("lexicalValue");
			ImplementationModifierNode node = new ImplementationModifierNode();

			if (semanticAttribute.isPresent() && semanticAttribute.get().equals("abstract")) {
				node.setAbstract(true);
			} else if (semanticAttribute.isPresent() && semanticAttribute.get().equals("native")) {
				node.setNative(true);
			} else if (semanticAttribute.isPresent() && semanticAttribute.get().equals("sealed")) {
				node.setSealed(true);
			} else if (semanticAttribute.isPresent() && semanticAttribute.get().equals("final")) {
				node.setFinal(true);
			} else if (semanticAttribute.isPresent() && semanticAttribute.get().equals("default")) {
				node.setDefault(true);
			} else if (semanticAttribute.isPresent() && semanticAttribute.get().equals("override")) {
				node.setOverride(true);
			} else if (semanticAttribute.isPresent() && semanticAttribute.get().equals("value")) {
				node.setValueClass(true);
			} else if (semanticAttribute.isPresent() && semanticAttribute.get().equals("mutable")) {
				node.setMutable(true);
			} else if (semanticAttribute.isPresent() && semanticAttribute.get().equals("type")) {
				node.setTypeClass(true);
			} else if (semanticAttribute.isPresent() && semanticAttribute.get().equals("satisfy")) {
				node.setSatisfy(true);
			}
			
			
			p.setAstNode(node);
		});

		getNonTerminal("constructorExtension").addSemanticAction((p, r) -> {
			ConstructorExtentionNode node = new ConstructorExtentionNode();

			int separatorIndex = findIndexOf(r, s -> s.getLexicalValue().equals("(") );

			String level = r.get(1).getLexicalValue();

			node.setCallLevel(level);

			if (separatorIndex > 2){
				String constructorName = r.get(1).getLexicalValue();
				node.setConstructorName(constructorName);
			}

			var nextNode = r.get(separatorIndex + 1).getAstNode();
			
			if (nextNode.isPresent()) {
				
				if (nextNode.get() instanceof ArgumentListNode args) {
					node.setArguments(args);
				}
			
		
			}
			
			p.setAstNode(node);

		});

		getNonTerminal("constructorDeclaration").addSemanticAction((p, r) -> {

			Modifiers modifiers = new Modifiers();

			int next = readModifiers(r, modifiers, ConstructorDeclarationNode.class);

			ConstructorDeclarationNode declarator = r.get(next++).getAstNode(ConstructorDeclarationNode.class).get();
			if (modifiers.getImplementationModifier().isAbstract()) {
				throw new CompilationError(modifiers.getImplementationModifier(), "Constructors can not be abstract.");
			}

			if (modifiers.getAnnotations() != null) {
				declarator.setAnnotations(modifiers.getAnnotations());
			}

			declarator.setAbstract(modifiers.getImplementationModifier().isAbstract());
			declarator.setNative(modifiers.getImplementationModifier().isNative());
			declarator.setVisibility(modifiers.getVisibility().getVisibility(Visibility.Private));

			Optional<TypeParametersListNode> generics = r.get(next + 1).getAstNode(TypeParametersListNode.class);

			if (generics.isPresent()) {
				declarator.setMethodScopeGenerics(generics.get());
			}
			next++;


			Optional<ParametersListNode> formalParams = r.get(next + 1).getAstNode(ParametersListNode.class);

			if (formalParams.isPresent()) {
				declarator.setParameters(formalParams.get());
			}
			// FormalParameterNode
			Optional<ConstructorExtentionNode> constructorExtentionNode = r.size() > next + 3 
					? r.get(next + 3).getAstNode(ConstructorExtentionNode.class)
							: Optional.empty();

					if (constructorExtentionNode.isPresent()){
						next++;
						declarator.setExtention(constructorExtentionNode.get());
					}

					Optional<BlockNode> block = r.get(r.size() - 1).getAstNode(BlockNode.class);

					if (block.isPresent()) {
						declarator.setBlock(block.get());
						declarator.setPrimary(false);
					} else {
						declarator.setBlock(null);
						declarator.setPrimary(true);
					}

					p.setAstNode(declarator);

		});

		getNonTerminal("constructorDeclarator").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				Optional<ConstructorDeclarationNode> n = r.get(0).getAstNode(ConstructorDeclarationNode.class);

				if (n.isPresent()) {
					p.setAstNode(n.get());
				} else {
					p.setAstNode(new ConstructorDeclarationNode());
				}

			} else if (r.size() == 2) {
				ConstructorDeclarationNode n = new ConstructorDeclarationNode();

				Optional<Object> id = r.get(1).getSemanticAttribute("lexicalValue");
				if (id.isPresent() && !id.get().equals("constructor")) {
					n.setName((String) id.get());
				} else {
					n.setImplicit(true);
				}
				p.setAstNode(n);
			} else if (r.size() == 3) {
				ConstructorDeclarationNode n = new ConstructorDeclarationNode();

				n.setImplicit(true);
				n.setName((String) r.get(2).getSemanticAttribute("lexicalValue").get());
				p.setAstNode(n);
			} else {
				throw new RuntimeException();
			}

		});

		getNonTerminal("constructorFormalParameterList").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				if (r.get(0).getAstNode(ParametersListNode.class).isPresent()) {
					p.setAstNode(r.get(0).getAstNode().get());
				} else {
					ParametersListNode list = new ParametersListNode();
					list.add(r.get(0).getAstNode().get());
					p.setAstNode(list);
				}

			} else if (r.size() == 3) {

				ParametersListNode list;
				if (r.get(0).getAstNode().get() instanceof ParametersListNode) {
					list = r.get(0).getAstNode(ParametersListNode.class).get();
					list.add(r.get(2).getAstNode().get());
				} else {
					list = new ParametersListNode();
					list.add(r.get(0).getAstNode().get());
					list.add(r.get(2).getAstNode().get());
				}
				p.setAstNode(list);
			}

		});

		getNonTerminal("constructorFormalParameter").addSemanticAction((p, r) -> {
			if (r.get(0).getSemanticAttribute("node").isPresent()
					&& r.get(0).getSemanticAttribute("node").get() instanceof VariableDeclarationNode) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				FormalParameterNode n = new FormalParameterNode();


				Modifiers modifiers = new Modifiers();

				int nextNodeIndex = readModifiers(r, modifiers, IdentifierNode.class);

				Optional<IdentifierNode> id = r.get(nextNodeIndex).getAstNode(IdentifierNode.class);

				if (id.isPresent()){
					n.setName(id.get().getName());
				}

				n.setImutability(modifiers.getImutability());
				n.setVisibility(modifiers.getVisibility().getVisibility(Visibility.Undefined));

				nextNodeIndex+=2;

				n.setTypeNode(ensureTypeNode(r.get(nextNodeIndex).getAstNode().get()));

				p.setAstNode(n);
			}
		});

		getNonTerminal("construtorBlock").addSemanticAction((p, r) -> {
			if (r.get(0).getAstNode().isPresent()) {
				p.setAstNode(r.get(0).getAstNode().get());
			} 
		});

		getNonTerminal("methodDeclaration").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode( r.get(0).getAstNode().get());
			} else {
				MethodDeclarationNode n = (MethodDeclarationNode) r.get(0).getAstNode().get();

				int bodyStartPosition = 1;
				if (":".equals(r.get(1).getLexicalValue())) {
					TypeNode type = r.get(2).getAstNode(TypeNode.class).get();

					n.setReturnType(type);
					bodyStartPosition= 3;
				} else {
					n.setReturnType(new InferableTypeNode());

					//throw new CompilationError(r.get(0).getAstNode().get(), "Return type is required");
				}

				String separator = r.get(bodyStartPosition).getLexicalValue();

				if (separator == null ){
					n.setBlock((BlockNode) r.get(bodyStartPosition).getAstNode().get());
				} else if ("{".equals(separator)){
					n.setBlock((BlockNode) r.get(bodyStartPosition + 1).getAstNode().get());
				} else if (";".equals(separator)){
					//no-op
				} else if ("=>".equals(separator)){
					ExpressionNode exp = ensureExpression(r.get(bodyStartPosition + 1).getAstNode().get());
					BlockNode block = new BlockNode();
					ReturnNode rNode = new ReturnNode();
					block.add(rNode);
					rNode.setValue(exp);
					n.setBlock(block );
				} else  {
					throw new CompilationError(r.get(0).getAstNode().get(), "Invalid syntax");
				}

				p.setAstNode(n);
			}

		});

		getNonTerminal("methodBody").addSemanticAction((p, r) -> {
			if (r.get(0).getAstNode().isPresent()) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				p.setAstNode(new BlockNode());
			}
		});

		getNonTerminal("block").addSemanticAction((p, r) -> {

			if (r.size() == 3) {
				p.setSemanticAttribute("node", r.get(1).getSemanticAttribute("node").get());
			} else if (r.size() == 1) {
				if (r.get(0).getSemanticAttribute("node").isPresent()) {
					p.setSemanticAttribute("node", r.get(0).getSemanticAttribute("node").get());
				} else {
					p.setSemanticAttribute("node", new BlockNode());
				}
			}
		});

		getNonTerminal("initializer").addSemanticAction((p, r) -> {
			p.setAstNode(r.get(0).getAstNode().get());
		});

		getNonTerminal("arrayInitializer").addSemanticAction((p, r) -> {
			LiteralSequenceInstanceCreation arrayNode = new LiteralSequenceInstanceCreation(
					r.get(1).getAstNode(ArgumentListNode.class).get());
			p.setAstNode(arrayNode);
		});

		getNonTerminal("arrayInitializerVariables").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				if (r.get(0).getAstNode().get() instanceof ArgumentListNode) {
					p.setAstNode(r.get(0).getAstNode().get());
				} else {
					ArgumentListNode node = new ArgumentListNode();
					node.add(r.get(0).getAstNode().get());
					p.setAstNode(node);
				}

			} else {
				ArgumentListNode node = r.get(0).getAstNode(ArgumentListNode.class).get();
				node.add(r.get(2).getAstNode().get());
				p.setAstNode(node);
			}
		});

		getNonTerminal("initializerVariable").addSemanticAction((p, r) -> {
			p.setAstNode(r.get(0).getAstNode().get());
		});

		getNonTerminal("tupleTypes").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				if (r.get(0).getAstNode().get() instanceof TypesListNode) {
					p.setAstNode(r.get(0).getAstNode().get());
				} else {
					TypesListNode list = new TypesListNode();
					list.add(r.get(0).getAstNode().get());
					p.setAstNode(list);
				}
			} else {
				TypesListNode list = r.get(0).getAstNode(TypesListNode.class).get();
				list.add(r.get(2).getAstNode().get());
				p.setAstNode(list);
			}
		});

		getNonTerminal("tupleInitializer").addSemanticAction((p, r) -> {

			if (r.size() == 2) {
				p.setAstNode(new lense.compiler.ast.VoidValue());
			} else {
				List<AstNode> list = r.get(1).getAstNode().get().getChildren();

				AstNode last = list.get(list.size() - 1);

				LiteralTupleInstanceCreation next = new LiteralTupleInstanceCreation(last);

				for (int i = list.size() - 2; i >= 0; i--) {
					AstNode current = list.get(i);

					next = new LiteralTupleInstanceCreation(current, next);
				}

				p.setAstNode(next);
			}

		});

		getNonTerminal("tupleInitializerVariables").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				if (r.get(0).getAstNode().get() instanceof ArgumentListNode) {
					p.setAstNode(r.get(0).getAstNode().get());
				} else {
					ArgumentListNode node = new ArgumentListNode();
					node.add(r.get(0).getAstNode().get());
					p.setAstNode(node);
				}

			} else {
				ArgumentListNode node = r.get(0).getAstNode(ArgumentListNode.class).get();
				node.add(r.get(2).getAstNode().get());
				p.setAstNode(node);
			}
		});

		getNonTerminal("mapInitializer").addSemanticAction((p, r) -> {
			LiteralAssociationInstanceCreation mapNode = new LiteralAssociationInstanceCreation(
					r.get(1).getAstNode(ArgumentListNode.class).get());
			p.setAstNode(mapNode);
		});

		getNonTerminal("mapInitializerVariables").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				if (r.get(0).getAstNode().get() instanceof ArgumentListNode) {
					p.setAstNode(r.get(0).getAstNode().get());
				} else {
					ArgumentListNode node = new ArgumentListNode();
					node.add(r.get(0).getAstNode().get());
					p.setAstNode(node);
				}

			} else {
				ArgumentListNode node = r.get(0).getAstNode(ArgumentListNode.class).get();
				node.add(r.get(2).getAstNode().get());
				p.setAstNode(node);
			}
		});

		getNonTerminal("mapInitializerPair").addSemanticAction((p, r) -> {

			//TODO create literal node to pass foward
			
			var candidate = LenseTypeSystem.KeyValuePair().getAllMembers().stream()
            .filter(m -> m.isConstructor())
            .map(m -> (Constructor) m)
            .filter(c -> c.getParameters().size() == 2) 
			.findFirst().get();
	
//			List<Match<Constructor>> ops = LenseTypeSystem.KeyValuePair().getConstructorByParameters(
//					new ConstructorParameter(LenseTypeSystem.Any()),
//					new ConstructorParameter(LenseTypeSystem.Any())
//					);

			NewInstanceCreationNode pair = NewInstanceCreationNode.of(
					LenseTypeSystem.KeyValuePair(),
					candidate,
					r.get(0).getAstNode().get(), 
					r.get(2).getAstNode().get()
					);

			p.setAstNode(pair);
		});

		getNonTerminal("mapInitializerVariable").addSemanticAction((p, r) -> {
			p.setAstNode(r.get(0).getAstNode().get());
		});

		getNonTerminal("abstractMethodDeclaration").addSemanticAction((p, r) -> {

			MethodDeclarationNode n = new MethodDeclarationNode();

			Modifiers modifiers = new Modifiers();

			int nextNodeIndex = readModifiers(r, modifiers, ParametersListNode.class);

			int typeIndex = nextNodeIndex + 2;
			if (nextNodeIndex < 0){
				nextNodeIndex = 0;
				for (int i = 0; i < r.size(); i++) {
					if (")".equals(r.get(i).getLexicalValue())) {
						nextNodeIndex = i;
						break;
					}
				}
			    typeIndex = nextNodeIndex + 1;
			} else {
				n.setParameters(r.get(nextNodeIndex).getAstNode(ParametersListNode.class).get());
			}

			if (modifiers.getAnnotations() != null) {
				n.setAnnotations(modifiers.getAnnotations());

			}

			Optional<TypeParametersListNode> generics = r.get(nextNodeIndex - 2).getAstNode(TypeParametersListNode.class);

			if (generics.isPresent()) {
				n.setMethodScopeGenerics(generics.get());
			}

			n.setName((String) r.get(nextNodeIndex - 3).getSemanticAttribute("lexicalValue").get());

			applyImplementationModifiers(n, modifiers);
			

			

			if (":".equals(r.get(typeIndex).getLexicalValue())) {
				TypeNode type = r.get(typeIndex + 1).getAstNode(TypeNode.class).get();

				n.setReturnType(type);
			} else {
				throw new CompilationError(r.get(0).getAstNode().get(), "Return type is required");
			}

			p.setSemanticAttribute("node", n);
		});

		getNonTerminal("methodHeader").addSemanticAction((p, r) -> {

			MethodDeclarationNode n = new MethodDeclarationNode();

			Modifiers modifiers = new Modifiers();

			int nextNodeIndex = readModifiers(r, modifiers, ParametersListNode.class);

			if (nextNodeIndex < 0){
				nextNodeIndex = r.size() - 1;
			} else {
				n.setParameters(r.get(nextNodeIndex).getAstNode(ParametersListNode.class).get());
			}

			if (modifiers.getAnnotations() != null) {
				n.setAnnotations(modifiers.getAnnotations());

			}

			Optional<TypeParametersListNode> generics = r.get(nextNodeIndex - 2).getAstNode(TypeParametersListNode.class);

			if (generics.isPresent()) {
				n.setMethodScopeGenerics(generics.get());
			}

			n.setName((String) r.get(nextNodeIndex - 3).getSemanticAttribute("lexicalValue").get());

			applyImplementationModifiers(n, modifiers);


			//
			//			int typeIndex = nextNodeIndex + 4;
			//			if (r.get(nextNodeIndex + 3).getSemanticAttribute("node").isPresent()) {
			//				AstNode list = (AstNode) r.get(nextNodeIndex + 3).getSemanticAttribute("node").get();
			//
			//				if (list instanceof ParametersListNode) {
			//					n.setParameters((ParametersListNode) list);
			//
			//				} else {
			//					ParametersListNode ln = new ParametersListNode();
			//					ln.add(list);
			//					n.setParameters(ln);
			//				}
			//				typeIndex = nextNodeIndex + 5;
			//			}

			p.setAstNode(n);
		});

		getNonTerminal("blockStatements").addSemanticAction((p, r) -> {

			AstNode other = r.get(0).getAstNode().get();
			BlockNode n;
			if (other instanceof BlockNode) {
				n = (BlockNode) other;
				n.add(r.get(1).getAstNode().get());
			} else {
				n = new BlockNode();
				n.add(r.get(0).getAstNode().get());
				if (r.size() > 1) {
					n.add(r.get(1).getAstNode().get());
				}
			}

			p.setAstNode(n);
		});

		getNonTerminal("blockStatement").addSemanticAction((p, r) -> {

			p.setAstNode(r.get(0).getAstNode().get());
		});

		getNonTerminal("throwsStatement").addSemanticAction((p, r) -> {

			ThowNode node = new ThowNode(ensureExpression(r.get(1).getAstNode().get()));

			p.setAstNode(node);
		});

		getNonTerminal("assertStatement").addSemanticAction((p, r) -> {

			AssertNode node = new AssertNode(ensureExpression(r.get(2).getAstNode().get()));

			if (r.size() > 5) {
				node.setText(ensureExpression(r.get(4).getAstNode().get()));
			}
			
			p.setAstNode(node);
		});

		SemanticAction upstream = (p, r) -> {
			p.setAstNode(r.get(0).getAstNode().get());
		};

		getNonTerminal("statement").addSemanticAction(upstream);
		getNonTerminal("statementWithoutTrailingSubstatement").addSemanticAction(upstream);
		getNonTerminal("conditionalExpression").addSemanticAction(upstream);
		getNonTerminal("expressionStatement").addSemanticAction(upstream);
		getNonTerminal("statementExpression").addSemanticAction(upstream);
		getNonTerminal("constantExpression").addSemanticAction(upstream);

		getNonTerminal("postincrementExpression").addSemanticAction((p, r) -> {
			PosExpression exp = new PosExpression(ArithmeticOperation.Increment);
			exp.add(ensureExpression(r.get(0).getAstNode().get()));
			p.setAstNode(exp);
		});

		getNonTerminal("postdecrementExpression").addSemanticAction((p, r) -> {
			PosExpression exp = new PosExpression(ArithmeticOperation.Decrement);
			exp.add(ensureExpression(r.get(0).getAstNode().get()));
			p.setAstNode(exp);
		});

		getNonTerminal("returnStatement").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				ReturnNode node = new ReturnNode();

				if (r.size() == 3) {
					node.setValue(ensureExpression(r.get(1).getAstNode().get()));
				}

				p.setAstNode(node);
			}
		});

		getNonTerminal("breakStatement").addSemanticAction((p, r) -> {

			BreakNode node = new BreakNode();

			// TODO add label

			p.setAstNode(node);

		});

		getNonTerminal("continueStatement").addSemanticAction((p, r) -> {

			ContinueNode node = new ContinueNode();

			// TODO add label

			p.setAstNode(node);

		});

		getNonTerminal("tryStatement").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				TryStatement node = new TryStatement();

				int nextNodeIndex = 3;
				if (r.get(1).getAstNode().isPresent() && r.get(1).getAstNode().get() instanceof BlockNode) {
					// no try-resource
					node.setInstructions(r.get(1).getAstNode(BlockNode.class).get());
					nextNodeIndex = 2;
				} else {
					// try-resource
					node.setResource(r.get(1).getAstNode(ExpressionNode.class).get());
					node.setInstructions(r.get(2).getAstNode(BlockNode.class).get());
					nextNodeIndex = 3;
				}

				if (r.get(nextNodeIndex).getAstNode().isPresent()) {
					AstNode n = r.get(nextNodeIndex).getAstNode().get();
					if (n instanceof CatchOptionNode) {
						CatchOptionsNode c = new CatchOptionsNode();
						c.add(n);
						node.setCatchOptions(c);
					} else {
						node.setCatchOptions((CatchOptionsNode) n);
					}

				}

				if (nextNodeIndex + 1 < r.size() && r.get(nextNodeIndex + 1).getAstNode().isPresent()) {
					// finally
					node.setFinally(r.get(nextNodeIndex + 1).getAstNode(BlockNode.class).get());
				}

				p.setAstNode(node);
			}
		});

		getNonTerminal("catches").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				AstNode n = r.get(0).getAstNode().get();
				if (n instanceof CatchOptionsNode) {
					CatchOptionsNode node = (CatchOptionsNode) n;
					node.add(r.get(1).getAstNode().get());
					p.setAstNode(node);
				} else {
					CatchOptionsNode node = new CatchOptionsNode();
					node.add(r.get(0).getAstNode().get());
					node.add(r.get(1).getAstNode().get());
					p.setAstNode(node);
				}
			}
		});

		getNonTerminal("catchClause").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				CatchOptionNode node = new CatchOptionNode();

				node.setExceptions(r.get(2).getAstNode(FormalParameterNode.class).get());
				node.setInstructions(r.get(4).getAstNode(BlockNode.class).get());

				p.setAstNode(node);
			}
		});

		getNonTerminal("finally").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				p.setAstNode(r.get(1).getAstNode().get());
			}
		});

		getNonTerminal("ifThenStatement").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				DecisionNode node = new DecisionNode();

				
				node.setCondition(r.get(2).getAstNode().map(a -> ensureExpression(a)).get());
				if (r.get(4).getAstNode(BlockNode.class).isPresent()) {
					node.setTruePath(r.get(4).getAstNode(BlockNode.class).get());
				}

				if (r.size() >= 7 && r.get(6).getAstNode().isPresent()) {
					node.setFalsePath(r.get(6).getAstNode().get());
				}

				p.setAstNode(node);
			}

		});

		getNonTerminal("elseOptions").addSemanticAction((p, r) -> {
			if (r.get(0).getAstNode().isPresent()) {
				p.setAstNode(r.get(0).getAstNode().get());
			}

		});

		getNonTerminal("ifThenElseStatement").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				DecisionNode node = new DecisionNode();

				node.setCondition(ensureExpression(r.get(2).getAstNode().get()));
				node.setTruePath(r.get(4).getAstNode(BlockNode.class).get());
				node.setFalsePath(r.get(6).getAstNode(BlockNode.class).get());

				p.setAstNode(node);
			}

		});

		getNonTerminal("ifThenElseIfStatement").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				DecisionNode node = r.get(0).getAstNode(DecisionNode.class).get();
				DecisionNode alternative = (DecisionNode) node.getFalseBlock();

				alternative.setFalsePath(r.get(1).getAstNode().get());
				p.setAstNode(node);
			}

		});

		getNonTerminal("conditionsList").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				DecisionNode node = new DecisionNode();

				node.setCondition(ensureExpression(r.get(2).getAstNode().get()));
				node.setTruePath(r.get(4).getAstNode(BlockNode.class).get());

				DecisionNode node2 = new DecisionNode();

				node.setFalsePath(node2);

				node2.setCondition(r.get(8).getAstNode(ExpressionNode.class).get());
				node2.setTruePath(r.get(10).getAstNode(BlockNode.class).get());

				p.setAstNode(node);
			}

		});

		getNonTerminal("endAlternative").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else if (r.size() == 2) {
				p.setAstNode(r.get(1).getAstNode(BlockNode.class).get());
			} else {
				DecisionNode node = new DecisionNode();

				node.setCondition(r.get(3).getAstNode(ExpressionNode.class).get());
				node.setTruePath(r.get(5).getAstNode(BlockNode.class).get());
				node.setFalsePath(r.get(6).getAstNode().get());

				p.setAstNode(node);
			}

		});

		getNonTerminal("whileStatement").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				WhileNode node = new WhileNode();

				node.setCondition(ensureExpression(r.get(2).getAstNode().get()));

				if (r.size() > 4 && r.get(4).getAstNode(BlockNode.class).isPresent()) {
					node.setBlock(r.get(4).getAstNode(BlockNode.class).get());
				}

				p.setAstNode(node);
			}

		});

		getNonTerminal("forStatement").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				ForEachNode node = new ForEachNode();

				VariableDeclarationNode var = new VariableDeclarationNode();

				if (r.get(2).getAstNode(IdentifierNode.class).isPresent()) {
					var.setName(r.get(2).getAstNode(IdentifierNode.class).map(id -> id.getName()).get());
				} else {

					var.setName(r.get(2).getAstNode(VariableDeclarationNode.class).map(v -> v.getName()).get());
				}

				var.setTypeNode(new lense.compiler.ast.InferableTypeNode());

				node.setIterableVariable(var);
				node.setContainer(ensureExpression(r.get(4).getAstNode().get()));

				if (r.size() > 4 && r.get(6).getAstNode(BlockNode.class).isPresent()) {
					node.setBlock(r.get(6).getAstNode(BlockNode.class).get());
				}

				p.setAstNode(node);
			}

		});

		getNonTerminal("iterationType").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				VariableDeclarationNode node = new VariableDeclarationNode();

				node.setImutability(new ImutabilityNode(Imutability.Imutable));
				if (r.size() == 2) {
					node.setName(r.get(1).getAstNode(IdentifierNode.class).get().getName());
				} else {
					node.setName(r.get(0).getAstNode(IdentifierNode.class).get().getName());
				}

				p.setAstNode(node);
			}

		});

		getNonTerminal("switchStatement").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				SwitchNode node = new SwitchNode();

				node.setCandidate(ensureExpression(r.get(2).getAstNode().get()));
				node.setOptions(r.get(4).getAstNode(SwitchOptions.class).get());

				p.setAstNode(node);
			}

		});

		getNonTerminal("switchBlock").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				p.setAstNode(r.get(1).getAstNode().get());
			}

		});

		getNonTerminal("switchLabels").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				AstNode a = r.get(0).getAstNode().get();

				if (a instanceof SwitchOptions) {
					p.setAstNode(r.get(0).getAstNode().get());
				} else {
					SwitchOptions node = new SwitchOptions();
					node.add(a);

					p.setAstNode(node);
				}
			} else {

				AstNode a = r.get(0).getAstNode().get();

				SwitchOptions node = new SwitchOptions();
				if (a instanceof SwitchOptions) {
					node = (SwitchOptions) a;
					node.add(r.get(1).getAstNode().get());
				} else {
					node.add(r.get(0).getAstNode().get());
					node.add(r.get(1).getAstNode().get());
				}
				p.setAstNode(node);
			}

		});

		getNonTerminal("switchLabel").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else if (r.size() == 2) {
				SwitchOption node = new SwitchOption(true);

				Optional<BlockNode> block = r.get(1).getAstNode(BlockNode.class);
				if (block.isPresent()) {
					node.setActions(block.get());
				}
				p.setAstNode(node);
			} else {
				SwitchOption node = new SwitchOption();

				node.setValue(ensureExpression(r.get(2).getAstNode().get()));

				Optional<BlockNode> block = r.get(4).getAstNode(BlockNode.class);
				if (block.isPresent()) {
					node.setActions(block.get());
				}

				p.setAstNode(node);
			}

		});

		getNonTerminal("localVariableDeclarationStatement").addSemanticAction((p, r) -> {

			if (r.get(0).getSemanticAttribute("node").isPresent()
					&& r.get(0).getSemanticAttribute("node").get() instanceof VariableDeclarationNode) {
				p.setSemanticAttribute("node", r.get(0).getSemanticAttribute("node").get());
			} else {
				VariableDeclarationNode n = new VariableDeclarationNode();

				int index = 0;
				Optional<ImutabilityNode> imutability = r.get(index).getAstNode()
						.filter(a -> a instanceof ImutabilityNode).map(a -> (ImutabilityNode) a);
				index = index + (imutability.isPresent() ? 1 : 0);

				n.setImutability(imutability.orElse(new ImutabilityNode(Imutability.Imutable)));

				index++; // bypass 'let' token
				
				n.setName(r.get(index++).getAstNode(IdentifierNode.class).get().getName());

				String separator = r.get(index++).getLexicalValue();
				if (":".equals(separator)) {
					n.setTypeNode(ensureTypeNode(r.get(index++).getAstNode().get()));
				} else {
					n.setTypeNode( new InferableTypeNode());
				}

				if (r.size() > index + 1) {
					if (r.get(r.size() - 2).getAstNode().isPresent()) {
						n.setInitializer(ensureExpression(r.get(r.size() - 2).getAstNode().get()));
					}

				}

				p.setAstNode( n);
			}

		});

		getNonTerminal("expressionName").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				if (!r.get(0).getAstNode().isPresent()) {
					p.setAstNode(new QualifiedNameNode(r.get(0).getLexicalValue()));
				} else if (r.get(0).getAstNode().get() instanceof IdentifierNode) {
					p.setAstNode(new QualifiedNameNode(r.get(0).getAstNode(IdentifierNode.class).get().getName()));
				} else {
					p.setAstNode(ensureQualifiedName(r.get(0).getAstNode().get()));
				}
			} else if (r.size() == 3) {

				QualifiedNameNode q = r.get(0).getAstNode(QualifiedNameNode.class).get();

				q.append(r.get(2).getLexicalValue());
				p.setAstNode(q);
			}

		});

		getNonTerminal("ambiguousName").addSemanticAction((p, r) -> {

			if (r.size() == 1) {

				if (!r.get(0).getAstNode().isPresent()) {
					p.setAstNode(new QualifiedNameNode(r.get(0).getLexicalValue()));
				} else if (r.get(0).getAstNode().get() instanceof IdentifierNode) {
					p.setAstNode(new QualifiedNameNode(r.get(0).getAstNode(IdentifierNode.class).get().getName()));
				} else {
					p.setAstNode(ensureQualifiedName(r.get(0).getAstNode().get()));
				}

			} else if (r.size() == 3) {

				QualifiedNameNode q = r.get(0).getAstNode(QualifiedNameNode.class).get();

				q.append(r.get(2).getLexicalValue());
				p.setAstNode(q);
			}

		});

		getNonTerminal("formalParameterList").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				if (r.get(0).getAstNode(ParametersListNode.class).isPresent()) {
					p.setAstNode(r.get(0).getAstNode().get());
				} else {
					ParametersListNode list = new ParametersListNode();
					list.add(r.get(0).getAstNode().get());
					p.setAstNode(list);
				}

			} else if (r.size() == 3) {

				ParametersListNode list;
				if (r.get(0).getAstNode().get() instanceof ParametersListNode) {
					list = r.get(0).getAstNode(ParametersListNode.class).get();
					list.add(r.get(2).getAstNode().get());
				} else {
					list = new ParametersListNode();
					list.add(r.get(0).getAstNode().get());
					list.add(r.get(2).getAstNode().get());
				}
				p.setAstNode(list);
			}

		});

		getNonTerminal("variableName").addSemanticAction((p, r) -> {

			if (r.get(0).getAstNode().isPresent()) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				p.setAstNode(new IdentifierNode((String) r.get(0).getSemanticAttribute("lexicalValue").get()));
			}

		});

		getNonTerminal("formalParameter").addSemanticAction((p, r) -> {
			if (r.get(0).getSemanticAttribute("node").isPresent()
					&& r.get(0).getSemanticAttribute("node").get() instanceof VariableDeclarationNode) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				FormalParameterNode n = new FormalParameterNode();
				n.setImutability(Imutability.Imutable); // parameters
				// shouldn't
				// be
				// changed

				if (r.get(0).getSemanticAttribute("lexicalValue").isPresent()) {
					IdentifierNode v = new IdentifierNode((String) r.get(0).getSemanticAttribute("lexicalValue").get());
					n.setName(v.getName());
				} else {
					n.setName(((IdentifierNode) r.get(0).getSemanticAttribute("node").get()).getName());
				}

				n.setTypeNode(ensureTypeNode(r.get(2).getAstNode().get()));

				p.setAstNode(n);
			}
		});

		getNonTerminal("expression").addSemanticAction((p, r) -> {
			p.setAstNode(r.get(0).getAstNode().get());
		});

		getNonTerminal("lambda").addSemanticAction((p, r) -> {
			if (r.size() == 5) {
				LambdaExpressionNode lambda = new LambdaExpressionNode();

				AstNode node = r.get(1).getAstNode().get();
				if (!(node instanceof ParametersListNode)) {
					ParametersListNode list = new ParametersListNode();
					list.add(node);
					node = list;
				}
				lambda.setParameters(node);

				lambda.setBody(ensureExpression(r.get(4).getAstNode().get()));
				p.setAstNode(lambda);
			} else if (r.size() == 3) {
				LambdaExpressionNode lambda = new LambdaExpressionNode();

				ParametersListNode list = new ParametersListNode();
				FormalParameterNode v = new FormalParameterNode(r.get(0).getLexicalValue());
				list.add(v);

				lambda.setParameters(list);
				lambda.setBody(ensureExpression(r.get(2).getAstNode().get()));
				p.setAstNode(lambda);
			}
		});

		getNonTerminal("lambdaExpression").addSemanticAction((p, r) -> {
			p.setAstNode(r.get(0).getAstNode().get());
		});

		getNonTerminal("lambdaParameters").addSemanticAction((p, r) -> {
			p.setAstNode(r.get(0).getAstNode().get());
		});

		getNonTerminal("variableNamesList").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				ParametersListNode list = new ParametersListNode();
				FormalParameterNode v = new FormalParameterNode(r.get(0).getLexicalValue());
				list.add(v);

				p.setAstNode(list);
			} else {
				ParametersListNode list = r.get(0).getAstNode(ParametersListNode.class).get();
				FormalParameterNode v = new FormalParameterNode(r.get(2).getLexicalValue());
				list.add(v);
				p.setAstNode(list);
			}

		});

		getNonTerminal("conditionalExpression").addSemanticAction((p, r) -> {
			p.setAstNode(r.get(0).getAstNode().get());
		});

		getNonTerminal("ternaryExpression").addSemanticAction((p, r) -> {

			TernaryConditionalExpressionNode node = new TernaryConditionalExpressionNode();

			node.setCondition(ensureExpression(r.get(0).getAstNode().get()));
			node.setThenExpression(ensureExpression(r.get(2).getAstNode().get()));
			node.setElseExpression(ensureExpression(r.get(4).getAstNode().get()));

			p.setAstNode(node);

		});

		SemanticAction booleanArithmetics = (p, r) -> {
			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {

				BooleanOperatorNode exp = new BooleanOperatorNode(resolveBooleanOperation(r.get(1)));
				exp.add(ensureExpression(r.get(0).getAstNode().get()));
				exp.add(ensureExpression(r.get(2).getAstNode().get()));
				p.setAstNode(exp);
			}
		};

		getNonTerminal("conditionalOrExpression").addSemanticAction(booleanArithmetics);
		getNonTerminal("conditionalAndExpression").addSemanticAction(booleanArithmetics);
		getNonTerminal("inclusiveOrExpression").addSemanticAction(booleanArithmetics);
		getNonTerminal("exclusiveOrExpression").addSemanticAction(booleanArithmetics);
		getNonTerminal("andExpression").addSemanticAction(booleanArithmetics);

		getNonTerminal("equalityExpression").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				ComparisonNode exp = new ComparisonNode(resolveComparisonOperation(r.get(1)));
				exp.add(ensureExpression(r.get(0).getAstNode().get()));
				exp.add(ensureExpression(r.get(2).getAstNode().get()));
				p.setAstNode(exp);
			}
		});

		getNonTerminal("relationalExpression").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				Operation op = resolveComparisonOperation(r.get(1));
				if (op == ComparisonNode.Operation.InstanceOf){
					InstanceOfNode exp = new InstanceOfNode();
					exp.add(ensureExpression(r.get(0).getAstNode().get()));
					exp.add(ensureTypeNode(r.get(2).getAstNode().get()));
					p.setAstNode(exp);
				} else {
					ComparisonNode exp = new ComparisonNode(op);
					exp.add(ensureExpression(r.get(0).getAstNode().get()));
					exp.add(ensureExpression(r.get(2).getAstNode().get()));
					p.setAstNode(exp);
				}

			}
		});

		SemanticAction arithmetics = (p, r) -> {
			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {

				ArithmeticNode exp = new ArithmeticNode(resolveOperation(r.get(1)));
				exp.add(ensureExpression(r.get(0).getAstNode().get()));
				exp.add(ensureExpression(r.get(2).getAstNode().get()));
				p.setAstNode(exp);
			}
		};
		getNonTerminal("shiftExpression").addSemanticAction(arithmetics);
		getNonTerminal("multiplicativeExpression").addSemanticAction(arithmetics);
		getNonTerminal("powerExpression").addSemanticAction(arithmetics);
		
		getNonTerminal("juxpositionExpression").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {

				JuxpositionNode exp = new JuxpositionNode();
				exp.add(ensureExpression(r.get(0).getAstNode().get()));
				exp.add(r.get(1).getAstNode().map(n -> ensureExpression(n)).orElseGet(() -> new FieldOrPropertyAccessNode(r.get(1).getLexicalValue())));
				p.setAstNode(exp);
			}
		});
		
		getNonTerminal("additiveExpression").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {

				ArithmeticNode exp = new ArithmeticNode(resolveOperation(r.get(1)));
				exp.add(ensureExpression(r.get(0).getAstNode().get()));
				exp.add(ensureExpression(r.get(2).getAstNode().get()));
				p.setAstNode(exp);
			}
		});

		getNonTerminal("intervalStart").addSemanticAction((p, r) -> {
			LiteralIntervalNode exp = new LiteralIntervalNode();
			exp.setStartClosed("|[".equals(r.get(0).getLexicalValue()));

			p.setAstNode(exp);
		});
		getNonTerminal("intervalEnd").addSemanticAction((p, r) -> {
			LiteralIntervalNode exp = new LiteralIntervalNode();
			exp.setEndClosed("|[".equals(r.get(0).getLexicalValue()));

			p.setAstNode(exp);
		});
		getNonTerminal("rangeExpression").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else 	if ("..".equals(r.get(1).getLexicalValue())) {
				RangeNode range = new RangeNode();
				range.add(ensureExpression(r.get(0).getAstNode().get()));
				range.add(ensureExpression(r.get(2).getAstNode().get()));
				p.setAstNode(range);
			} else if ("..<".equals(r.get(1).getLexicalValue())) {
				RangeNode range = new RangeNode();
				range.add(ensureExpression(r.get(0).getAstNode().get()));
				range.add(ensureExpression(r.get(2).getAstNode().get()));
				range.setIncludeEnd(false);
				p.setAstNode(range);
			} else {
				boolean startInf = "*".equals(r.get(1).getLexicalValue());
				boolean endInf = "*".equals(r.get(3).getLexicalValue());

				if (startInf && endInf ) {
					// should not be possible in current sintax
					throw new CompilationError("Universal intervals are not allowed");
				} else if (startInf){
					LiteralIntervalNode interval = r.get(4).getAstNode(LiteralIntervalNode.class).get();

					interval.setStartInf(true);

					interval.add(ensureExpression(r.get(3).getAstNode().get()));
					p.setAstNode(interval);
				} else if (endInf){
					LiteralIntervalNode interval = r.get(0).getAstNode(LiteralIntervalNode.class).get();

					interval.setEndInf(true);

					interval.add(ensureExpression(r.get(3).getAstNode().get()));
					p.setAstNode(interval);
				} else {
					LiteralIntervalNode s = r.get(0).getAstNode(LiteralIntervalNode.class).get();
					LiteralIntervalNode e = r.get(4).getAstNode(LiteralIntervalNode.class).get();

					LiteralIntervalNode interval = new LiteralIntervalNode();

					interval.setStartClosed(s.isStartClosed());
					interval.setEndClosed(e.isEndClosed());

					interval.add(ensureExpression(r.get(1).getAstNode().get()));
					interval.add(ensureExpression(r.get(3).getAstNode().get()));

					p.setAstNode(interval);
				}




			}

		});

		getNonTerminal("unaryExpression").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				PreExpression exp = new PreExpression(resolveUnaryOperation(r.get(0)));
				exp.add(ensureExpression(r.get(1).getAstNode().get()));
				p.setAstNode(exp);
			}
		});

		getNonTerminal("unaryExpressionNotPlusMinus").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				PreBooleanUnaryExpression exp = new PreBooleanUnaryExpression(resolveBooleanOperation(r.get(0)));
				exp.add(r.get(1).getAstNode(ExpressionNode.class).get());
				p.setAstNode(exp);
			}
		});

		getNonTerminal("castExpression").addSemanticAction((p, r) -> {
			p.setAstNode(r.get(0).getAstNode().get());
		});

		getNonTerminal("postfixExpression").addSemanticAction((p, r) -> {

			if (r.get(0).getAstNode().isPresent()) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				p.setAstNode(new IdentifierNode(r.get(0).getLexicalValue()));
			}

		});


		getNonTerminal("assignment").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {

				lense.compiler.ast.AssignmentNode.Operation op = resolveAssignmentOperation(r.get(1));
				AssignmentNode node;

				ExpressionNode left = ensureExpression(r.get(0).getAstNode().get());


				//                if (left instanceof QualifiedNameNode) {
				//                    if (((QualifiedNameNode) left).isComposed()) {
				//                        FieldOrPropertyAccessNode f = new FieldOrPropertyAccessNode(
				//                                ((QualifiedNameNode) left).getLast().getName());
				//                        f.setPrimary(((QualifiedNameNode) left).getPrevious());
				//                        left = f;
				//                    } else {
				//                        FieldOrPropertyAccessNode f = new FieldOrPropertyAccessNode(((QualifiedNameNode) left).getName());
				//                        left = f;
				//                    }
				//
				//                }

				if (op.isOperateAndAssign()) {
					node = new AssignmentNode(lense.compiler.ast.AssignmentNode.Operation.SimpleAssign);

					ArithmeticNode arithm = new ArithmeticNode(op.getArithmeticOperation());

					FieldOrPropertyAccessNode f = (FieldOrPropertyAccessNode)left;

					f = f.duplicate();

					arithm.add(f);

					arithm.add(ensureExpression(r.get(2).getAstNode().get()));

					node.setRight(arithm);

				} else {
					node = new AssignmentNode(op);
					node.setRight(ensureExpression(r.get(2).getAstNode().get()));
				}


				node.setLeft(left);

				p.setAstNode(node);
			}

		});

		getNonTerminal("assignmentOperator").addSemanticAction((p, r) -> {
			p.setSemanticAttribute("lexicalValue", r.get(0).getSemanticAttribute("lexicalValue").get());
		});

		getNonTerminal("leftHandSide").addSemanticAction((p, r) -> {
			p.setAstNode(r.get(0).getAstNode().get());
		});

		getNonTerminal("variableWrite").addSemanticAction((p, r) -> {
			if (r.size() == 1 && r.get(0).getAstNode().isPresent()
					&& r.get(0).getAstNode().get() instanceof VariableReadNode) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				VariableWriteNode v = new VariableWriteNode(
						(String) r.get(0).getSemanticAttribute("lexicalValue").get());
				p.setAstNode(v);
			}
		});

		getNonTerminal("assignmentOperator").addSemanticAction((p, r) -> {
			p.setSemanticAttribute("lexicalValue", r.get(0).getSemanticAttribute("lexicalValue").get());

		});

		getNonTerminal("creationType").addSemanticAction((p, r) -> {

			if (r.size() == 1) {

				Optional<IdentifierNode> id = r.get(0).getAstNode(IdentifierNode.class);
				CreationTypeNode ct;
				if (id.isPresent()) {
					ct = new CreationTypeNode(id.get().getName());
				} else  {


					if (r.get(0).getAstNode(CreationTypeNode.class).isPresent()) {
						ct = r.get(0).getAstNode(CreationTypeNode.class).get();
					} else {
						ct = new CreationTypeNode(r.get(0).getSemanticAttribute("lexicalValue").get().toString());
					}
				}

				p.setAstNode(ct);
			} else {


				CreationTypeNode ct = new CreationTypeNode(r.get(0).getSemanticAttribute("lexicalValue").get().toString());

				ct.setParameters(r.get(2).getAstNode(TypeParametersListNode.class).get());

				p.setAstNode(ct);
			}
		});

		getNonTerminal("classInstanceCreationExpression").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				NewInstanceCreationNode node = new NewInstanceCreationNode();

				CreationTypeNode t = r.get(1).getAstNode(CreationTypeNode.class).get();

				node.setCreationParameters(t);

				node.setTypeNode(ensureTypeNode(t));

				if (r.size() == 5) {
					AstNode n = r.get(3).getAstNode().get();
					if (n instanceof ArgumentListNode) {
						node.setArguments((ArgumentListNode) n);
					} else if (n instanceof ExpressionNode) {
						ArgumentListNode args = new ArgumentListNode();
						args.add(n);
						node.setArguments(args);
					}
				} else if (r.size() == 6) {
					node.setConstructorName((String) r.get(3).getLexicalValue());
				} else if (r.size() == 7) {
					node.setConstructorName((String) r.get(3).getLexicalValue());
					AstNode n = r.get(5).getAstNode().get();
					if (n instanceof ArgumentListNode) {
						node.setArguments((ArgumentListNode) n);
					} else if (n instanceof ExpressionNode) {
						ArgumentListNode args = new ArgumentListNode();
						args.add(n);
						node.setArguments(args);
					}
				}

				p.setAstNode(node);
			}

		});

		getNonTerminal("argumentList").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				if (r.get(0).getAstNode().get() instanceof ArgumentListNode) {
					p.setAstNode(r.get(0).getAstNode().get());
				} else {
					ArgumentListNode node = new ArgumentListNode();
					node.add(r.get(0).getAstNode().get());
					p.setAstNode(node);
				}

			} else if (r.get(0).getAstNode().get() instanceof ArgumentListNode) {
				ArgumentListNode node = r.get(0).getAstNode(ArgumentListNode.class).get();
				node.add(r.get(2).getAstNode().get());
				p.setAstNode(node);
			} else {
				ArgumentListNode node = new ArgumentListNode();
				node.add(r.get(0).getAstNode().get());
				node.add(r.get(2).getAstNode().get());
				p.setAstNode(node);
			}

		});

		getNonTerminal("argument").addSemanticAction((p, r) -> {
			if (r.size() == 1 && (r.get(0).getAstNode().get() instanceof ArgumentListItemNode)) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else if (r.size() == 1) {
				ArgumentListItemNode arg = new ArgumentListItemNode(-1, r.get(0).getAstNode().get());
				p.setAstNode(arg);
			} else {
				ArgumentListItemNode arg = new ArgumentListItemNode(-1, r.get(2).getAstNode().get());
				
				arg.setName(r.get(0).getLexicalValue());
				
				p.setAstNode(arg);
			}
		});
		
		
		getNonTerminal("methodInvocation").addSemanticAction((p, r) -> {
			if (r.size() == 1 && (r.get(0).getAstNode().get() instanceof MethodInvocationNode)) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				MethodInvocationNode node = new MethodInvocationNode();
				if (r.size() == 1) {
					node.setCall(r.get(0).getAstNode(MethodCallNode.class).get());
					// implicit self access
				} else {

					if (r.get(1).getLexicalValue().equals(".")){
						// with primary
						node.setAccess(r.get(0).getAstNode().get());
						if (r.get(4).getAstNode(ArgumentListNode.class).isPresent()){
							node.setCall(new MethodCallNode(r.get(2).getLexicalValue(), r.get(4).getAstNode(ArgumentListNode.class).get()));
						} else {
							node.setCall(new MethodCallNode(r.get(2).getLexicalValue(),new ArgumentListNode()));
						}

					} else {
						// implicit this

						if (r.get(2).getAstNode().isPresent()){
							node.setCall(new MethodCallNode(r.get(0).getLexicalValue(), r.get(2).getAstNode(ArgumentListNode.class).get()));
						} else {
							node.setCall(new MethodCallNode(r.get(0).getLexicalValue(),new ArgumentListNode()));
						}

					}

					// TODO different r.size 4 name ( args ) , 6= primary . name
					// (args)
					//
					//					int index = 0;
					//					if (r.size() == 6) {
					//						node.setAccess(r.get(index).getAstNode().get());
					//						index = 2;
					//					}
					//
					//					MethodCallNode call = null;
					//
					//					Optional<QualifiedNameNode> qname = r.get(index).getAstNode(QualifiedNameNode.class);
					//
					//					if (qname.isPresent()) {
					//						QualifiedNameNode name = r.get(index).getAstNode(QualifiedNameNode.class).get();
					//
					//						if (name.getName().contains(".")) {
					//
					//							int pos = name.getName().lastIndexOf('.');
					//							call = new MethodCallNode(name.getName().substring(pos + 1));
					//
					//							node.setAccess(new QualifiedNameNode(name.getName().substring(0, pos)));
					//						} else {
					//							call = new MethodCallNode(name.getName());
					//						}
					//					} else {
					//						if (".".equals(r.get(1).getLexicalValue())) {
					//							call = new MethodCallNode(r.get(2).getLexicalValue());
					//						} else {
					//							call = new MethodCallNode(r.get(index).getLexicalValue());
					//						}
					//
					//					}
					//
					//					index += 2;
					//
					//					if (r.get(index).getAstNode().isPresent()
					//							&& r.get(index).getAstNode().get() instanceof ArgumentListNode) {
					//						call.setArgumentListNode(r.get(index).getAstNode(ArgumentListNode.class).get());
					//					} else {
					//						ArgumentListNode list = new ArgumentListNode();
					//
					//						if (r.get(index).getAstNode().isPresent()) {
					//							list.add(r.get(index).getAstNode().get());
					//						}
					//						call.setArgumentListNode(list);
					//					}
					//
					//					node.setCall(call);
				}
				p.setAstNode(node);
			}

		});

		// getNonTerminal("maybeArgumentList").addSemanticAction( (p, r) -> {
		// if (r.size() == 1 && (r.get(0).getAstNode().get() instanceof
		// ArgumentListNode)){
		// p.setAstNode(r.get(0).getAstNode().get());
		// }
		// });

		getNonTerminal("fieldAccess").addSemanticAction((p, r) -> {
			if (r.size() == 1) {
				if (r.get(0).getAstNode().isPresent()){
					p.setAstNode(r.get(0).getAstNode().get());
				} else {
					FieldOrPropertyAccessNode node = new FieldOrPropertyAccessNode(
							(String) r.get(0).getSemanticAttribute("lexicalValue").get());
					p.setAstNode(node);
				}

			} else {
				FieldOrPropertyAccessNode node = new FieldOrPropertyAccessNode(
						(String) r.get(2).getSemanticAttribute("lexicalValue").get());

				AstNode primary;
				if (r.get(0).getSemanticAttribute("lexicalValue").map( s -> "this".equals(s)).orElse(false)) {
					primary = new VariableReadNode("this");
				} else if (r.get(0).getSemanticAttribute("lexicalValue").map( s -> "super".equals(s)).orElse(false)) {
					primary = new VariableReadNode("super");
				} else {
					primary = r.get(0).getAstNode().get();
				}


				node.setPrimary(primary);

				p.setAstNode(node);
			}
		});

		getNonTerminal("arrayAccess").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				IndexedPropertyReadNode node = new IndexedPropertyReadNode();

				node.setAccess(ensureExpression(r.get(0).getAstNode().get()));
				node.setArguments(r.get(2).getAstNode(ArgumentListNode.class).get());

				p.setAstNode(node);
			}

		});

		getNonTerminal("primary").addSemanticAction((p, r) -> {
			if (r.size() == 3) {
				if (r.get(0).getLexicalValue().equals("(") && r.get(2).getLexicalValue().equals(")")) {
					p.setAstNode(r.get(1).getAstNode().get());
				} else {
					p.setAstNode(r.get(0).getAstNode().get());
				}
			} else if (r.size() == 4) {
				var typeNode = new TypeNode(r.get(2).getAstNode(IdentifierNode.class).get().getName());
				p.setAstNode(new TypeOfInvocation(typeNode));
			} else if (r.get(0).getAstNode().isPresent()) {
				p.setAstNode(r.get(0).getAstNode().get());
			} else {
				// 'this'
				if ("this".equals(r.get(0).getSemanticAttribute("lexicalValue").get())) {
					p.setAstNode(new VariableReadNode("this"));
				} else if ("super".equals(r.get(0).getSemanticAttribute("lexicalValue").get())) {
					p.setAstNode(new VariableReadNode("super"));
				} else {
					p.setAstNode(r.get(0).getAstNode().get());
				}
			}

		});

		getNonTerminal("literal").addSemanticAction((p, r) -> {
			p.setAstNode(r.get(0).getAstNode().get());
		});

		getNonTerminal("numberLiteral").addSemanticAction((p, r) -> {

			String number = (String) r.get(0).getSemanticAttribute("lexicalValue").get();

			NumericValue v = new NumericValue();
			if (!Character.isDigit(number.charAt(number.length() - 1))) {
				final BigDecimal n = parseNumber(number.substring(0, number.length() - 1));
				v.setValue(n, determineNumberType(number));
			} else {
				final BigDecimal n = parseNumber(number);
				v.setValue(n, determineNumberType(number));
			}

			p.setAstNode(v);

		});

		getNonTerminal("booleanLiteral").addSemanticAction((p, r) -> {

			BooleanValue n = new BooleanValue();

			n.setValue("true".equals(r.get(0).getLexicalValue()));

			p.setAstNode(n);

		});

		getNonTerminal("stringLiteral").addSemanticAction((p, r) -> {

			StringValue n = new StringValue();
			n.setValue(r.get(0).getLexicalValue());
			p.setAstNode(n);

		});

		getNonTerminal("nullLiteral").addSemanticAction((p, r) -> {

			throw new CompilationError("null is not allowed. Did you mean 'none' ?");

		});

		getNonTerminal("noneLiteral").addSemanticAction((p, r) -> {

			NoneValue n = new NoneValue();

			p.setAstNode(n);

		});

		getNonTerminal("superDeclaration").addSemanticAction((p, r) -> {
			if (r.get(1).getSemanticAttribute("lexicalValue").isPresent()) {

				Optional<TypeNode> type = r.get(1).getAstNode(TypeNode.class);
				if (type.isPresent()){
					p.setAstNode(type.get());
				} else {

					QualifiedNameNode n = new QualifiedNameNode();

					n.append((String) r.get(1).getSemanticAttribute("lexicalValue").get());

					p.setAstNode(n);
				}

			} else {
				p.setAstNode(r.get(1).getAstNode().get());
			}

		});

		getNonTerminal("propertyDeclaration").addSemanticAction((p, r) -> {
			p.setAstNode(r.get(0).getAstNode().get());
		});

		getNonTerminal("typedPropertyDeclaration").addSemanticAction((p, r) -> {

			PropertyDeclarationNode prp = parsePropertyDeclaration(r);

			p.setAstNode(prp);
		});

		getNonTerminal("nonTypedPropertyDeclaration").addSemanticAction((p, r) -> {

			PropertyDeclarationNode prp = parsePropertyDeclaration(r);

			p.setAstNode(prp);
		});

		getNonTerminal("abstractPropertyDeclaration").addSemanticAction((p, r) -> {

			PropertyDeclarationNode prp = parsePropertyDeclaration(r);

			p.setAstNode(prp);
		});
		getNonTerminal("propertyDeclarationHead").addSemanticAction((p, r) -> {

			if (r.size() == 3) {
				p.setAstNode(r.get(1).getAstNode().get());
			} else {
				PropertyDeclarationNode prp = new PropertyDeclarationNode();
				prp.setAcessor(new AccessorNode(true,false));
				prp.setModifier(new ModifierNode(true, false));
				p.setAstNode(prp);
			}
		});

		getNonTerminal("propertyDeclarationInit").addSemanticAction((p, r) -> {

			if (r.size() == 1 ) {

				if (";".equals(r.get(0).getLexicalValue())) {
					PropertyDeclarationNode prp = new PropertyDeclarationNode();
					prp.setAcessor(new AccessorNode(true, false));
					prp.setModifier(new ModifierNode(true, false));

					p.setAstNode(prp);
				} else {
					PropertyDeclarationNode prp = r.get(0).getAstNode(PropertyDeclarationNode.class).get();

					p.setAstNode(prp);
				}

			} else {
				if ("=".equals(r.get(0).getLexicalValue())) {
					PropertyDeclarationNode prp;
					if (r.size() == 3) {
						prp = new PropertyDeclarationNode();
						prp.setAcessor(new AccessorNode(true, false));
						prp.setModifier(new ModifierNode(true, false));

					} else {
						prp = r.get(2).getAstNode(PropertyDeclarationNode.class).get();


					}

					Optional<ExpressionNode> exp = r.get(1).getAstNode(ExpressionNode.class);

					if (exp.isPresent()) {

						prp.setInitializer(exp.get());
					}

					p.setAstNode(prp);
				} else if ("=".equals(r.get(1).getLexicalValue())) {
					
					PropertyDeclarationNode prp =  r.get(0).getAstNode(PropertyDeclarationNode.class).get();

					Optional<ExpressionNode> exp = r.get(2).getAstNode(ExpressionNode.class);

					if (exp.isPresent()) {

						prp.setInitializer(exp.get());
					}

					p.setAstNode(prp);
					
				} else {
					PropertyDeclarationNode prp = new PropertyDeclarationNode();


					ExpressionNode exp = ensureExpression(r.get(1).getAstNode().get());

					ReturnNode rn = new ReturnNode();

					rn.setValue(exp);

					BlockNode b = new BlockNode();

					b.add(rn);

					AccessorNode a = new AccessorNode(false, true);

					a.setStatements(b);

					prp.setAcessor(a);

					p.setAstNode(prp);
				}

			}


		});

		getNonTerminal("propertyMembers").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				if (r.get(0).getAstNode(PropertyDeclarationNode.class).isPresent()) {
					p.setAstNode(r.get(0).getAstNode().get());
				} else if (r.get(0).getAstNode(ModifierNode.class).isPresent()) {
					PropertyDeclarationNode prp = new PropertyDeclarationNode();
					prp.setModifier(r.get(0).getAstNode(ModifierNode.class).get());
					p.setAstNode(prp);
				} else if (r.get(0).getAstNode(AccessorNode.class).isPresent()) {
					PropertyDeclarationNode prp = new PropertyDeclarationNode();
					prp.setAcessor(r.get(0).getAstNode(AccessorNode.class).get());
					p.setAstNode(prp);
				}
			} else {

				PropertyDeclarationNode prp = r.get(0).getAstNode(PropertyDeclarationNode.class).get();

				if (r.get(1).getAstNode(ModifierNode.class).isPresent()) {
					prp.setModifier(r.get(1).getAstNode(ModifierNode.class).get());
					p.setAstNode(prp);
				} else if (r.get(1).getAstNode(AccessorNode.class).isPresent()) {
					prp.setAcessor(r.get(1).getAstNode(AccessorNode.class).get());
					p.setAstNode(prp);
				}
				p.setAstNode(prp);

			}
		});

		getNonTerminal("propertyMember").addSemanticAction((p, r) -> {
			int baseIndex = 0;
			String name = r.get(baseIndex).getLexicalValue();

			Visibility visibility = null;
			if (name == null) {
				visibility = r.get(0).getAstNode(VisibilityNode.class).get().getVisibility();

				name = r.get(1).getLexicalValue();
				baseIndex = 1;
			}

			if (name.equals("set")) {
				ModifierNode a = new ModifierNode(r.size() <= 2, true);
				a.setVisibility(visibility);

				if (r.size() > baseIndex + 2) {
					a.setValueVariableName(r.get(baseIndex + 2).getLexicalValue());
					a.setStatements(r.get(baseIndex + 5).getAstNode(BlockNode.class).get());
				}
				p.setAstNode(a);
			} else if (name.equals("get")) {
				AccessorNode a = new AccessorNode(r.size() <= 2, true);
				a.setVisibility(visibility);

				if (r.size() > baseIndex + 2) {
					a.setStatements(r.get(baseIndex + 2).getAstNode(BlockNode.class).get());
				}

				p.setAstNode(a);
			}
		});

		getNonTerminal("indexerDeclaration").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				if (r.get(0).getAstNode(PropertyDeclarationNode.class).isPresent()) {
					p.setAstNode(r.get(0).getAstNode().get());
				} else if (r.get(0).getAstNode(ModifierNode.class).isPresent()) {
					PropertyDeclarationNode prp = new PropertyDeclarationNode();
					prp.setModifier(r.get(0).getAstNode(ModifierNode.class).get());
					p.setAstNode(prp);
				} else if (r.get(0).getAstNode(AccessorNode.class).isPresent()) {
					PropertyDeclarationNode prp = new PropertyDeclarationNode();
					prp.setAcessor(r.get(0).getAstNode(AccessorNode.class).get());
					p.setAstNode(prp);
				}
			} else {

				IndexerPropertyDeclarationNode prp = parseIndexer(r);

				p.setAstNode(prp);

			}
		});

		getNonTerminal("abstractIndexerDeclaration").addSemanticAction((p, r) -> {

			IndexerPropertyDeclarationNode prp = parseIndexer(r);

			p.setAstNode(prp);

		});

		getNonTerminal("abstractPropertyHead").addSemanticAction((p, r) -> {

			if (r.size() == 3) {
				p.setAstNode(r.get(1).getAstNode().get());
			} else {
				PropertyDeclarationNode prp = new PropertyDeclarationNode();
				prp.setAcessor(new AccessorNode(true, false));
				prp.setModifier(new ModifierNode(true, false));
				p.setAstNode(prp);
			}

		});

		getNonTerminal("abstractPropertyMembers").addSemanticAction((p, r) -> {

			if (r.size() == 1) {
				if (r.get(0).getAstNode(PropertyDeclarationNode.class).isPresent()) {
					p.setAstNode(r.get(0).getAstNode().get());
				} else if (r.get(0).getAstNode(ModifierNode.class).isPresent()) {
					PropertyDeclarationNode prp = new PropertyDeclarationNode();
					prp.setModifier(r.get(0).getAstNode(ModifierNode.class).get());
					p.setAstNode(prp);
				} else if (r.get(0).getAstNode(AccessorNode.class).isPresent()) {
					PropertyDeclarationNode prp = new PropertyDeclarationNode();
					prp.setAcessor(r.get(0).getAstNode(AccessorNode.class).get());
					p.setAstNode(prp);
				}
			} else {

				PropertyDeclarationNode prp = r.get(0).getAstNode(PropertyDeclarationNode.class).get();

				if (r.get(1).getAstNode(ModifierNode.class).isPresent()) {
					prp.setModifier(r.get(1).getAstNode(ModifierNode.class).get());
					p.setAstNode(prp);
				} else if (r.get(1).getAstNode(AccessorNode.class).isPresent()) {
					prp.setAcessor(r.get(1).getAstNode(AccessorNode.class).get());
					p.setAstNode(prp);
				}
				p.setAstNode(prp);

			}
		});

		getNonTerminal("abstractPropertyMember").addSemanticAction((p, r) -> {

			if (r.get(0).getLexicalValue().equals("set")) {
				p.setAstNode(new ModifierNode(true, true));
			} else if (r.get(0).getLexicalValue().equals("get")) {
				p.setAstNode(new AccessorNode(true, true));
			}

		});


	}

	private <A extends AstNode> Optional<A> scan(List<Symbol> r, int nextNodeIndex,Class<A> nodeType) {

		ListIterator<Symbol> listIterator = r.listIterator(nextNodeIndex);

		while (listIterator.hasNext()) {
			Optional<A> it = listIterator.next().getAstNode(nodeType);
			if (it.isPresent()) {
				return it;
			}
		}

		return Optional.empty();


	}

	private void applyImplementationModifiers(InvocableDeclarionNode n, Modifiers modifiers) {

		n.setVisibility(modifiers.getVisibility().getVisibility(Visibility.Undefined));

		ImplementationModifierNode implementationModifier = modifiers.getImplementationModifier();

		n.setAbstract(implementationModifier.isAbstract());
		n.setNative(implementationModifier.isNative());
		n.setOverride((implementationModifier.isOverride()));
		n.setDefault((implementationModifier.isDefault()));
		n.setImutability(modifiers.getImutability());
		n.setSatisfy(implementationModifier.isSatisfy());
	}

	private PropertyDeclarationNode parsePropertyDeclaration(List<Symbol> r) {

		Modifiers modifiers = new Modifiers();

		int nextNodeIndex = readModifiers(r, modifiers, PropertyDeclarationNode.class);

		PropertyDeclarationNode prp = r.get(nextNodeIndex).getAstNode(PropertyDeclarationNode.class).get();

		nextNodeIndex -= 2;

		TypeNode typeNode = null;
		if (":".equals(r.get(nextNodeIndex).getLexicalValue())) {
			typeNode = ensureTypeNode(r.get(nextNodeIndex + 1).getAstNode().get());
			nextNodeIndex--;
		}
		
		String identifier = r.get(nextNodeIndex).getLexicalValue();

		if (modifiers.getAnnotations() != null) {
			prp.setAnnotations(modifiers.getAnnotations());
		}

		prp.setName(identifier);
		prp.setType(typeNode);

		applyImplementationModifiers(prp, modifiers);

		if ( prp.getImutability() == Imutability.Imutable) {

			// remove modifier;

			ModifierNode m = prp.getModifier();

			if (m != null) {
				if (!m.isDeclared()) {
					prp.remove(m);
					prp.setModifier(null);
				} else {
					throw new CompilationError(prp, "Cannot declare a modifier for a imutable property");
				}
			}
		} else {

			// add modifier

			ModifierNode m = prp.getModifier();

			if (m == null && prp.getAcessor()!= null && !prp.getAcessor().isDeclared()) {
				prp.setModifier(new ModifierNode(true, false));	
			}
		}


		Optional<ExpressionNode> exp = r.get(r.size() - 2).getAstNode(ExpressionNode.class);

		if (exp.isPresent()) {

			prp.setInitializer(exp.get());
		}
		return prp;
	}

	private IndexerPropertyDeclarationNode parseIndexer(List<Symbol> r) {

		Modifiers modifiers = new Modifiers();

		int nextNodeIndex = readModifiers(r, modifiers, ParametersListNode.class);

		ParametersListNode indexes = r.get(nextNodeIndex).getAstNode(ParametersListNode.class).get();

		nextNodeIndex+=2;

		TypeNode typeNode = null;
		if (":".equals(r.get(nextNodeIndex++).getLexicalValue())) {
			typeNode = ensureTypeNode(r.get(nextNodeIndex++).getAstNode().get());
		} 

		IndexerPropertyDeclarationNode prp = new IndexerPropertyDeclarationNode(r.get(nextNodeIndex).getAstNode(PropertyDeclarationNode.class).get());

		if (modifiers.getAnnotations() != null) {
			prp.setAnnotations(modifiers.getAnnotations());
		}

		prp.setAbstract(modifiers.getImplementationModifier().isNative());
		prp.setNative(modifiers.getImplementationModifier().isNative());
		prp.setVisibility(modifiers.getVisibility().getVisibility(Visibility.Private));

		prp.setParameters(indexes);
		prp.setType(typeNode);
		return prp;
	}


	private BigDecimal parseNumber(String number) {
		number = number.replaceAll("_", "");
		if (number.startsWith("#")) {
			// hexadecimal
			return new BigDecimal(new BigInteger(number.substring(1), 16));
		} else if (number.startsWith("$")) {
			// binary
			return new BigDecimal(new BigInteger(number.substring(1), 2));
		}
		return new BigDecimal(number);
	}

	/**
	 * @param astNode
	 * @return
	 */
	private ExpressionNode ensureExpression(AstNode node) {
		if (node instanceof IdentifierNode) {
			if (((IdentifierNode) node).getName() == null) {
				return null;
			}
			return new FieldOrPropertyAccessNode(((IdentifierNode) node).getName());
		} else if (node instanceof QualifiedNameNode) {
			QualifiedNameNode q = (QualifiedNameNode) node;
			if (q.isComposed()) {
				FieldOrPropertyAccessNode f = new FieldOrPropertyAccessNode(q.getLast().getName());
				f.setPrimary(q.getPrevious());
				return f;
			} else {
				return new VariableReadNode(q.getName());
			}

		} else {
			return (ExpressionNode) node;
		}
	}

	/**
	 * @param object
	 * @return
	 */
	private QualifiedNameNode ensureQualifiedName(AstNode node) {
		if (node instanceof QualifiedNameNode) {
			return (QualifiedNameNode) node;
		} else if (node instanceof IdentifierNode) {
			QualifiedNameNode q = new QualifiedNameNode();
			q.append(((IdentifierNode) node).getName());
			return q;
		} else {
			throw new RuntimeException();
		}
	}

	private TypeNode ensureTypeNode(AstNode t) {
		if (t instanceof TypeNode) {
			return (TypeNode) t;
		} else if (t instanceof IdentifierNode){
			return new TypeNode(((IdentifierNode) t).getName());
		} else if (t instanceof CreationTypeNode) {
			TypeNode tn =  new TypeNode(((CreationTypeNode) t).getName());

			for(AstNode c : ((CreationTypeNode) t).getTypeParametersListNode().getChildren()) {
				tn.add(c);
			}

			return tn;
		} else {
			return new TypeNode((QualifiedNameNode) t);
		}
	}

	/**
	 * @param semanticAttribute
	 * @return
	 */
	private AssignmentNode.Operation resolveAssignmentOperation(Symbol symbol) {

		String op = (String) ((Symbol) symbol.getParserTreeNode().getChildren().get(0))
				.getSemanticAttribute("lexicalValue").get();

		switch (op) {
		case "=":
			return AssignmentNode.Operation.SimpleAssign;
		case "*=":
			return AssignmentNode.Operation.MultiplyAndAssign;
		case "/=":
			return AssignmentNode.Operation.DivideAndAssign;
		case "%=":
			return AssignmentNode.Operation.RemainderAndAssign;
		case "+=":
			return AssignmentNode.Operation.AddAndAssign;
		case "-=":
			return AssignmentNode.Operation.SubtractAndAssign;
		case "<<=":
			return AssignmentNode.Operation.LeftShiftAndAssign;
		case ">>=":
			return AssignmentNode.Operation.RightShiftAndAssign;
		case ">>>=":
			return AssignmentNode.Operation.PositiveRightShiftAndAssign;
		case "&=":
			return AssignmentNode.Operation.BitAndAndAssign;
		case "^=":
			return AssignmentNode.Operation.BitXorAndAssign;
		case "|=":
			return AssignmentNode.Operation.BitOrAndAssign;
		default:
			throw new CompilationError(op + " is not a recognized assigment operator");
		}
	}

	/**
	 * @param string
	 * @return
	 */
	private BooleanOperation resolveBooleanOperation(Symbol symbol) {

		String op;
		if (symbol.getSemanticAttribute("lexicalValue").isPresent()) {
			op = (String) symbol.getSemanticAttribute("lexicalValue").get();
		} else {
			op = (String) ((Symbol) symbol.getParserTreeNode().getChildren().get(0))
					.getSemanticAttribute("lexicalValue").get();
		}

		switch (op) {
		case "&":
			return BooleanOperation.BitAnd;
		case "&&":
			return BooleanOperation.LogicShortAnd;
		case "|":
			return BooleanOperation.BitOr;
		case "||":
			return BooleanOperation.LogicShortOr;
		case "^":
			return BooleanOperation.BitXor;
		case "~":
			return BooleanOperation.BitNegate;
		case "!":
			return BooleanOperation.LogicNegate;
		default:
			throw new CompilationError(op + " is not a recognized boolean operator");
		}
	}

	/**
	 * @param string
	 * @return
	 */
	private ComparisonNode.Operation resolveComparisonOperation(Symbol symbol) {

		String op = symbol.getLexicalValue();

		if (op == null){
			op = (String) ((Symbol) symbol.getParserTreeNode().getChildren().get(0))
					.getSemanticAttribute("lexicalValue").get();
		}

		for (ComparisonNode.Operation val : ComparisonNode.Operation.values()){
			if (val.symbol().equals(op)){
				return val;
			}
		}

		throw new CompilationError(op + "is not a recognized comparison operator");

	}

	public static ArithmeticOperation resolveOperation(Symbol symbol) {
		String op;
		if (symbol.getSemanticAttribute("lexicalValue").isPresent()) {
			op = (String) symbol.getSemanticAttribute("lexicalValue").get();
		} else {
			op = (String) ((Symbol) symbol.getParserTreeNode().getChildren().get(0))
					.getSemanticAttribute("lexicalValue").get();
		}

		for (ArithmeticOperation val : ArithmeticOperation.values()){
			if (val.symbol().equals(op)){
				return val;
			}
		}

		throw new CompilationError(op + " is not a recognized arithmetic operator");

	}

	public static UnitaryOperation resolveUnaryOperation(Symbol symbol) {
		String op;
		if (symbol.getSemanticAttribute("lexicalValue").isPresent()) {
			op = (String) symbol.getSemanticAttribute("lexicalValue").get();
		} else {
			op = (String) ((Symbol) symbol.getParserTreeNode().getChildren().get(0))
					.getSemanticAttribute("lexicalValue").get();
		}

		for (UnitaryOperation val : UnitaryOperation.values()){
			if (val.getArithmeticOperation().symbol().equals(op)){
				return val;
			}
		}

		throw new CompilationError(op + " is not a recognized arithmetic operator");

	}

	private <T> int readModifiers(List<Symbol> r, Modifiers modifiers, Class<T> type) {
		int next = 0;
		T declarator = null;
		while (declarator == null && next < r.size()) {
			Optional<AstNode> candidate = r.get(next).getAstNode();
			if (candidate.isPresent()) {

				if (candidate.get() instanceof AnnotationListNode) {
					modifiers.setAnnotations((AnnotationListNode) candidate.get());
				} else if (candidate.get() instanceof VisibilityNode) {
					modifiers.setVisibility((VisibilityNode) candidate.get());
				} else if (candidate.get() instanceof ImplementationModifierNode m) {
					;
					modifiers.setImplementationModifier(m.merge(modifiers.getImplementationModifier()));
					
				} else if (candidate.get() instanceof ImutabilityNode) {
					modifiers.setImutability((ImutabilityNode) candidate.get());
				} else if (type.isInstance(candidate.get())) {
					declarator = (T) candidate.get();
					return next;
				}
			}
			next++;

		}
		return -1;
	}

	private int findIndexOf(List<Symbol> symbols , Predicate<Symbol> predicate){
		int index = -1;
		for (Symbol s : symbols){
			index++;

			if (predicate.test(s)){
				return index;
			}
		}
		return index;
	}


}
