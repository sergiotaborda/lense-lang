package lense.compiler.typesystem.operations;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AlphaSystem implements System{

    @Override
    public boolean isSubType(Type a, Type b) {
        
        a = a.simplify(this);
        b = b.simplify(this);
        
        // a is substype of b if 
        if (a instanceof BottomType){
            // bottom type is a subtype of all types, including itself 
            return true;
        } else if (b instanceof TopType){
            // all types are subtypes of toptype, includign itself
            return true;
        }else if (areEqual(a,b)) { // is the same type
            return true;
        } else if (a instanceof ConcreteType){
            ConcreteType c = (ConcreteType)a;
            if (areEqual(c.getSuperType(), TopType.instance())){
                return false;
            } else if (b instanceof ProductType){
                List<Type> bTypes = ((ProductType)a).composition;
                if (bTypes.size() == 1){
                    return isSubType(a, bTypes.get(0));
                }
            } else {
                
                return isSubType(c.getDerivationType(), b);
            }
        } else if (a instanceof ProductType ){
            List<Type> aTypes = ((ProductType)a).composition;
            
            if (b instanceof ProductType){
                if (aTypes.size() == ((ProductType)b).composition.size()) {
                    Iterator<Type> itA = aTypes.iterator();
                    Iterator<Type> itB =((ProductType)b).composition.iterator(); 
                    
                    while (itA.hasNext()){
                        if (!isSubType(itA.next(), itB.next())){
                            return false;
                        }
                    }
                    return true;
                }
            } else if (b instanceof ConcreteType && aTypes.size() == 1){
                return isSubType(aTypes.get(0), b);
            }
        } else if (a instanceof UnionType ){
            List<Type> aTypes = ((UnionType)a).composition;
            
            if (b instanceof UnionType){
                List<Type> bTypes = ((UnionType)b).composition;
                  
                if (aTypes.size() == aTypes.size()) {
                    Iterator<Type> itA = aTypes.iterator();
                    Iterator<Type> itB = aTypes.iterator(); 
                    
                    while (itA.hasNext()){
                        if (!isSubType(itA.next(), itB.next())){
                            return false;
                        }
                    }
                    return true;
                }
            }
        } else if (a instanceof IntersectionType){
            List<Type> aTypes = ((IntersectionType)a).composition;
            
            if (b instanceof IntersectionType){
                List<Type> bTypes = ((IntersectionType)b).composition;
                  
                if (aTypes.size() == aTypes.size()) {
                    Iterator<Type> itA = aTypes.iterator();
                    Iterator<Type> itB = aTypes.iterator(); 
                    
                    while (itA.hasNext()){
                        if (isSubType(itA.next(), itB.next())){
                            return true;
                        }
                    }
                    return false;
                }
            } else {
               
           
                    Iterator<Type> itA = aTypes.iterator();
                   
                    while (itA.hasNext()){
                        if (isSubType(itA.next(), b)){
                            return true;
                        }
                    }
                    return false;
                
            }
        }
        
        return false;
    }



    @Override
    public boolean areEqual(Type a, Type b) {
        if (a.getClass().isAssignableFrom(b.getClass())){
            if (a instanceof ConcreteType){
                return ((ConcreteType)a).getName().equals(((ConcreteType)b).getName());
            } else if (a instanceof CompositeType){
                List x =  ((CompositeType)a).composition;
                List y =  ((CompositeType)b).composition;
                if (x.size() == y.size()){
                    Iterator<Type> itx = x.iterator();
                    Iterator<Type> ity = y.iterator();

                    while (itx.hasNext()){
                        if (!areEqual(itx.next() , ity.next())){
                            return false;
                        }
                    }

                    return true;
                }
            }
            return true;
        }
        return false;  
    }



    @Override
    public Type reduce(Type type) {
        if (type instanceof IntersectionType){
            // if any type in the intersection is final 
            
            List<Type> all = ((IntersectionType)type).composition;
            
            Set<Type> rest = new LinkedHashSet<>();
  
            outter : for (int i =0; i < all.size(); i++){
                Type a = all.get(i);
                for (int j = i+1 ; j< all.size(); j++){
                    Type b = all.get(j);
                    if (this.isSubType(a, b)){
                        rest.add(a);
                    } else if (this.isSubType(b, a)){
                        rest.add(b);
                        continue outter;
                    } else {
                        rest.add(b);
                    }
                }
                rest.add(a);
            }
            
            List<Type> finalTypes = rest.stream().filter(t -> t.isFinal()).collect(Collectors.toList());
            if (rest.size() == 1){
                return rest.iterator().next();
            } else if (rest.isEmpty() || !finalTypes.isEmpty()){
                return BottomType.instance();
            } else {
                return new IntersectionType(rest);
            }
        }
        return type;
    }

}
