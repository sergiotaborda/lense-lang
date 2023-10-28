package lense.compiler.crosscompile.typescript;

import java.util.LinkedList;
import java.util.List;

import lense.compiler.ast.LenseAstNode;

public class NamespaceNode {

    private String name;
    private List<NamespaceNode> children = new LinkedList<>();
    private List<LenseAstNode> units = new LinkedList<>();
    private NamespaceNode parent;
    
    public NamespaceNode(String name){
        this.name = name;
    }
    
    public String getstring(){
        return name;
    }
    
    public void add(NamespaceNode node){
        node.parent = this;
        children.add(node);
    }
    
    public NamespaceNode getParent(){
        return this.parent;
    }
    
    public void addUnit(LenseAstNode unit){
        units.add(unit);
    }

    public String getName() {
        return name;
    }

    public List<LenseAstNode> getUnits() {
        return units;
    }
    
    public List<NamespaceNode> getChildren() {
        return children;
    }
}
