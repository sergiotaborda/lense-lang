/**
 * 
 */
package lense.compiler.crosscompile.java;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import compiler.CompilationResult;
import compiler.CompilationResultSet;
import compiler.CompiledUnit;
import compiler.CompilerBackEnd;
import compiler.CompilerListener;
import compiler.filesystem.SourceFile;
import compiler.filesystem.SourceFileSystemNode;
import compiler.filesystem.SourceFolder;
import compiler.filesystem.SourceWalker;
import compiler.filesystem.SourceWalkerResult;
import lense.compiler.CompilationError;
import lense.compiler.FileLocations;
import lense.compiler.LenseCompiler;
import lense.compiler.NativeSourceInfo;
import lense.compiler.PackageSourcePathUtils;
import lense.compiler.asm.ByteCodeTypeDefinitionReader;
import lense.compiler.ast.ClassTypeNode;
import lense.compiler.ast.ModuleImportNode;
import lense.compiler.ast.ModuleNode;
import lense.compiler.ast.UnitTypes;
import lense.compiler.crosscompile.ErasurePhase;
import lense.compiler.crosscompile.NativePeersPhase;
import lense.compiler.crosscompile.java.JavaCompilerBackEndFactory.JavaCompilerBackEnd;
import lense.compiler.dependency.DependencyRelationship;
import lense.compiler.modules.ModulesRepository;
import lense.compiler.phases.CompositePhase;
import lense.compiler.phases.DesugarPhase;
import lense.compiler.phases.EnhancementPhase;
import lense.compiler.phases.ReificationPhase;
import lense.compiler.repository.ModuleCompilationScopeTypeRepository;
import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeDefinition;
import lense.compiler.utils.Strings;

/**
 * 
 */
public class LenseToJavaCompiler extends LenseCompiler{


	private final static JavaCompilerBackEndFactory javaCompilerBackEndFactory = new JavaCompilerBackEndFactory();

	public LenseToJavaCompiler (ModulesRepository globalModulesRepository){
		super("java",globalModulesRepository,javaCompilerBackEndFactory); 
	}
	
	@Override
	protected boolean shouldGraphContain(DependencyRelationship parameter) {
		return DependencyRelationship.Structural == parameter;
	}


	protected void initCorePhase(CompositePhase corePhase, Map<String, NativeSourceInfo> nativeTypes, UpdatableTypeRepository typeContainer){

		CompilerListener compilerListener = this.getCompilerListener();
		corePhase
			.add(new DesugarPhase(compilerListener))
			.add(new EnhancementPhase(compilerListener))
			.add(new ReificationPhase(compilerListener))
			.add(new ErasurePhase(compilerListener))
			.add(new NativePeersPhase(compilerListener,nativeTypes))
			.add(new JavalizePhase(compilerListener,nativeTypes, typeContainer));
			//.add(ir);

	}

	protected void createModuleArchive(FileLocations locations, ModuleNode module,Set<String> applications) throws IOException, FileNotFoundException {
		StringBuilder builder;
		Manifest jarManifest = new Manifest();
		jarManifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

		JavaCompilerBackEnd compilerBackEnd = javaCompilerBackEndFactory.create(locations);

		// add bootstrap class
		if (!applications.isEmpty()){

			if (applications.size() > 1){
				// TODO move to another phase
				throw new CompilationError("More than one Application was found. Every module can only have a maximum of 1 application");
			}
			String mainType = applications.iterator().next();

			int pos = mainType.lastIndexOf('.');
			String pack = mainType.substring(0, pos);

			builder = writeBootstrap(mainType, pack);

			var sourceFile = locations.getTargetFolder().folder(PackageSourcePathUtils.fromPackageName(pack)).file("Bootstrap.java");
			try(Writer writer = sourceFile.writer()){
				writer.write(builder.toString());
				writer.close();
			}

			compilerBackEnd.compile(sourceFile);

			jarManifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, pack + ".Bootstrap");

		}


