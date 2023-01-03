package lense.compiler;

import compiler.lexer.ScanPosition;
import compiler.lexer.ScanPositionHolder;

public class TypeMembersNotLoadedError extends TypeRelatedCompilationError  {

	private static final long serialVersionUID = -7584061954974492253L;
	
	private final String typeName;

	public TypeMembersNotLoadedError(ScanPosition position, String typeName) {
		super(position, "Type '" + typeName + " was no members loaded.");
		this.typeName = typeName;
	}

	public TypeMembersNotLoadedError(ScanPositionHolder holder, String typeName) {
		this(holder.getScanPosition(), typeName);
	}
	
	public String getTypeName() {
		return typeName;
	}
}
