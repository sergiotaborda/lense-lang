/**
 * 
 */
package lense.compiler.context;

import java.math.BigDecimal;
import java.util.Optional;

import compiler.syntax.AstNode;
import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public class VariableInfo {

	private TypeVariable type;
	private String name;
	private boolean initialized;
	private boolean isTypeVariable;
	private boolean escapes = false;
	private int writeCount = 0;
	private boolean imutable;
	private boolean predefined;
	private AstNode declaringNode;
    private BigDecimal min;
    private BigDecimal max;

	/**
	 * Constructor.
	 * @param name
	 * @param type
	 */
	public VariableInfo(String name, TypeVariable type, AstNode declaringNode, boolean isTypeVariable, boolean predefined) {
		this.name = name;
		this.type = type;
		this.isTypeVariable= isTypeVariable;
		this.imutable = false;
		this.predefined = predefined;
		this.declaringNode = declaringNode;
	}
	
	public AstNode getDeclaringNode(){
		return declaringNode;
	}

	public boolean isPredefined(){
		return predefined;
	}
	
	public void markEscapes(){
		escapes = true;
	}
	
	public boolean doesEscape(){
		return escapes;
	}
	
	public void markWrite(){
		writeCount++;
	}

	public boolean isEfectivlyFinal(){
		return writeCount < 2;
	}

	public TypeVariable getTypeVariable() {
		return type;
	}
	
	public void setTypeVariable(TypeVariable type) {
		this.type = type;
	}

	/**
	 * Obtains {@link String}.
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param b
	 */
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
	
	public boolean isInitialized(){
		return initialized;
	}


	/**
	 * @return
	 */
	public boolean isTypeVariable() {
		return isTypeVariable;
	}

	public boolean isImutable() {
		return imutable;
	}

	public void setImutable(boolean imutable) {
		this.imutable = imutable;
	}

    public void setMininumValue(Number min) {
        if (min instanceof BigDecimal){
            this.min = (BigDecimal)min;
        } else {
            this.min = new BigDecimal(min.toString());
        }
    }

    public void setMaximumValue(Number max) {
        if (max instanceof BigDecimal){
            this.max = (BigDecimal)max;
        } else {
            this.max = new BigDecimal(max.toString());
        }
    }

    public Optional<BigDecimal> getMinimum(){
        return Optional.ofNullable(this.min);
    }

    public Optional<BigDecimal> getMaximum(){
        return Optional.ofNullable(this.max);
    }
	
}
