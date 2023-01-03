package lense.compiler.crosscompile.javascript;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import compiler.CompiledUnit;
import compiler.CompilerBackEnd;
import compiler.filesystem.SourceFolder;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import lense.compiler.CompilerBackEndFactory;
import lense.compiler.FileLocations;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.LenseAstNode;
import lense.compiler.ast.QualifiedNameNode;

public class JsCompilerBackEndFactory implements CompilerBackEndFactory {

    @Override
    public CompilerBackEnd create(FileLocations locations) {
        return new JsCompilerBackEnd(locations);
    }

 

    static class JsCompilerBackEnd implements CompilerBackEnd {

        private FileLocations locations;
        private Map<String , NamespaceNode> namespaces = new HashMap<>();

        public JsCompilerBackEnd(FileLocations locations) {
            this.locations = locations;
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public void use(CompiledUnit unit) {

            for ( AstNode it : ((LenseAstNode)unit.getAstRootNode()).findChilds(c -> c instanceof ClassTypeNode)){

                ClassTypeNode classNode = (ClassTypeNode)it;
                QualifiedNameNode original = new QualifiedNameNode(classNode.getFullname());

                QualifiedNameNode name = original.getPrevious();
                NamespaceNode subNode = new NamespaceNode(original.getName());
                subNode.addUnit(classNode);
                
                while (name != null){
                    NamespaceNode node = namespaces.get(name.getName());

                    if (node == null){
                        NamespaceNode ns = new NamespaceNode(name.getName());
                        
                        namespaces.put(name.getName(), ns);
                        
                        if (subNode != null ){
                            ns.add(subNode);
                        }
                        subNode = ns;
                        
                        name = name.getPrevious();
                    } else {

                        node.add(subNode);
                        break;
                    }
                }

            }

        }

        public void afterAll() {
            List<NamespaceNode> roots = namespaces.values().stream().filter(ns -> ns.getParent() == null).collect(Collectors.toList());
           
            var target = locations.getTargetFolder();
           
            for (NamespaceNode root : roots ){
                
                String path = root.getName().replace('.', '/');
                int pos = path.lastIndexOf('/');
                String filename = path.substring(pos+1) + ".js";
                SourceFolder folder;
                if (pos >=0){
                    path = path.substring(0, pos);
                    folder = target.folder(path);
                } else {
                    folder = target;
                }

                folder.ensureExists();

                var compiled = folder.file(filename);
            
                compiled.ensureExists();

                try(PrintWriter writer = new PrintWriter(new OutputStreamWriter(compiled.outputStream()))){
                    
                    // add header
                    printNameSpace("",root, writer);
                    
                } 
           
            }
        }


        private void printNameSpace(String parentNameSpaceName, NamespaceNode ns, PrintWriter writer) {
            QualifiedNameNode original = new QualifiedNameNode(ns.getName());
            
            String namespaceName = original.getLast().getName();
            if (parentNameSpaceName.isEmpty()){
                writer.append("var ").append(namespaceName).append(" = ").append(namespaceName).append(" || {}; ").println();;
            } else if (!ns.getChildren().isEmpty()){
                writer.append(parentNameSpaceName).append(".").append(namespaceName).append(" = ").append(parentNameSpaceName).append(".").append(namespaceName).append(" || {}; ").println();;
            }
               
            for ( AstNode u : ns.getUnits()){
                TreeTransverser.transverse(u, new Ecma5JavascriptWriterVisitor(locations.getNativeFolder(), writer, namespaceName));
                
            }

            // add each element 
            for ( NamespaceNode n : ns.getChildren()){
                if (parentNameSpaceName.isEmpty()){
                    printNameSpace(namespaceName, n, writer);
                } else {
                    printNameSpace(parentNameSpaceName + "." + namespaceName, n, writer);
                }
                
            }
        }


    }



	@Override
	public void setClasspath(List<SourceFolder> classpath) {
		// TODO Auto-generated method stub
		
	}

}
