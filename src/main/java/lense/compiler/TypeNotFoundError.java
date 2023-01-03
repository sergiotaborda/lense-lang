package lense.compiler;

import compiler.lexer.ScanPosition;
import compiler.lexer.ScanPositionHolder;

public class TypeNotFoundError extends TypeRelatedCompilationError {

	private static final long serialVersionUID = -418924476441060426L;
	
	private final String name;

	public TypeNotFoundError(ScanPositionHolder holder, String name) {
		this(holder.getScanPosition(), name);
	}
	
	public TypeNotFoundError(ScanPosition position, String name) {
		super(position, "Type '"  + name  + "' is not recognized. Did you imported it?");
		var pos = name.indexOf("'");
		if (pos >=0) {
			name = name.substring(0, pos);
		}
		this.name = name;
	}
	

	
	public String getTypeName() {
		return name;
	}


}
