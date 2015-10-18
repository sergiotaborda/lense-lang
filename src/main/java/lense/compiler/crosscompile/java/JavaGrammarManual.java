/**
 * 
 */
package lense.compiler.crosscompile.java;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import compiler.Grammar;
import compiler.lexer.ScanPosition;
import compiler.lexer.Token;
import compiler.parser.Identifier;
import compiler.parser.NonTerminal;
import compiler.parser.Production;
import compiler.parser.ProductionItem;
import compiler.parser.Terminal;

/**
 * 
 */
public class JavaGrammarManual extends Grammar {

	private Map<String, Symbols> types = new HashMap<>();

	private Set<Character> terminals;
	
	public JavaGrammarManual (){

		NonTerminal compilationUnit  = NonTerminal.of("compilationUnit");
	
		NonTerminal packageDeclaration  = NonTerminal.of("package declaration");
		NonTerminal importDeclarations  = NonTerminal.of("import declarations");
		NonTerminal typeDeclarations  = NonTerminal.of("type declarations");
		
		compilationUnit.setRule(packageDeclaration.optional().add(importDeclarations.optional()).add(typeDeclarations.optional()));
		
		NonTerminal packageName  = NonTerminal.of("packageName");
		packageDeclaration.setRule(Terminal.of("package").add(packageName));
		packageName.setRule(Identifier.instance().or(packageName.add(Terminal.of(".")).add(Identifier.instance())));

		NonTerminal importDeclaration  = NonTerminal.of("import declaration");
		
		importDeclarations.setRule(importDeclaration.or(importDeclarations.add(importDeclaration)));
		
		NonTerminal singleTypeImportDeclaration  = NonTerminal.of("single type import declaration");
		NonTerminal typeImportOnDemandDeclaration  = NonTerminal.of("type import on demand declaration");
		importDeclaration.setRule(singleTypeImportDeclaration.or(typeImportOnDemandDeclaration) );	
		
		NonTerminal typeName  = NonTerminal.of("typeName");
		singleTypeImportDeclaration.setRule (Terminal.of("import").add(typeName));
		typeImportOnDemandDeclaration.setRule(Terminal.of("import").add(packageName).add(Terminal.of(".*")));

		typeName.setRule(Identifier.instance().or(packageName.add(Terminal.of(".")).add(Identifier.instance())));

		NonTerminal typeDeclaration  = NonTerminal.of("type declaration");
		typeDeclarations.setRule (typeDeclaration.or(typeDeclarations.add(typeDeclaration)));
		
		NonTerminal classDeclaration  = NonTerminal.of("class declaration");
		NonTerminal interfaceDeclaration  = NonTerminal.of("interface declaration");
		typeDeclaration.setRule(classDeclaration.or(interfaceDeclaration).or(Terminal.of(";")));
		
		NonTerminal classModifiers  = NonTerminal.of("class modifiers");
		NonTerminal superDecl  = NonTerminal.of("super");
		NonTerminal interfaces  = NonTerminal.of("interfaces");
		NonTerminal classBody  = NonTerminal.of("classBody");
		
		classDeclaration.setRule( classModifiers.optional().add(Terminal.of("class")).add(Identifier.instance()).add(superDecl.optional()).add(interfaces.optional()).add(classBody));

		NonTerminal classModifier  = NonTerminal.of("class modifier");
		classModifiers.setRule(classModifier.or(classModifiers.add(classModifier)));
		
		classModifier.setRule(Terminal.of("public").or(Terminal.of("abstract")).or(Terminal.of("final")));
		
		NonTerminal classType = NonTerminal.of("classType");
		superDecl.setRule (Terminal.of("extends").add(classType));
		classType.setRule(typeName);
		
		NonTerminal interfaceTypeList = NonTerminal.of("interfaceTypeList");
		
		interfaces.setRule(Terminal.of("implements").add(interfaceTypeList));
		
		NonTerminal interfaceType = NonTerminal.of("interfaceType");
		interfaceTypeList.setRule(interfaceType.or(interfaceTypeList.add(interfaceType)));
		
		interfaceType.setRule(typeName);
		
		NonTerminal classBodyDeclarations = NonTerminal.of("class body declarations");
		classBody.setRule(Terminal.of("{").add(classBodyDeclarations.optional()).add(Terminal.of("}")));

		NonTerminal classBodyDeclaration = NonTerminal.of("classBodyDeclaration");
		
		classBodyDeclarations.setRule(classBodyDeclaration.or(classBodyDeclarations.add(classBodyDeclaration)));
		
		NonTerminal classMemberDeclaration = NonTerminal.of("classMemberDeclaration");
		NonTerminal staticInitializer = NonTerminal.of("static initializer");
		NonTerminal constructoDeclaration = NonTerminal.of("constructor declaration");
		
		classBodyDeclaration.setRule(classMemberDeclaration.or(staticInitializer).or(constructoDeclaration));
		
//		<class body> ::= { <class body declarations>? }
//
//		<class body declarations> ::= <class body declaration> | <class body declarations> <class body declaration>
//
//		<class body declaration> ::= <class member declaration> | <static initializer> | <constructor declaration>
//
//		<class member declaration> ::= <field declaration> | <method declaration>
//
//		<static initializer> ::= static <block>
		
		types.put("/**", Symbols.StartMultilineComment);
		types.put("/*", Symbols.StartMultilineComment);
		types.put("*/", Symbols.EndMultilineComment);
		types.put("//", Symbols.LineMultilineComment);
		types.put("package", Symbols.Keywork);

		types.put("class", Symbols.Keywork);
		types.put("interface", Symbols.Keywork);	
		types.put("enum", Symbols.Keywork);

		types.put("static", Symbols.Keywork);
		types.put("abstract", Symbols.Keywork);
		types.put("final", Symbols.Keywork);

		types.put("implements", Symbols.Keywork);
		types.put("extends", Symbols.Keywork);

		types.put("instanceof", Symbols.Keywork);
		types.put("super", Symbols.Keywork);
		types.put("this", Symbols.Keywork);
		types.put("throw", Symbols.Keywork);
		types.put("throws", Symbols.Keywork);
		types.put("try", Symbols.Keywork);
		types.put("catch", Symbols.Keywork);
		types.put("finally", Symbols.Keywork);

		types.put("void", Symbols.Keywork);
		types.put("import", Symbols.Keywork);
		types.put("public", Symbols.Keywork);
		types.put("private", Symbols.Keywork);
		types.put("protected", Symbols.Keywork);
		types.put("strictfp", Symbols.Keywork);
		types.put("native", Symbols.Keywork);

		types.put("if", Symbols.Keywork);
		types.put("else", Symbols.Keywork);
		types.put("while", Symbols.Keywork);
		types.put("for", Symbols.Keywork);
		types.put("switch", Symbols.Keywork);
		types.put("case", Symbols.Keywork);
		types.put("default", Symbols.Keywork);
		types.put("continue", Symbols.Keywork);
		types.put("return", Symbols.Keywork);
		types.put("break", Symbols.Keywork);
		types.put("new", Symbols.Keywork);
		types.put("void", Symbols.Keywork);

		types.put("true", Symbols.Keywork);
		types.put("false", Symbols.Keywork);
		types.put("null", Symbols.Keywork);

		types.put("const", Symbols.Keywork);
		types.put("goto", Symbols.Keywork);

		types.put("volatile", Symbols.Keywork);
		types.put("transient", Symbols.Keywork);
		types.put("synchronized", Symbols.Keywork);
		types.put("assert", Symbols.Keywork);


		types.put("{", Symbols.StartStatementsGroup);
		types.put("}", Symbols.EndStatementsGroup);
		types.put("(", Symbols.StartParametersGroup);
		types.put(")", Symbols.EndParametersGroup);
		types.put("[", Symbols.StartIndexGroup);
		types.put("]", Symbols.EndIndexGroup);

		types.put("\"", Symbols.LiteralStringSurround);

		types.put("byte", Symbols.Type);
		types.put("short", Symbols.Type);
		types.put("int", Symbols.Type);
		types.put("long", Symbols.Type);
		types.put("char", Symbols.Type);
		types.put("double", Symbols.Type);
		types.put("float", Symbols.Type);
		types.put("boolean", Symbols.Type);

		types.put("?", Symbols.Operator);
		types.put("+", Symbols.Operator);
		types.put("++", Symbols.Operator);
		types.put("-", Symbols.Operator);
		types.put("--", Symbols.Operator);
		types.put("*", Symbols.Operator);
		types.put("=", Symbols.Operator);
		types.put("==", Symbols.Operator);
		types.put("!=", Symbols.Operator);
		types.put(">", Symbols.Operator);
		types.put("<", Symbols.Operator);
		types.put(".", Symbols.Operator);
		types.put("!", Symbols.Operator);
		types.put("%", Symbols.Operator);
		types.put("~", Symbols.Operator);
		types.put("&&", Symbols.Operator);
		types.put("&", Symbols.Operator);
		types.put("||", Symbols.Operator);
		types.put("|", Symbols.Operator);
		types.put("/", Symbols.Operator);
		types.put("->", Symbols.Operator);
		types.put("~=", Symbols.Operator);
		types.put(">=", Symbols.Operator);
		types.put("=<", Symbols.Operator);
		types.put("+=", Symbols.Operator);
		types.put("-=", Symbols.Operator);
		types.put("*=", Symbols.Operator);
		types.put("/=", Symbols.Operator);
		types.put("%=", Symbols.Operator);
		types.put("<>", Symbols.Operator);
		types.put(":", Symbols.Operator);
		types.put("::", Symbols.Operator);
		
		types.put(",", Symbols.ParameterSeparator);
		types.put(";", Symbols.StatementSeparator);
		
		terminals = types.entrySet().stream().filter(e -> e.getValue() == Symbols.Operator).map( e -> e.getKey().charAt(0)).collect(Collectors.toSet());
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStopCharacter(char c) {
		return isIgnore(c)  || terminals.contains(c) || c == '"' || c == '(' || c == ')' || c == '{' || c == '}' || c == '[' || c == ']' || c == ';'|| c == ',';
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isIgnore(char c) {
		return c == '\t' || c == '\r' ||  c== ' ' || c == '\n' ;
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
	public Optional<Token> maybeMatch(ScanPosition pos,String text) {

		if (text.trim().length() == 0){
			return Optional.empty();
		}

		if (text.matches("^\\d+$")){
			return Optional.of(new JavaToken(text, Symbols.StartNumberLiteral));
		}

		Symbols s = types.get(text);
		if (s == null || s == Symbols.Keywork){ // keywords connet be guessed
			s = Symbols.ID;
		} 
		return Optional.of(new JavaToken(text, s));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Token> terminalMatch(ScanPosition pos,String text) {
		if (text.trim().length() == 0){
			return Optional.empty();
		}
		if (text.length() > 1 && text.startsWith("\"") && text.endsWith("\"")){
			return  Optional.of(new JavaToken(text.substring(1,text.length()-1), Symbols.LiteralString));
		} else if (text.matches("^\\d+$")){
			return  Optional.of(new JavaToken(text, Symbols.LiteralWholeNumber));
		} else if (text.matches("[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?")){
			return  Optional.of(new JavaToken(text, Symbols.LiteralFloatPointNumber));
		} else if (text.equals("do")){
			return  Optional.of(new JavaToken("do", Symbols.Keywork));
		}
		Symbols s = types.get(text);
		if (s == null){
			s = Symbols.ID;
		} 
		return  Optional.of(new JavaToken(text, s));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Token> stringLiteralMath(ScanPosition pos,String text) {
		return Optional.of(new JavaToken(text, Symbols.LiteralString));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Production getStartProduction() {
		throw new UnsupportedOperationException("Not implememented yet");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProductionItem getFinalProductionItem(int targetId) {
		throw new UnsupportedOperationException("Not implememented yet");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getFinalProductionItemTargetId(ProductionItem item) {
		throw new UnsupportedOperationException("Not implememented yet");
	}







}
