package lense.compiler;


import java.io.File;

import lense.compiler.repository.ClasspathRepository;

public class Lense {

	public static void main(String[] args) {

		Arguments arguments = new ArgumentParser().parse(args);

		switch (arguments.getCommand()){
		case  COMPILE:
			compile(arguments);
			break;
		case RUN:
			run(arguments);
			break;
		default:
			help();
		}
	}

	private static void help() {
		println("lense compile|run ");
	}

	private static void run(Arguments arguments) {
		// TODO Auto-generated method stub

	}

	private static void compile(Arguments arguments) {
		
		File base = new File(new File(".").getAbsoluteFile().getParentFile(), arguments.getRepositoryBase());
		ClasspathRepository repo = new ClasspathRepository(base);

		println("Using repository at " + base);
		
		File moduleproject = new File(new File(".").getAbsoluteFile().getParentFile(), arguments.getSource());
		
		println("Compiling at " + moduleproject);
		
		long time = System.currentTimeMillis();
		
		final LenseCompiler compiler = new LenseCompiler(repo);
		compiler.compileModuleFromDirectory(moduleproject);
		
		long elapsed = System.currentTimeMillis() - time;
		
		println("Compilation ended ( " + elapsed + " ms)");
	}

	private static void println(String text) {
		System.out.println(text);
	}

}
