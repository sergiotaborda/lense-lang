package lense.compiler.type;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lense.compiler.context.SemanticContext;
import lense.compiler.type.variable.RecursiveTypeVariable;
import lense.compiler.type.variable.TypeVariable;
import lense.compiler.type.variable.UpdatableTypeVariable;
import lense.compiler.typesystem.LenseTypeSystem;
import lense.compiler.typesystem.TypeMatch;
import lense.compiler.typesystem.Visibility;

public class LenseTypeAssistant implements TypeAssistant {

	private final SemanticContext semanticContext;
	private final LenseTypeSystem typeSystem =  LenseTypeSystem.getInstance();
	
	public LenseTypeAssistant(SemanticContext semanticContext) {
		this.semanticContext = semanticContext;
	}
	
	private TypeDefinition ensureNotFundamental(TypeDefinition type) {
		if (semanticContext == null) {
			return type;
		}
		return semanticContext.ensureNotFundamental(type);
	}

	@Override
	public boolean isAny(TypeDefinition type) {
		return typeSystem.isAny(type);
	}

	@Override

	public TypeVariable unionOf(TypeVariable a, TypeVariable b) {
		if (a.isSingleType()) {
			a = a.getUpperBound();
		}
		if (b.isSingleType()) {
			b = b.getUpperBound();
		}

		if (a == b || a.equals(b)) {
			return a;
		} else if (a.getTypeDefinition().getName().equals("lense.core.lang.Nothing")) {
			return b;
		} else if (b.getTypeDefinition().getName().equals("lense.core.lang.Nothing")) {
			return a;
		} else if (a.getTypeDefinition().getName().equals("lense.core.lang.None")) {
			if (isMaybe(b)) {
				return b;
			} 
		
		} else if (b.getTypeDefinition().getName().equals("lense.core.lang.None")) {
			if (isMaybe(a)) {
				return a;
			}
		} else if (isAssignableTo(a, b).matches()) {
			return b;
		} else if (isAssignableTo(b, a).matches()) {
			return a;
		} 
			
		return new UnionType(a, b);
		
	}

	@Override
	public boolean isMaybe(TypeVariable type) {
		return typeSystem.isMaybe(type);
	}

	@Override
	public boolean isBoolean(TypeVariable type) {
		return typeSystem.isBoolean(type);
	}

	@Override
	public boolean isTuple(TypeVariable type, int tupleCount) {
		return typeSystem.isTuple(type,tupleCount);
	}
	
	@Override
	public boolean isNothing(TypeVariable type) {
		return typeSystem.isNothing(type);
	}
	
	@Override
	public boolean isNumber(TypeVariable type) {
		return isAssignableTo(type, LenseTypeSystem.Number()).matches();
	}
	
	@Override
	public boolean isNumber(TypeDefinition type) {
		return isNumber((TypeVariable)type) 
				|| (type.getName().startsWith("lense.core.math") 
					&& (type.getName().endsWith("Natural")
						|| type.getName().endsWith("Integer") 
						|| type.getName().endsWith("Real")
				)
		);
	}

	@Override
	public TypeMatch isAssignableTo(TypeVariable type, TypeVariable target) {
		if (type == target) {
			return TypeMatch.Exact;
		}
		
		if (type == null) {
			throw new IllegalArgumentException("Type cannot be null");
		}
		
		if (target == null) {
			throw new IllegalArgumentException("Target cannot be null");
		}
		
		if (type instanceof UnionType){
		    UnionType union = (UnionType)type;
		    
		    return isAssignableTo(union.getLeft(), target).and(isAssignableTo(union.getRight(), target));

		} else if (type.isSingleType()) {
			if (target.isSingleType()) {
				return isAssignableTo(type.getTypeDefinition(), target.getTypeDefinition());
			} else {
				TypeVariable interval = (TypeVariable) target;
				// interval contains type ?
				return isAssignableTo(type, interval.getUpperBound()).and(isAssignableTo(interval.getLowerBound(), type));
			}
		} else {
			return isAssignableTo(type.getLowerBound(), target.getLowerBound()).and(isAssignableTo(type.getUpperBound(), target.getUpperBound()));

		}
	}
	
