package lense.core.lang.reflection;

import java.lang.reflect.InvocationTargetException;

import lense.core.collections.Sequence;
import lense.core.lang.Any;
import lense.core.lang.java.Base;
import lense.core.lang.java.Constructor;
import lense.core.math.Int32;
import lense.core.math.NativeNumberFactory;

public abstract class Type extends Base {

	public static Type forName(String name) {
		return newInstance(name, null);
	}

	public static Type forClass(Class<?> instanceClass) {
		return newInstance(instanceClass.getName(), instanceClass);
	}

	public static Type newInstance(String name, Class<?> instanceClass) {
		try {
			var typeClass = Class.forName(name + "$$Type", true, Thread.currentThread().getContextClassLoader());

			var typeInstance = (Type) typeClass.getConstructor().newInstance();
			
			if (instanceClass != null) {
				typeInstance.instanceReflectionClass = instanceClass;
			}

			return typeInstance;
			
		} catch (ClassNotFoundException e) {
			// native types do not have $$Type
			
			try {
				return new NativeType(Class.forName(name, true, Thread.currentThread().getContextClassLoader()));
			} catch (ClassNotFoundException ec) {
				throw new IllegalStateException(ec); // TODO create specific exception
			}
			
		} catch ( InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) { // TODO create specific exception
			throw new IllegalStateException(e);
		}
	}


	@Constructor(paramsSignature = "")
	public static Type constructor(){
		throw new IllegalArgumentException("Methods cannot be created directly");
	}

	private Type[] generics = null;
	protected Class<?> instanceReflectionClass;

	protected Type() {

	}
	
	protected Class<?> instanceClass(){
		if (this.instanceReflectionClass == null) {
			try {
				this.instanceReflectionClass = Class.forName(this.getName().toString());
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(e); // TODO create specific exception
			}
		}
		return this.instanceReflectionClass;
	}

	public abstract Type duplicate();

	@lense.core.lang.java.Property(name="name")
	public abstract lense.core.lang.String  getName();

	public boolean isInstance(Any any) {
		return this.instanceClass().isInstance(any);
	}

	@lense.core.lang.java.Property(name="methods")
	public  Sequence getMethods() {
		return loadMethods();
	}

	public Type genericType(Int32 index) {
		var i = NativeNumberFactory.toPrimitiveInt(index);
		if (this.generics != null && i >=0 && i < this.generics.length) {
			return this.generics[i];
		}
		throw new IndexOutOfBoundsException(i);
	}

	protected abstract Sequence loadMethods();

	@lense.core.lang.java.Property(name="properties")
	public  Sequence getProperties() {
		return loadProperties();
	}


	protected abstract Sequence loadProperties();

	public final Type withGenerics(Type ... types) {

		var t = this.duplicate();
		t.generics = types;

		return t;
	}


	public final Type getGenericTypeAt(int parameterIndex) {
		if (generics == null) {
			throw new ClassCastException("Type " + this.getName() + " has no generic parameters");
		}

		if (parameterIndex > generics.length - 1) {
			throw new ClassCastException("Type " + this.getName() + " has no generic parameters at index " + parameterIndex );
		}
		return generics[parameterIndex];
	}



}
