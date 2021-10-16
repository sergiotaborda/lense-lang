package lense.compiler.crosscompile.pim;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import compiler.AstCompiler;
import compiler.CompiledUnit;
import compiler.ListCompilationUnitSet;
import compiler.SourceFileCompilationUnit;
import compiler.filesystem.SourceFile;
import compiler.filesystem.SourceFolder;
import lense.compiler.FileLocations;
import lense.compiler.LenseCompiler;
import lense.compiler.LenseLanguage;
import lense.compiler.PathPackageResolver;
import lense.compiler.TypeMembersNotLoadedError;
import lense.compiler.TypeNotFoundError;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ModuleNode;
import lense.compiler.ast.SystemOutCompilerListener;
import lense.compiler.dependency.CompilationUnitDependencyNode;
import lense.compiler.dependency.DependencyRelation;
import lense.compiler.dependency.DependencyRelationship;
import lense.compiler.graph.GraphTransversor;
import lense.compiler.graph.GraphTranverseListener;
import lense.compiler.graph.TopologicOrderTransversor;
import lense.compiler.graph.VertexTraversalEvent;
import lense.compiler.modules.ModulesRepository;
import lense.compiler.phases.CompositePhase;
import lense.compiler.phases.ConstructorDesugarPhase;
import lense.compiler.phases.NameResolutionPhase;
import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.TypeDefinition;

public class LenseToPimCompiler extends LenseCompiler {

	private final static PimCompilerBackEndFactory compilerBackEndFactory = new PimCompilerBackEndFactory();
	private FileLocations fileLocations;
	private Map<String, SourceFile> compilerNativeTypes = new HashMap<String, SourceFile>();
	private CompositePhase corePhase;
	
	public LenseToPimCompiler(
		ModulesRepository globalRepository
	) {
		super("pim", globalRepository, compilerBackEndFactory);
	}

	@Override
	protected void initCorePhase(
			CompositePhase corePhase, 
			Map<String, SourceFile> nativeTypes,
			UpdatableTypeRepository typeContainer
	) {
		this.corePhase = corePhase;
		
	}

	@Override
	protected void createModuleArchive(FileLocations locations, ModuleNode module, Set<String> applications)
			throws IOException, FileNotFoundException {
		

	}


	@Override
	protected void collectNative(FileLocations fileLocations, Map<String, SourceFile> nativeTypes) throws IOException {
		this.fileLocations = fileLocations;
		
//	    var sourcePath = this.fileLocations.getSourceFolder().getPath();
//
//		var rootDir = fileLocations.getSourceFolder();
//	    
//		rootDir.walkTree(new SourceWalker(){
//
//			@Override
//			public SourceWalkerResult visitFile(SourceFile file) {
//				if (file.getName().endsWith(".lense")){
//					
//					AstCompiler parser = new AstCompiler(new LenseLanguage());
//
//					ListCompilationUnitSet set = new ListCompilationUnitSet();
//					
//					set.add(new SourceFileCompilationUnit(file));
//					
//					parser.parse(set).sendToList().stream()
//							.flatMap(it -> it.getAstRootNode().getChildren().stream())
//							.filter(it -> it instanceof ClassTypeNode node && isNative(node))
//							.map(it -> (ClassTypeNode)it)
//							.forEach(it -> {
//								
//								var source = sourcePath.relativize(file.getPath());
//								
//								var typeName = source.getParent().join(".") + "." + it.getSimpleName();
//								
//								nativeTypes.put(typeName, file);
//								compilerNativeTypes.put(typeName, file);
//							});
//
//					 
//					
//				}
//				return SourceWalkerResult.CONTINUE;
//			}
//
//		});

	}
	
	Set<String> pseudoNativeSet = Set.of(
			"Integer", 
			"Natural",
			"Float", 
			"Whole",
			"Imaginary",
			"Real",
			"Rational",
			"Complex",
			"SignedNumber" , 
			"RealLineElement" , 
			"Number", 
			"Comparable",
			"Comparison",
			"Ordinal",
			"Progressable",
			"Progression",
			"Iterable",
			"Iterator",
			"Sequence",
			"Assortment",
			"Countable",
			"KeyValuePair",
			"Application",
			"Version"
	);
	
