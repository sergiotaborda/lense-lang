package lense.compiler.ir.tac;

import lense.compiler.type.TypeDefinition;

public interface Operand {

	boolean isTemporary();

	boolean isInstruction();

	TypeDefinition getOperandType();

}
