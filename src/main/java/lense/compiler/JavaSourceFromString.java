package lense.compiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

class JavaSourceFromString extends SimpleJavaFileObject {
	final String code;
	private File output;

	JavaSourceFromString(String name, String code, File output) {
		super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension),Kind.SOURCE);
		this.code = code;
		this.output = output;
	}

	public long getLastModified(){
		return System.currentTimeMillis();
	}
	
	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return code;
	}
	
	@Override
	public OutputStream openOutputStream() throws IOException{
		return new FileOutputStream(output);
	}
	
	@Override
	public Writer openWriter() throws IOException{
		return new FileWriter(output);
	}
}