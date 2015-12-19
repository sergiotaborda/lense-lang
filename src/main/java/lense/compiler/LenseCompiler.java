/**
 * 
 */
package lense.compiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.GenericTypeParameterNode;
import lense.compiler.ast.ModuleImportNode;
import lense.compiler.ast.ModuleNode;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.ast.TypeNode;
import lense.compiler.ast.UnitTypes;
import lense.compiler.crosscompile.java.OutToJavaSource;
import lense.compiler.dependency.DependencyGraph;
import lense.compiler.dependency.DependencyNode;
import lense.compiler.dependency.DependencyRelation;
import lense.compiler.dependency.DependencyRelationship;
import lense.compiler.graph.EdgeTraversalEvent;
import lense.compiler.graph.GraphTransversor;
import lense.compiler.graph.GraphTranverseListener;
import lense.compiler.graph.TopologicOrderTransversor;
import lense.compiler.graph.VertexTraversalEvent;
import lense.compiler.phases.LenseSemanticPhase;
import lense.compiler.phases.NameResolutionPhase;
import lense.compiler.repository.MachineRepository;
import lense.compiler.repository.ModuleRepository;
import lense.compiler.repository.TypeRepository;
import lense.compiler.typesystem.LenseTypeDefinition;
import compiler.AstCompiler;
import compiler.CompilationResult;
import compiler.CompilationResultSet;
import compiler.CompilationUnitSet;
import compiler.CompiledUnit;
import compiler.CompilerListener;
import compiler.CompilerMessage;
import compiler.FolderCompilationUnionSet;
import compiler.ListCompilationUnitSet;
import compiler.StringCompilationUnit;
import compiler.syntax.AstNode;
import compiler.typesystem.GenericTypeParameter;
import compiler.typesystem.TypeSearchParameters;

/**
 * 
 */
public class LenseCompiler {

	private CompilerListener listener = new CompilerListener(){

		@Override
		public void start() {

		}

		@Override
		public void error(CompilerMessage error) {
			System.err.println(error.getMessage());
		}

		@Override
		public void warn(CompilerMessage error) {

		}

		@Override
		public void end() {

		}

	};

	public LenseCompiler (){}

