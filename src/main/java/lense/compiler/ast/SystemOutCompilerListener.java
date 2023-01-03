package lense.compiler.ast;

import compiler.CompilerMessage;

public class SystemOutCompilerListener extends LenseCompilerListener{

    @Override
    public void error(CompilerMessage message) {
        System.err.println("[ERROR]" + message.getMessage());
       // throw new RuntimeException(message.getMessage());
    }

    @Override
    public void warn(CompilerMessage message) {
        System.out.println("[WARN]" + message.getMessage());
    }

    @Override
    public void trace(CompilerMessage message) {
        System.out.println("[TRACE]" + message.getMessage());
    }
}
