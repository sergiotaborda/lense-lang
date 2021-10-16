package lense.compiler.crosscompile.pim;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import compiler.CompiledUnit;
import compiler.CompilerBackEnd;
import compiler.filesystem.SourceFile;
import compiler.filesystem.SourceFolder;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import lense.compiler.FileLocations;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.utils.Strings;

public class PimCompilerBackEnd implements CompilerBackEnd {

	
	private FileLocations fileLocations;

	public PimCompilerBackEnd (FileLocations fileLocations){
		this.fileLocations = fileLocations;
	}
	
	@Override
	public void use(CompiledUnit unit) {

		try{
			var target = fileLocations.getTargetFolder();

			List<SourceFile> files = new LinkedList<>();

			for (AstNode node : unit.getAstRootNode().getChildren()) {
				SourceFile  compiled = null;
				if (target.isFolder()){

					if (!(node instanceof ClassTypeNode)){
						continue;
					}
					ClassTypeNode t = (ClassTypeNode)node;

					String[] names = Strings.split(t.getFullname(), ".");
					
					if (t.getKind().isObject()) {
						names[names.length -1 ] = Strings.cammelToPascalCase(names[names.length - 1]);
					}
					
					String path =  Strings.join(names, "/");
					int pos = path.lastIndexOf('/');
					
					String filename = path.substring(pos+1) + ".unit";
					SourceFolder folder;
					if (pos >=0){
						path = path.substring(0, pos);
						folder = target.folder(path);
					} else {
						folder = target;
					}

					folder.ensureExists();

					compiled = folder.file(filename);
					compiled.ensureExists();

				

				}

				try(PrintWriter writer = new PrintWriter(compiled.writer())){
					TreeTransverser.transverse(node, new PimUnitWriterVisitor(writer));
					files.add(compiled);
				} 
			}

		} catch (Exception e){
			throw new RuntimeException("Error compiling unit " + unit.getUnit().getName(),e);
		}
	}


}
