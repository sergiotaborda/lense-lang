package lense.core.lang;

import lense.core.collections.Assortment;
import lense.core.collections.Iterator;
import lense.core.collections.NativeProgression;
import lense.core.collections.Progression;
import lense.core.collections.Sequence;
import lense.core.lang.java.ConcatenatedString;
import lense.core.lang.java.JavaReifiedArguments;
import lense.core.lang.java.MethodSignature;
import lense.core.lang.java.NativeString;
import lense.core.lang.java.PlatformSpecific;
import lense.core.lang.java.Property;
import lense.core.lang.java.Signature;
import lense.core.lang.java.SubstringView;
import lense.core.lang.reflection.Type;
import lense.core.math.NativeNumberFactory;
import lense.core.math.Natural;
import lense.core.math.Natural64;

@Signature("::lense.core.collections.Sequence<lense.core.lang.Character>&lense.core.lang.Concatenable<lense.core.lang.String,lense.core.lang.String,lense.core.lang.String>")
public interface String extends Sequence , CharSequence, Concatenable {


	@Override
	@Property(name = "size")
	@MethodSignature(returnSignature = "lense.core.math.Natural", paramsSignature = "", declaringType = "lense.core.collections.Sequence")
	public Natural getSize();

	@Override
	public default Iterator getIterator() {
		return new Iterator() {
			Iterator range = getIndexes().getIterator();
			
			@Override
			public boolean moveNext() {
				return range.moveNext();
			}

			@Override
			public Any current() {
				return get((Natural)range.current());
			}
			
		};
	}
	@Override
	public lense.core.lang.Character get(Natural index);

	@Override
	public default Progression getIndexes(){
		return new NativeProgression(0, this.length() - 1);
	}
	
	@Override
	public default String asString() {
		return this;
	}

	@Override
	public boolean contains(Any other);
	
	@Override
	public default boolean containsAll(Assortment other) {
		if (other.getEmpty()) {
			return true; // empty set is contained in any set
		}
		var iterator = other.getIterator();
		while(iterator.moveNext()) {
			if (!contains(iterator.current())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean getEmpty();


	@Override
	public default boolean equalsTo(Any other) {
		if (this instanceof NativeString thisString && other instanceof NativeString nativeOther) {
			return thisString.equalsNative(nativeOther);
		} else if (other instanceof String that) {

			var thatSize = that.getSize();
			var thisSize = this.getSize();
			
			if (!thatSize.equals(thisSize)) {
				return false;
			} else if (thisSize.isZero()) {
				return true;
			}
	
			var a = this.getIterator();
			var b = that.getIterator();
			
			while(a.moveNext() && b.moveNext()) {
				if (!a.current().equals(b.current())) {
					return false;
				}
			}

		}
		return true;
	}
	

	@Override
	public default HashValue hashValue() {
		return new HashValue(this.toString().hashCode());
	}

	public String concat(String other);
	
	@PlatformSpecific
	public default String concat(java.lang.String other) {
		return concat(NativeString.valueOfNative(other));
	}
	
	public default String concat(Any other) {
		if (other == null){
			throw new IllegalArgumentException("argument cannot be null");
		}
		if (other.asString() == null){
			throw new IllegalArgumentException("asString cannot be null");
		}
		return concat(other.asString());
	}
   
	@Override
	public default Type type() {
		 return NativeString.TYPE_RESOLVER.resolveType();
	}
	

	@Override
	public default int length() {
		return NativeNumberFactory.toPrimitiveInt(getSize());
	}

	
	@Override
	public default char charAt(int index) {
		return get(NativeNumberFactory.newNatural(index)).toPrimitiveChar();
	}

	@Override
	public default CharSequence subSequence(int start, int end) {
		// TODO validate pre conditions
		return subString(NativeNumberFactory.newNatural(start), NativeNumberFactory.newNatural(end - start));
	}
	
	public default String removeAt(Natural position) {
		if (position.isZero() || position.compareWith(this.getSize()).isGreater()) {
			return this;
		}
		return ConcatenatedString.newInstance(
			new SubstringView(this, Natural64.ZERO, position.predecessor()),
			this.subString(position.successor())
		);
	}
	
	@MethodSignature(returnSignature = "lense.core.lang.Maybe<lense.core.math.Natural>" , paramsSignature = "lense.core.lang.String")
	public default Maybe indexOf(String candidate){
		if (this.isEmpty()) {
			if (candidate.isEmpty()) {
				return Some.constructor(JavaReifiedArguments.getInstance().addType(NativeNumberFactory.NATURAL_TYPE_RESOLVER), Natural64.ZERO);
			} else {
				return None.NONE;
			}
		}
		var fistChar = candidate.get(Natural64.ZERO);
		Natural pos = Natural64.ZERO;
		var iterator = this.getIterator();
		while(iterator.moveNext()) {
			if (iterator.current().equals(fistChar)) {
				if (new SubstringView(this,pos, candidate.getSize()).equalsTo(candidate)) {
					return Some.constructor(JavaReifiedArguments.getInstance().addType(NativeNumberFactory.NATURAL_TYPE_RESOLVER), pos);
				};
			}
			pos = pos.successor();
		}
		
		return None.NONE;
	}
	
	public default String subString(Natural start, Natural length) {
		return new SubstringView(this, start, length);
	}
	
	public default String subString(Natural start) {
		return new SubstringView(this, start, this.getSize().minus(start).abs());
	}

	public default boolean starstWith(String other) {
		return indexOf(other).valueEqualsTo(Natural64.ZERO);
	}
	
	public default boolean endsWith(String other ) {
		return indexOf(other).valueEqualsTo(this.getSize().minus(other.getSize()).abs());
	}
	
	@Override
	@lense.core.lang.java.MethodSignature( returnSignature = "lense.core.lang.String" , paramsSignature = "lense.core.lang.String,lense.core.lang.String" , override = true , satisfy = true, declaringType = "lense.core.lang.Concatenable")
	public default Any concatenate(Any a, Any b){
		return ((lense.core.lang.String)a).concat(((lense.core.lang.String)b));
	}
}