		var file = locations.getModulesFolder().file(module.getName() + ".jar");

		Properties p = new Properties();

		p.put("module.name", module.getName());
		p.put("module.version", module.getVersion().toString());
		
		var requirements = module.getImports().getChildren(ModuleImportNode.class);
				
		if(!requirements.isEmpty()) {
			var buffer= new StringBuffer();
			for(var m : requirements) {
				if(buffer.length() >0) {
					buffer.append(";");
				}
				buffer.append(m.getQualifiedNameNode().toString()).append("@").append(m.getVersionNode().getVersion().toString());
			}
			
			p.put("module.requires", buffer.toString());
		}
	
		
		var moduleProperties = locations.getTargetFolder().file("module.properties");
		p.store(moduleProperties.outputStream(), "Lense module definition");

		createJar(locations.getTargetFolder(), file,jarManifest);
	}

	private StringBuilder writeBootstrap(String applicationType , String pack) {


		StringBuilder builder = new StringBuilder("package ").append(pack).append(";\n")
				.append("import lense.core.system.ConsoleApplication;\n")
				.append("import lense.core.collections.Array;\n")
				.append("public class Bootstrap {\n")
				.append("public static void main(String[] args) {\n")
				.append("		ConsoleApplication app = ").append(applicationType).append(".constructor();\n")
				.append("		app.setArguments(Array.fromNative(lense.core.lang.reflection.JavaReifiedArguments.getInstance().addType(lense.core.lang.String.TYPE_RESOLVER) ,args, s -> lense.core.lang.String.valueOfNative(s)));\n")
				.append("		app.onStart();\n")
				.append("	}")
				.append("}");


		return builder;
	}

	private void createJar(SourceFolder source, SourceFile output, Manifest manifest) throws IOException {

		JarOutputStream target = new JarOutputStream(output.outputStream(), manifest);

		String name = source.getPath().join("/") + "/";
		for (var nestedFile: source.children()){
			addToJar(name, nestedFile, target);
		}
		target.close();
	}

	private void addToJar(String base, SourceFileSystemNode source, JarOutputStream target) throws IOException
	{
		BufferedInputStream in = null;
		try
		{
			String name = source.getPath().join("/").substring(base.length());
			if (source.isFolder())
			{

				if (!name.isEmpty())
				{
					if (!name.endsWith("/")){
						name += "/";
					}

					JarEntry entry = new JarEntry(name);
					entry.setTime(source.lastModified());
					target.putNextEntry(entry);
					target.closeEntry();
				}
				
				for (var nestedFile: source.asFolder().children()){
					addToJar(base, nestedFile, target);
				}
				
				return;
			} else if (! (source.getName().endsWith(".class") || source.getName().endsWith(".properties"))){
				return;
			}

			JarEntry entry = new JarEntry(name);
			entry.setTime(source.lastModified());
			target.putNextEntry(entry);
			in = new BufferedInputStream(source.asFile().inputStream());


			byte[] buffer = new byte[1024];	
			while (true)
			{
				int count = in.read(buffer);
				if (count == -1){
					break;
				}
				target.write(buffer, 0, count);
			}
			target.closeEntry();
		}
		finally
		{
			if (in != null)
				in.close();
		}
	}


	protected Optional<SourceFile> resolveNativeFile(SourceFolder folder, String name) {
		return Optional.of(folder.file(name + ".class"));
	}

	protected void collectNative(FileLocations fileLocations, Map<String, NativeSourceInfo> nativeTypes) throws IOException {

		if (!fileLocations.getNativeFolder().exists()){
			return;
		}

		List<SourceFile> javaFiles = new LinkedList<>();
		Map<SourceFile, SourceFile> lenseFiles = new HashMap<>();

		var rootDir = fileLocations.getNativeFolder();
	    
		rootDir.walkTree(new SourceWalker(){

			@Override
			public SourceWalkerResult visitFile(SourceFile file) {
				if (file.getName().endsWith(".java")){
					javaFiles.add(file);
					var relativePath = fileLocations.getNativeFolder().getPath().relativize(file.getPath());
					
					var sourcePathName = relativePath.toString().replace(".java", ".lense").replace('>', File.separatorChar);
					var source = fileLocations.getSourceFolder().file(sourcePathName);
					if (source.exists()) {
						lenseFiles.put(file, source);
					}
					
				}
				return SourceWalkerResult.CONTINUE;
			}

		});

		if(javaFiles.isEmpty()) {
			return;
		}
		
		if (javaCompilerBackEndFactory.create(fileLocations).compile(javaFiles)){

			// compile all files
			outter: for (var n : javaFiles){
				
			
				var name = n.getName().substring(0,  n.getName().length() - 5);
				var source = resolveNativeFile(n.parentFolder(), name);

				try (var reader = new BufferedReader(new InputStreamReader(n.inputStream()))){
					if (!name.equals("Placeholder") && reader.lines().anyMatch(line -> line.startsWith("@Placeholder"))) {
						continue outter;
					}
				}
				
				var packagePath = rootDir.getPath().relativize(n.getPath()).getParent();
				var packageFolder = fileLocations.getTargetFolder().folder(packagePath);
				
				var target =  packageFolder.file(name  + ".class");

				target.parentFolder().ensureExists();

				if (source.isPresent() && !source.get().exists()){
					throw new CompilationError("Compiled file with java compiler does not exist (" + source.toString() +"). ");
				} else {
					source.get().moveTo(target);
					
				
					var lense = lenseFiles.get(n);
					nativeTypes.put(packagePath.join(".") + "." + name, new NativeSourceInfo(target, lense));

				}

			}


		} else {
			throw new CompilationError("Cannot compile source with java compile");
		}

	}
	
	@Override
	protected List<TypeDefinition> extactTypeDefinitionFromNativeType(
			UpdatableTypeRepository currentModuleRepository,
			Collection<NativeSourceInfo> nativeFiles
	) throws IOException {

  		var reader = new ByteCodeTypeDefinitionReader(currentModuleRepository);

  		return nativeFiles.stream().map(it -> it.nativeCompiledFile()).map(target -> {
			try {
				return reader.readNative(target);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).toList();
  		
	}

	@Override
	protected void applyCompilation(
			Map<String, NativeSourceInfo> nativeTypes, 
			FileLocations locations,
			CompositePhase corePhase,
			ModuleCompilationScopeTypeRepository currentModuleRepository, 
			final CompilerBackEnd backend,
			CompiledUnit unit
	) {
		if (unit != null){
			UnitTypes types = (UnitTypes)unit.getAstRootNode();

			for (ClassTypeNode type : types.getTypes()) {

				if (type.isNative()) {

					var nativeTypeFile = nativeTypes.get(type.getFullname());

					if (nativeTypeFile == null) {
						String[] name;
						if (type.getKind().isObject()) {

						    name = Strings.split(type.getFullname(), ".");
							name[name.length - 1 ] = Strings.cammelToPascalCase(name[name.length - 1 ]);
						}
						else 
						{
							name = Strings.split(type.getFullname(), ".");
						}
						
						nativeTypeFile =  new NativeSourceInfo(
								resolveNativeFile (locations.getTargetFolder(), Strings.join(name, File.separator))
								.orElseThrow(() ->  new CompilationError(type, "Expected native file for type " + type.getFullname()  + " does not exist")),
								null
						);
					
					}

					try {
						this.extactTypeDefinitionFromNativeType(currentModuleRepository, Arrays.asList(nativeTypeFile)).forEach(typeDef -> {
							
							typeDef = currentModuleRepository.registerType(typeDef, typeDef.getGenericParameters().size());

							type.setTypeDefinition((LenseTypeDefinition)typeDef);
							
						});

					

					} catch (IOException e1) {
						throw new RuntimeException(e1);
					}
				}
			}

			new CompilationResultSet(new CompilationResult(unit)).passBy(corePhase).sendTo(backend);

		}
	}

}
