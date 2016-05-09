/**
 * 
 */
package lense.compiler.type;

import lense.compiler.type.variable.TypeVariable;

/**
 * 
 */
public interface MethodMember {

	public TypeVariable getType();
	public Method getDeclaringMethod();
	public void  setDeclaringMethod(Method method);
	public PositionalVariance getPositionVariance();
}
