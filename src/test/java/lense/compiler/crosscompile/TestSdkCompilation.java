/**
 * 
 */
package lense.compiler.crosscompile;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.Test;

import lense.compiler.LenseCompiler;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.repository.ModuleRepository;
import lense.compiler.repository.TypeRepository;
import lense.compiler.repository.Version;
import lense.compiler.type.TypeDefinition;
import lense.compiler.typesystem.TypeSearchParameters;

/**
 * 
 */
public class TestSdkCompilation {


	@Test 
	public void testCompileLibrary() throws IOException {
		File folder = new File(new File(".").getAbsoluteFile().getParentFile(), "src/main/sdk/");


		TypeRepository repo = new TypeRepository(){

			@Override
			public Optional<TypeDefinition> resolveType(TypeSearchParameters filter) {
				return Optional.empty();
			}

			@Override
			public List<ModuleRepository> resolveModuleByName(QualifiedNameNode qualifiedNameNode) {
				return Collections.emptyList();
			}

			@Override
			public Optional<ModuleRepository> resolveModuleByNameAndVersion(QualifiedNameNode qualifiedNameNode,
					Version version) {
				return Optional.empty();
			}

		};

		new LenseCompiler(repo).compileModuleFromDirectory(folder);
	}


}
