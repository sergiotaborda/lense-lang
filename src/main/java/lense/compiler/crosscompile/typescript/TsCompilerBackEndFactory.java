package lense.compiler.crosscompile.typescript;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import compiler.CompiledUnit;
import compiler.CompilerBackEnd;
import compiler.filesystem.SourceFile;
import compiler.filesystem.SourceFolder;
import compiler.syntax.AstNode;
import compiler.trees.TreeTransverser;
import lense.compiler.CompilerBackEndFactory;
import lense.compiler.FileLocations;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.LenseAstNode;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.crosscompile.java.JavaSourceWriterVisitor;
import lense.compiler.utils.Strings;

public class TsCompilerBackEndFactory implements CompilerBackEndFactory {

	@Override
	public CompilerBackEnd create(FileLocations locations) {
		return new TsCompilerBackEnd(locations);
	}



	static class TsCompilerBackEnd implements CompilerBackEnd {

		private FileLocations locations;
		private Map<String , NamespaceNode> namespaces = new HashMap<>();

		public TsCompilerBackEnd(FileLocations locations) {
			this.locations = locations;
		}

		/**
		 * {@inheritDoc}
		 */
		 @Override
		 public void use(CompiledUnit unit) {
			 toSource(unit);
		 }


		 protected List<SourceFile> toSource(CompiledUnit unit) {

			 List<SourceFile> files = new LinkedList<>();

			 for (AstNode node : unit.getAstRootNode().getChildren()) {

				 var target = locations.getTargetFolder();
				 SourceFile  compiled = null;
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
					 String filename = path.substring(pos+1) + ".ts";
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
					 TreeTransverser.transverse(node, new TsSourceWriterVisitor(writer));
					 node.setProperty("writen", Boolean.TRUE);
					 files.add(compiled);
				 } 
			 }
			 return files;
		 }
	}



	@Override
	public void setClasspath(List<SourceFolder> classpath) {
		// TODO Auto-generated method stub

	}

}