	@Override
	public TypeMatch isAssignableTo(TypeDefinition type, TypeDefinition target) {

		if (type == null || target == null) {
			return TypeMatch.NoMatch;
		}

		if (type == target) {
			return TypeMatch.Exact;
		}
		

		if (this.isAny(target)) {
			// all types are assignable to Any
			return this.isAny(type)
					? TypeMatch.Exact
					: TypeMatch.UpCast; 
		}
		
		if (this.isNothing(type)) {
			return TypeMatch.UpCast; // nothing is assignable to all types
		}
		
		if (this.isNothing(target)) {
			return TypeMatch.NoMatch; // only nothing is assignable to nothing
		}
		
		if (this.isAny(type)) {
			// any is assignble to no one but it self
			return TypeMatch.NoMatch;
		}

		type = this.ensureNotFundamental(type);
		target = this.ensureNotFundamental(target);
		
		if (type.getName().equals(target.getName())) {
			
			if (type.getGenericParameters().size() != target.getGenericParameters().size()) {
				return TypeMatch.NoMatch;
			}
			
			TypeMatch assignable = TypeMatch.Exact;
			for (int i = 0; i < type.getGenericParameters().size(); i++) {
				var a = type.getGenericParameters().get(i);
				var b = target.getGenericParameters().get(i);
				
				if (a == b) {
					assignable = assignable.and(TypeMatch.Exact);
				} else if (b instanceof UpdatableTypeVariable u && u.original() instanceof RecursiveTypeVariable) {
					assignable = assignable.and(TypeMatch.Exact);
				} else {
					assignable = assignable.and(isAssignableTo(a, b));
				}
				
				if (!assignable.matches()) {
					break;
				}
			}
			
			if (assignable.matches()) {
				return assignable;
			}
		}


		if (!target.getKind().isTypeClass()) {
			// super type
			if (type.getSuperDefinition() != null) {
				if (!type.getSuperDefinition().getName().equals("lense.core.lang.Any") && type.isGeneric()) {
					TypeVariable[] types = new TypeVariable[type.getGenericParameters().size()];
					for (int i = 0; i < types.length; i++) {
						types[i] = type.getGenericParameters().get(i);
					}
					if( isAssignableTo(LenseTypeSystem.getInstance().specify(type.getSuperDefinition(), types), target).matches()) {
						return TypeMatch.UpCast;
					}
				} else {
					if( isAssignableTo(type.getSuperDefinition(), target).matches()) {
						return TypeMatch.UpCast;
					}
				}
			}

			
			// interface implementation
			for (TypeDefinition interfaceDefiniton : type.getInterfaces()) {
				TypeMatch match = isAssignableTo(interfaceDefiniton, target);
				if (match.matches()) {
					return TypeMatch.UpCast;
				}
			}
		} else {
			// types satisfied 
			for (TypeDefinition definiton : type.getImplementedTypeClasses()) {
				TypeMatch match = isAssignableTo(definiton, target);
				if (match.matches()) {
					return TypeMatch.UpCast;
				}
			}
		}
		
		
	

		return TypeMatch.NoMatch;

		// return type.equals(Nothing()) || ( type.getName() == target.getName()
		// && type.getGenericParameters().size() ==
		// target.getGenericParameters().size());
	}

	@Override
	public boolean isPromotableTo(TypeVariable a, TypeVariable b) {
		if (a == null || b == null) {
			return false;
		}

		if (a == b) {
			return true;
		} else if (isAssignableTo(a, b).matches()) {
			return true;
		} 

		if (b.isFixed()) {
			return getConstructorByImplicitAndPromotableParameters(b.getTypeDefinition(), true, new ConstructorParameter(a)).isPresent();
		}
		return false;
	}
	

	public boolean isSignaturePromotableTo(MethodSignature from, MethodSignature to) {
		if (from.getName().equals(to.getName()) && from.getParameters().size() == to.getParameters().size()) {

			for (int i = 0; i < from.getParameters().size(); i++) {
				if (!isPromotableTo(from.getParameters().get(i).getType(), to.getParameters().get(i).getType())) {
					return false;
				}
			}
			return true;
		}
		return false;
	}



