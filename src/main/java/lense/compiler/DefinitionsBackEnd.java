package lense.compiler;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import compiler.CompiledUnit;
import compiler.CompilerBackEnd;
import compiler.filesystem.SourceFolder;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.utils.Strings;

public class DefinitionsBackEnd implements CompilerBackEnd {

	private FileLocations locations;

	public DefinitionsBackEnd(FileLocations locations) {
		this.locations= locations;
	}

	@Override
	public void use(CompiledUnit unit) {
	
		var target= locations.getTargetFolder();
		for (AstNode node : unit.getAstRootNode().getChildren()) {

			if (target.isFolder()){

				if (!(node instanceof ClassTypeNode)){
					continue;
				}
				ClassTypeNode t = (ClassTypeNode)node;

				if (t.isNative()){
					continue;
				}

				String[] names = Strings.split(t.getFullname(), ".");
				
				if (t.getKind().isObject()) {
					names[names.length -1 ] = Strings.cammelToPascalCase(names[names.length - 1]);
				}
				
				String path =  Strings.join(names, "/");
				int pos = path.lastIndexOf('/');
				String filename = path.substring(pos+1) + ".def.lense";
				SourceFolder folder;
				if (pos >=0){
					path = path.substring(0, pos);
					folder = target.folder(path);
				} else {
					folder = target;
				}

				folder.ensureExists();

				var compiled = folder.file(filename); 
		
				try(PrintWriter writer = new PrintWriter(new OutputStreamWriter(compiled.outputStream()))){
					TreeTransverser.transverse(node, new DefinitionsVisitor(writer));
				}

			}
		}
	
	}

}
