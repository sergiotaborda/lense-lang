package lense.compiler.asm;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;

import compiler.filesystem.SourceFile;
import lense.compiler.repository.UpdatableTypeRepository;
import lense.compiler.type.LenseTypeDefinition;
import lense.compiler.type.TypeDefinition;

public class ByteCodeTypeDefinitionReader {

    private final UpdatableTypeRepository typeContainer;
    
	public ByteCodeTypeDefinitionReader(UpdatableTypeRepository typeContainer){
	    this.typeContainer = typeContainer;
	}
	
	public TypeDefinition readNative(SourceFile classFile) throws IOException {	
		if (classFile == null) {
			throw new IllegalArgumentException("Class file cannot be null");
		}
		return readNative(classFile.inputStream());
	}
	
	public LenseTypeDefinition readNative(InputStream input) throws IOException {
		
		// TODO need access to TypeResolver.
		
		ByteCodeReader cp = new ByteCodeReader(typeContainer);
		ClassReader cr = new ClassReader(input);
		cr.accept(cp, 0);
		
		
		return cp.getBuilder().build();

	}
	

}