	public <M extends CallableMember<M>> boolean isSignatureImplementedBy(CallableMemberSignature<M> signature,
			CallableMember<M> m) {
		final List<CallableMemberMember<M>> memberParameters = m.getParameters();
		final List<CallableMemberMember<M>> signatureParameters = signature.getParameters();

		return signature.getName().equals(m.getName())
				&& areSignatureParametersImplementedBy(signatureParameters, memberParameters).matches();

	}
	
	@Override
	public boolean isMethodImplementedBy(Method reference, Method candidate) {

		return reference.getName().equals(candidate.getName())
				&& areSignatureParametersImplementedBy(reference.getParameters(), candidate.getParameters()).matches();
	}

	public <M extends CallableMember<M>> TypeMatch areSignatureParametersImplementedBy(
			List<CallableMemberMember<M>> signatureParameters, List<CallableMemberMember<M>> memberParameters) {

		if (signatureParameters.size() == memberParameters.size()) {

			TypeMatch match = TypeMatch.Exact;
			for (int i = 0; i < signatureParameters.size(); i++) {
				match = match.and(isAssignableTo(signatureParameters.get(i).getType(), memberParameters.get(i).getType()));
				if (!match.matches()) {
					return TypeMatch.NoMatch;
				}
			}
			return match;
		}
		return TypeMatch.NoMatch;

	}

