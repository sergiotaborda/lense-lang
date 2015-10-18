/**
 * 
 */
package lense.compiler.crosscompile.java;

enum Symbols {
	ID,
	Keywork,
	StartMultilineComment,
	EndMultilineComment,
	LineMultilineComment, 
	StartStatementsGroup, 
	EndStatementsGroup, 
	StartParametersGroup, StartIndexGroup, EndIndexGroup, EndParametersGroup, 
	Type, 
	LiteralString, LiteralWholeNumber, LiteralFloatPointNumber, Operator, LiteralStringSurround, StartNumberLiteral, StatementSeparator, ParameterSeparator
}