	public void setCompilerListener(CompilerListener listener){
		this.listener = listener;
	}
	/**
	 * @param moduleproject
	 * @throws IOException 
	 */
	public void compileModuleFromDirectory(File moduleproject){

		listener.start();
		try {

			File sources = new File(moduleproject, "source");

			if (!sources.exists()){
				listener.error(new CompilerMessage("No sources found. No folder " + sources.getAbsolutePath() +" exists"));
			}

			File modulesOut = new File(moduleproject, "compilation/java/bin");
			if (!modulesOut.exists()){
				modulesOut.mkdirs();
			}

			File target = new File(moduleproject, "compilation/java/target");
			if (!target.exists()){
				target.mkdirs();
			} else {
				Path directory = target.toPath();
				Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						Files.delete(file);
						return FileVisitResult.CONTINUE;
					}

				});
			}

			CompilationUnitSet moduleUnit = new FolderCompilationUnionSet(sources , name -> name.equals("module.lense"));

			AstCompiler parser = new AstCompiler(new LenseLanguage());
			

			final List<CompiledUnit> modulesList = parser.parse(moduleUnit).sendToList();

			if (modulesList.isEmpty()){
				listener.error(new CompilerMessage("Could not find module.lense file in the source. "));
			}

			ModuleNode module  = (ModuleNode) modulesList.get(0).getAstRootNode().getChildren().get(0);

			// The global, even remote, repository
			TypeRepository localRepository = new MachineRepository(); // TODO bind to some folder in the OS or URL

			// The repository being mounted
			ModuleRepository currentModuleRepository = new ModuleRepository(module, localRepository);


			for(compiler.syntax.AstNode n : module.getImports().getChildren()){
				ModuleImportNode ip = (ModuleImportNode)n;
				// import dependent modules from local repository
				// is they are not present , abort
				currentModuleRepository.importModule(ip.getQualifiedNameNode(), ip.getVersionNode());
			}

			DependencyGraph graph = new DependencyGraph();

			DependencyNode moduleNode = new DependencyNode(modulesList.get(0), module.getName());

			Set<QualifiedNameNode> packages = new HashSet<>(); 

			CompilationUnitSet unitSet = new FolderCompilationUnionSet(sources , name -> !name.equals("module.lense") && name.endsWith(".lense"));


			Set<String> foundNames = new HashSet<>();
			Set<String> referencedNames = new HashSet<>();

			parser.parse(unitSet)
			.passBy(new NameResolutionPhase(currentModuleRepository, new PathPackageResolver(sources.toPath()), listener))
			.peek(node -> {
				UnitTypes t = (UnitTypes) node.getAstRootNode();

				for(ClassTypeNode type : t.getTypes()){

					foundNames.add(type.getName());

					// the module depends on every type inside
					Optional<DependencyNode> it = graph.findDependencyNode(type.getName());
					DependencyNode dn;
					if (it.isPresent()){
						dn = it.get();
						dn.setUnit(node);
					}else {
						dn = new DependencyNode(node, type.getName());
					}

					List<GenericTypeParameter> typeParameters = new ArrayList<>();

					if (type.getGenerics() != null){

						for(AstNode n : type.getGenerics().getChildren()){
							GenericTypeParameterNode gnode = ((GenericTypeParameterNode)n);
							TypeNode tn = gnode.getTypeNode();
							typeParameters.add( new lense.compiler.typesystem.FixedGenericTypeParameter(tn.getName(), null, gnode.getVariance()));
						}

					}

					LenseTypeDefinition ltype = new LenseTypeDefinition(type.getName(), type.getKind(), null,typeParameters.toArray(new compiler.typesystem.GenericTypeParameter[0]));


					currentModuleRepository.registerType(ltype);

					graph.addEdge(new DependencyRelation(DependencyRelationship.Module), dn, moduleNode);


					packages.add(new QualifiedNameNode(type.getSemanticContext().getCurrentPackageName()));
					for(Import imp : type.imports()){
						String name = imp.getTypeName().getName();
						it = graph.findDependencyNode(name);
						DependencyNode imported;
						if (it.isPresent()){
							imported = it.get();
							imported.setUnit(node);
						}else {
							imported = new DependencyNode(null, name);
						}


						if (imp.isMemberCalled()){
							System.out.println(dn.getName() + " strongly depends on " + imported.getName());
							graph.addEdge(new DependencyRelation(DependencyRelationship.Structural),  imported, dn);
						} else {
							System.out.println(dn.getName() + " referes by name " + imported.getName());
							// TODO validate it exists
						}

						referencedNames.add(imported.getName());

					}
				}



			}).sendToList();

			referencedNames.removeAll(foundNames);

			for(Iterator<String> it = referencedNames.iterator(); it.hasNext(); ){
				if (currentModuleRepository.resolveType(new TypeSearchParameters(it.next(), 0)).isPresent()){
					it.remove();
				}
			}

			if (!referencedNames.isEmpty()){
				throw new CompilationError("Type " + referencedNames.stream().filter(r -> r.length() > 0).findFirst().get() + " was not found");
			}

			LenseSemanticPhase semantic = new LenseSemanticPhase(listener);

			GraphTransversor<DependencyRelation, DependencyNode> tt = new TopologicOrderTransversor<>();
			tt.addListener(new GraphTranverseListener<DependencyNode, DependencyRelation>() {

				@Override
				public void endVertex(VertexTraversalEvent<DependencyNode, DependencyRelation> e) {

				}



				@Override
				public void beginVertex(VertexTraversalEvent<DependencyNode, DependencyRelation> e) {
					System.out.println("Visiting : " + e.getVertex().getObject().getName());
					new CompilationResultSet( new CompilationResult(e.getVertex().getObject().getCompiledUnit())).passBy(semantic).sendTo(new OutToJavaSource(target));
					System.out.println("Visited : " + e.getVertex().getObject().getName());
				}
				@Override
				public void endEdgeTraversed(
						EdgeTraversalEvent<DependencyRelation, DependencyNode> e) {

				}
				@Override
				public void beginEdgeTraversed(
						EdgeTraversalEvent<DependencyRelation, DependencyNode> e) {

				}
			});
			tt.transverse(graph, moduleNode);

			// TODO organize units in dependency order

			// compile souce files and read packages
			//			compiler.parse(unitSet).peek(new OutToJavaSource(target)).stream().forEach( cr -> {  // TODO add package to type from folder
			//				if (!cr.isError()){
			//					UnitTypes unit = (UnitTypes)cr.getCompiledUnit().getAstRootNode();
			//
			//					ClassTypeNode type = (ClassTypeNode) unit.getChildren().get(0);
			//
			//					QualifiedNameNode qn = new QualifiedNameNode(type.getName());
			//
			//					QualifiedNameNode pack = qn.getPrevious();
			//					if (!pack.getName().startsWith(module.getName())){
			//						throw new CompilationError("Package '" + pack + "' name in unit '" + type.getName() + "' does not beging with module name '" + module.getName() + "')");
			//					}
			//					packages.add(pack);
			//				}
			//			});


			// produce packages
			ListCompilationUnitSet all = new ListCompilationUnitSet();
			for(QualifiedNameNode pack : packages){



				StringBuilder builder = new StringBuilder("import lense.core.lang.reflection.Package; import lense.core.lang.String;")
				.append("public class ").append("Package$$Info implements Package { ");

				builder.append("public String getName(){");
				builder.append("	return \"").append(pack).append("\" ;");
				builder.append("}");
				builder.append("}");

				all.add(new StringCompilationUnit(builder.toString(), sources.toPath().resolve(pack.getName().replace('.', '/')).toString()+ "/Package$$Info.lense")); // TODO specify package
			}

			parser.parse(all)
			.passBy(new NameResolutionPhase(currentModuleRepository, new PathPackageResolver(sources.toPath()), listener))
			.sendTo(new OutToJavaSource(target));
			
			// produce module
			Properties p = new Properties();

			p.put("module.name", module.getName());
			p.put("module.version", module.getVersion().toString());

			File moduleProperties = new File(target, "module.properties");
			p.store(new FileOutputStream(moduleProperties), "Lense module definition");

			StringBuilder builder = new StringBuilder("import lense.core.lang.reflection.Module; import lense.core.lang.Version; import lense.core.collections.Sequence; import lense.core.collections.LinkedList; import lense.core.lang.reflection.Package; ");
			
			int i=1;
			for(QualifiedNameNode pack : packages){ 
				builder.append(" import ").append(pack).append(".Package$$Info as Pack").append(i).append(";\n");
				i++;
			}
			
			builder.append("public class ").append("Module$$Info implements Module { ");

			builder.append("public Version getVersion(){");
			builder.append("	return new Version(\"").append(module.getVersion()).append("\");");
			builder.append("}");
			builder.append("public Sequence<Package> getPackages(){");
			builder.append("	var LinkedList<Package> all = new LinkedList<Package>(); ");
			
			for(i =0; i < packages.size(); i++){ 
				builder.append("	all.add(Pack").append(i).append(");\n");
			}
			
			builder.append(" 	return all;");
			builder.append("}");
			builder.append("}");
			all = new ListCompilationUnitSet();
			all.add(new StringCompilationUnit(builder.toString(), sources.toPath().resolve(module.getName().replace('.', '/')).toString() + "/Module$$Info.lense")); // TODO specify package

			parser.parse(all)
			.passBy(new NameResolutionPhase(currentModuleRepository, new PathPackageResolver(sources.toPath()), listener))
			.sendTo(new OutToJavaSource(target));

			// TODO zip it to a jar file and delete all singular files.
		} catch (Exception e) {
			e.printStackTrace();
			listener.error(new CompilerMessage(e.getMessage()));
		} finally {
			listener.end();
		}

	}


}
