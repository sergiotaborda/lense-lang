package lense.compiler.ast;

public class ConstructorExtentionNode extends LenseAstNode {

    private String level;

    public void setArguments(ArgumentListNode node) {
        this.add(node);
    }
    
    public ArgumentListNode getArguments(){
        return (ArgumentListNode)this.getFirstChild();
    }

    public void setCallLevel(String level) {
        this.level = level;
    }

    public String getCallLevel() {
        return level;
    }

    public void setConstructorName(String constructorName) {
        // TODO Auto-generated method stub
        
    }



}
