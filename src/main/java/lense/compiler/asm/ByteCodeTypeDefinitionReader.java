package lense.compiler.asm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;

import lense.compiler.type.TypeDefinition;

public class ByteCodeTypeDefinitionReader {

	
	public ByteCodeTypeDefinitionReader(){}
	
	public TypeDefinition readNative(File classFile) throws IOException {	
		return readNative(new FileInputStream(classFile));
	}
	
	public TypeDefinition readNative(InputStream input) throws IOException {
		
		// TODO need access to TypeResolver.
		
		ByteCodeReader cp = new ByteCodeReader();
		ClassReader cr = new ClassReader(input);
		cr.accept(cp, 0);
		

		return  cp.getType();
		
	}
	

}
