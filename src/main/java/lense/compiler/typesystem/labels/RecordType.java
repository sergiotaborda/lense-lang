package lense.compiler.typesystem.labels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordType implements Type{

    private boolean isfinal;

    private Map<String, TypeMember> members = new HashMap<>();

    private String name;

    public RecordType (String name){
        this.name = name;
    }
    
    public List<TypeMember> members(){
        return new ArrayList<>(this.members.values());
    }
    
    public TypeMember getMember(String name){
        return this.members.get(name);
    }
    
    public String toString(){
        StringBuilder builder = new StringBuilder(name).append("{");
        
        return builder.append("}").toString();
    }
    @Override
    public Type or(Type other) {
        return new UnionType(this, other);
    }

    @Override
    public Type and(Type other) {
        if (other instanceof RecordType){
            Map<String,TypeMember> o = new HashMap<>(((RecordType)other).members);
            RecordType intersection = new RecordType(this.name);
            
            for (Map.Entry<String, TypeMember> member : this.members.entrySet()){
                TypeMember type = o.remove(member.getKey());
                if (type == null){
                    intersection.putMember(member.getKey(), member.getValue().getType());
                } else {
                    intersection.putMember(member.getKey(), member.getValue().getType().and(type.getType()));
                }
            }
            
            if (!o.isEmpty()){
                for (Map.Entry<String, TypeMember> member : o.entrySet()){
                    intersection.putMember(member.getKey(), member.getValue().getType());
                }
            }
            
            return intersection;
            
        } else {
            return new IntersectionType(this, other);
        }
       
    }

    @Override
    public Type times(Type other) {
        return new ProductType(this, other);
    }

    @Override
    public Type simplify(System system) {
        return system.reduce(this);
    }

    @Override
    public boolean isFinal() {
        return isfinal;
    }
    
    public void setFinal(boolean isFinal){
        this.isfinal = isFinal;
    }

    public void putMember(String name, Type type){
        this.members.put(name, new TypeMember(type, name));
    }
}
