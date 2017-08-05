/**
 * 
 */
package lense.compiler.crosscompile;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import compiler.CompilerListener;
import compiler.CompilerMessage;
import lense.compiler.ast.LenseCompilerListener;
import lense.compiler.ast.QualifiedNameNode;
import lense.compiler.crosscompile.java.LenseToJavaCompiler;
import lense.compiler.crosscompile.javascript.LenseToJsCompiler;
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
		File folder = new File(new File(".").getAbsoluteFile().getParentFile(), "/lense/sdk/");


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

		new LenseToJavaCompiler(repo)
		.setCompilerListener(LenseCompilerListener.error(msg -> fail(msg.getMessage())))
		.compileModuleFromDirectory(folder);
	}

	@Test 
    public void testCompileLibraryJavascript() throws IOException {
        File folder = new File(new File(".").getAbsoluteFile().getParentFile(), "/lense/sdk/");


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

        new LenseToJsCompiler(repo)
        .setCompilerListener(LenseCompilerListener.error(msg -> fail(msg.getMessage())))
        .compileModuleFromDirectory(folder);
    }

}
