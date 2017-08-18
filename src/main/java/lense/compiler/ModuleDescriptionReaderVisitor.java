package lense.compiler;

import compiler.syntax.AstNode;
import compiler.trees.Visitor;
import compiler.trees.VisitorNext;
import lense.compiler.ast.ModuleNode;
import lense.compiler.ast.ModuleImportNode;
import lense.compiler.modules.EditableModuleDescriptor;
import lense.compiler.modules.ModuleExport;
import lense.compiler.ast.ModuleExportNode;
public class ModuleDescriptionReaderVisitor implements Visitor<AstNode> {

    private EditableModuleDescriptor moduleDescriptor;

    public ModuleDescriptionReaderVisitor(EditableModuleDescriptor moduleDescriptor) {
       this.moduleDescriptor = moduleDescriptor;
    }

    @Override
    public void startVisit() {}

    @Override
    public void endVisit() {}

    @Override
    public VisitorNext visitBeforeChildren(AstNode node) {
        return VisitorNext.Children;
    }

    @Override
    public void visitAfterChildren(AstNode node) {
       if (node instanceof ModuleNode){
           ModuleNode module = (ModuleNode)node;
           moduleDescriptor.setName(module.getName());
           moduleDescriptor.setVersion(module.getVersion()); 
       } else if (node instanceof ModuleImportNode){
           ModuleImportNode otherModule = (ModuleImportNode)node;
     
           moduleDescriptor.addRequiredModule(new EditableModuleDescriptor(otherModule.getQualifiedNameNode().toString(), otherModule.getVersionNode().getVersion()));
       } else if (node instanceof ModuleExportNode){
           ModuleExportNode otherModule = (ModuleExportNode)node;
     
           moduleDescriptor.addExport(new ModuleExport(otherModule.getName(), otherModule.doesIncludeAllSubLevels()));
       } 
    }

   

}