	public boolean isSignatureAssignableTo(MethodSignature from, MethodSignature to) {
		if (from.getName().equals(to.getName()) && from.getParameters().size() == to.getParameters().size()) {

			TypeMatch match = TypeMatch.Exact;
			
			for (int i = 0; i < from.getParameters().size(); i++) {
				match = match.and(isAssignableTo(from.getParameters().get(i).getType(), to.getParameters().get(i).getType()));
				if (!match.matches()) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public TypeDefinition specify(TypeVariable definition, TypeVariable... genericParametersCapture) {
		return typeSystem.specify(definition, genericParametersCapture);
	}

	@Override
	public TypeDefinition specify(TypeDefinition definition, List<TypeVariable> genericParameters) {
		return typeSystem.specify(definition, genericParameters);
	}

	@Override
	public TypeDefinition specify(TypeDefinition definition, TypeVariable... genericParameters) {
		return typeSystem.specify(definition, genericParameters);
	}
	

	public boolean areNomallyEquals(TypeDefinition a, TypeDefinition b) {
		return a == b || a.getName().equals(b.getName());
	}
	
	public boolean areNomallyEquals(TypeVariable a, TypeVariable b) {
		return  a == b || a.getTypeDefinition().getName().equals(b.getTypeDefinition().getName());
	}
	
	@Override
	public List<Match<Constructor>> getConstructorByName(TypeDefinition type, String constructorName , ConstructorParameter... parameters) {
        // find exact local

        List<CallableMemberMember<Constructor>> list = Arrays.asList(parameters);
        Stream<Constructor> map = type.getMembers().stream()
                .filter(m -> m.isConstructor())
                .map(m -> (Constructor) m);
        
    	if (constructorName != null) {
			map = map.filter(c -> constructorName.equals(c.getName()));
		} else {
			map = map.filter(c -> c.getName() == null || "constructor".equals(c.getName()));
		} 	
    	
    	return map.map(c -> Match.of(c , areSignatureParametersImplementedBy(list, c.getParameters()))  )
				.filter( c-> c.getMatch().matches())
				.sorted(Comparator.reverseOrder())
				.collect(Collectors.toList());
	}
	
    @Override
    public List<Match<Constructor>> getConstructorByParameters(TypeDefinition type,ConstructorParameter... parameters) {
       return getConstructorByParameters( type,Visibility.Undefined, parameters);
    }
    
    @Override
    public List<Match<Constructor>> getConstructorByParameters(TypeDefinition type,Visibility visibility , ConstructorParameter... parameters) {
        List<CallableMemberMember<Constructor>> list = Arrays.asList(parameters);
        Stream<Constructor> map = type.getMembers().stream()
                .filter(m -> m.isConstructor())
                .map(m -> (Constructor) m);
        
        if (visibility != Visibility.Undefined) {
        	map = map.filter(c -> c.getVisibility() == visibility);
        }
        
		return map.map(c -> Match.of(c , areSignatureParametersImplementedBy(list, c.getParameters()))  )
				.filter( c-> c.getMatch().matches())
				.sorted(Comparator.reverseOrder())
				.collect(Collectors.toList());
    }
	
	@Override
	public Optional<Constructor> getConstructorByNameAndPromotableParameters(TypeDefinition type,String name , ConstructorParameter... parameters) {
		return searchConstructorWithPromotableParameters(type,null,name, null, parameters);
	}
	
	@Override
    public Optional<Constructor> getConstructorByPromotableParameters(TypeDefinition type,ConstructorParameter... parameters) {
		return searchConstructorWithPromotableParameters(type,null,null, null, parameters);
    }

	@Override
	public Optional<Constructor> getConstructorByImplicitAndPromotableParameters(TypeDefinition type,boolean implicit, ConstructorParameter... parameters) {
		return searchConstructorWithPromotableParameters(type, null, null, implicit, parameters);
	}
	
	private Optional<Constructor> searchConstructorWithPromotableParameters(
			TypeDefinition type,
			Visibility visibility ,
			String name , 
			Boolean implicit,
			ConstructorParameter... parameters
	) {
		
		type = this.ensureNotFundamental(type);
		
		Stream<Constructor> map = type.getMembers().stream()
				.filter(m -> m.isConstructor())
				.map(m -> (Constructor)m)
				.filter(c -> c.getParameters().size() == parameters.length);
		
		
		if (implicit != null) {
			map = map.filter(c -> c.isImplicit() == implicit.booleanValue());
		}
		
		if (name != null) {
			map = map.filter(c -> name.equals(c.getName()));
		} 
//		else if (name == null) {
//			map = map.filter(c -> name == null);
//		}
	
		
		if (visibility != null && visibility != Visibility.Undefined) {
	        map = map.filter(c -> c.getVisibility() == visibility);
	    }
		
		var f = map.collect(Collectors.toList());
		 
		Iterator<Constructor> iterator = f.iterator();
		
		if ( parameters.length == 0) {
			if (iterator.hasNext()) {
				Optional.ofNullable(iterator.next());
			}
			return Optional.empty();
		}
		
		Constructor mostSpecific = null;
        while(iterator.hasNext()){
            Constructor constructor = iterator.next();
          
            for (int p = 0; p < parameters.length; p++) {
                ConstructorParameter mp = parameters[p];
                if (isPromotableTo(mp.getType(), constructor.getParameters().get(p).getType())) {
                	if (mostSpecific == null) {
                		mostSpecific = constructor;
                	} else {
                		mostSpecific = mostSpecific(mostSpecific, constructor);
                	}
                   
                }
            }
            
        }
        return Optional.ofNullable(mostSpecific);
	}
	
    private <C extends CallableMember<C>> C mostSpecific(C mostSpecific, C constructor) {
    	 
    	 for (int p = 0; p < constructor.getParameters().size(); p++) {
              CallableMemberMember<C> mp = constructor.getParameters().get(p);
              CallableMemberMember<C> sp = mostSpecific.getParameters().get(p);
              
             if (isAssignableTo(sp.getType(),mp.getType()).matches()) {
            	 return constructor;
                
             }
         }
    	 return mostSpecific;
    	 
	}
    

    @Override
    public Collection<Method> getMethodsByName(TypeDefinition type,String name) {
        Collection<Method> all = this.ensureNotFundamental(type).getMembers().stream().filter(m -> m.isMethod() && m.getName().equals(name))
                .map(m -> (Method) m).collect(Collectors.toList());

        if (all.isEmpty() && type.getSuperDefinition() != null) {
            all = getMethodsByName(type.getSuperDefinition(), name);
        }
        
        return all;
    }

    
    public Optional<Method> getDeclaredMethodBySignature(TypeDefinition type,MethodSignature signature) {
    	
        if (signature.getName() == null) {
            throw new IllegalArgumentException("Signature must have a name");
        }

        // find exact local

    	Method mostSpecific = null;
        for (TypeMember m : this.ensureNotFundamental(type).getMembers()) {
            if (m.isMethod() && signature.getName().equals(m.getName()) && isSignatureImplementedBy(signature, (CallableMember<Method>) m) ) {
            	 
            	if (mostSpecific == null) {
            		mostSpecific = (Method) m;
            	} else {
            		mostSpecific = mostSpecific(mostSpecific,(Method) m);
            	}
            }
        }
        
        return Optional.ofNullable(mostSpecific);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Method> getMethodBySignature(TypeDefinition type,MethodSignature signature) {

        if (signature.getName() == null) {
            throw new IllegalArgumentException("Signature must have a name");
        }

        type = this.ensureNotFundamental(type);
        
        // find exact local
        Optional<Method> method = getDeclaredMethodBySignature(type, signature);
        
        if (method.isPresent()) {
        	return method;
        }

        // find exact upper class
        if (type.getSuperDefinition() != null && !type.getSuperDefinition().equals(this)) {
           method = getMethodBySignature(type.getSuperDefinition(), signature);

            if (method.isPresent()){
                Method myMethod = method.get().changeDeclaringType(type);
               // this.addMethod(myMethod);

                return Optional.of(myMethod);
            }
        }

        for (TypeDefinition i : type.getInterfaces()){
            Optional<Method> m = getMethodBySignature(i,signature);
            if (m.isPresent()){
                Method myMethod = m.get().changeDeclaringType(type);
              //  this.addMethod(myMethod);

                return Optional.of(myMethod);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Method> getMethodByPromotableSignature(TypeDefinition type,MethodSignature signature) {
        // find promotable

    	Method mostSpecific = null;
    	
    	outter: for (Method mth : this.getMethodsByName(type,signature.getName())) {
            if (mth.getParameters().size() == signature.getParameters().size()) {
            	
            	if (signature.getParameters().isEmpty()) {
            		   return Optional.of(mth);
            	}
            	
                for (int p = 0; p < signature.getParameters().size(); p++) {
                    CallableMemberMember<Method> mp = signature.getParameters().get(p);
                    if (!isPromotableTo(mp.getType(), mth.getParameters().get(p).getType())) {
                    	continue outter;
                    }
                }
                
                if (mostSpecific == null) {
                	mostSpecific = mth;
                } else {
                	mostSpecific = mostSpecific(mostSpecific , mth);
                }
              
            }
        }
    	
    	if (mostSpecific != null) {
        	return Optional.of(mostSpecific);
        } 
    	
    	type = this.ensureNotFundamental(type);
    	
        if (type.getSuperDefinition() != null){
            Optional<Method> m = getMethodByPromotableSignature(type.getSuperDefinition(), signature);
            if (m.isPresent()){
                return m;
            }
        }

        for (TypeDefinition i : type.getInterfaces()){
            Optional<Method> m =  getMethodByPromotableSignature(i,signature);
            if (m.isPresent()){
                return m;
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<IndexerProperty> getIndexerPropertyByTypeArray(TypeDefinition type, TypeVariable[] params) {

    	type = this.ensureNotFundamental(type);
    	
        Optional<IndexerProperty> member = type.getMembers().stream().filter(m -> m.isIndexer() && indexesAreEqual(((IndexerProperty)m).getIndexes(), params))
                .map(m -> (IndexerProperty) m).findAny();

        if (!member.isPresent() && type.getSuperDefinition() != null) {
            return getIndexerPropertyByTypeArray(type.getSuperDefinition(), params);
        }

        return member;
    }



    private boolean indexesAreEqual(TypeVariable[] indexes, TypeVariable[] params) {
        if( indexes.length != params.length) {
            return false;
        }

        for (int i = 0; i < indexes.length; i++){
            if (!isAssignableTo(params[i], indexes[i]).matches()){
                return false;
            }
        }
        return true;
    }

	@Override
	public boolean isSuper(TypeDefinition candidate, TypeDefinition base) {
		  if(this.isAny(base)) {
			 return false;
		 } else if(this.isAny(candidate)) {
			 return true;
		 } else if (base.getSuperDefinition().equals(candidate)) {
			 return true;
		 }
		 return isSuper(candidate, base.getSuperDefinition());
	}

}
