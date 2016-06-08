package lense.compiler.asm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.objectweb.asm.ClassReader;

import lense.compiler.type.TypeDefinition;

public class ByteCodeTypeDefinitionReader {

	
	public ByteCodeTypeDefinitionReader(){}
	
	public TypeDefinition readNative(File classFile) throws IOException {
		
		ByteCodeReader cp = new ByteCodeReader();
		ClassReader cr = new ClassReader(new FileInputStream(classFile));
		cr.accept(cp, 0);
		

		return cp.getType();
	}
}