	private boolean isNative(ClassTypeNode node) {
		return node.isNative() || node.getKind().isInterface() || pseudoNativeSet.contains(node.getFullname());
	}

	@Override
	protected SourceFile resolveNativeFile(SourceFolder folder, String name) {
		
		var s = compilerNativeTypes.get(name.replace('\\', '.'));
		return s;
		//return this.fileLocations.getSourceFolder().file(name + ".lense");
	}
	
	@Override
	protected List<TypeDefinition> extactTypeDefinitionFromNativeType(
			UpdatableTypeRepository currentTypeRepository,
			Collection<SourceFile> files
	) throws IOException {
		
	    var deque = new LinkedList<SourceFile>();
	    var nativeTypesDefs = new LinkedList<TypeDefinition>();
	    var sourcePath = this.fileLocations.getSourceFolder().getPath();
	    var listener = new SystemOutCompilerListener();
	    
	    
	    var prePhase =  new CompositePhase()
	    		.add(new ConstructorDesugarPhase(listener)) 
	    		.add(new NameResolutionPhase(new PathPackageResolver(fileLocations.getSourceFolder().getPath()), listener));

	    var phase = new CompositePhase()
	    .add(prePhase)
		.add(corePhase);
	    
	    AstCompiler parser = new AstCompiler(new LenseLanguage());

	    
		GraphTransversor<DependencyRelation, CompilationUnitDependencyNode> tt = new TopologicOrderTransversor<>();

		tt.addListener(new GraphTranverseListener<CompilationUnitDependencyNode, DependencyRelation>() {

			@Override
			public void endVertex(VertexTraversalEvent<CompilationUnitDependencyNode, DependencyRelation> e) {

				CompiledUnit unit = e.getVertex().getObject().getCompiledUnit();

				if (unit != null && !unit.getProperty("visited", Boolean.class).orElse(false)) {

					System.out.println("Visiting EXtract : " + e.getVertex().getObject().getName());

				//	unit.setProperty("visited", Boolean.TRUE);
					
					var path = sourcePath.relativize(unit.getUnit().getOrigin());
					
					deque.add(fileLocations.getSourceFolder().file(path));

				} 

			}
		});

		tt.transverse(graph, null);
		
//		LenseTypeSystem.getInstance().getAll().stream()
//		.filter(it -> it.isNative())
//		.forEach(it ->{
//			
//			currentTypeRepository.registerType(it, it.getGenericParameters().size());
//			
//		});
		
		while (!deque.isEmpty()) {
			
			var target = deque.pop();
			var source = sourcePath.relativize(target.getPath());
		
			var name = source.getName();
			var pos = name.indexOf('.');
			var typeName = name.substring(0, pos);
			
			ListCompilationUnitSet set = new ListCompilationUnitSet();
			
			set.add(new SourceFileCompilationUnit(target));
			
			try {
				parser.parse(set).passBy(phase).sendToList().stream()
				.flatMap(it -> it.getAstRootNode().getChildren().stream())
				.filter(it -> it instanceof ClassTypeNode node && isNative(node) && node.getSimpleName().equals(typeName))
				.map(it -> (ClassTypeNode)it).forEach(it ->{
					
			
					nativeTypesDefs.add(it.getTypeDefinition());
					
				});
			
			} catch (TypeNotFoundError | TypeMembersNotLoadedError e) {
				var match = deque.stream().filter(it -> {
					var s = sourcePath.relativize(it.getPath());
					
					var n = s.getName();
					var p = n.indexOf('.');


					return (s.getParent().join(".") + "." + n.substring(0, p)).equals(e.getTypeName());
					
				}).findAny();
				
				if (match.isPresent()) {
					deque.addFirst(target); // put back to reprocess
					deque.remove(match.get());
					deque.addFirst(match.get());
				} else {
					throw e;
				}
			}

  		}

		return nativeTypesDefs;
	}

	@Override
	protected boolean shouldGraphContain(DependencyRelationship parameter) {
		return true;
	}

}
