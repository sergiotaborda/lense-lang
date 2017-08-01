package lense.compiler.ast;

import java.util.function.Consumer;

import compiler.CompilerListener;
import compiler.CompilerMessage;

public class LenseCompilerListener implements CompilerListener {

    public static LenseCompilerListener error(Consumer<CompilerMessage> consumer){
        return new LenseCompilerListener(){
            @Override
            public void error(CompilerMessage message) {
                consumer.accept(message);
            }
        };
    }
    
    public LenseCompilerListener (){}
    
    
    @Override
    public void start() {
        // no-op
    }

    @Override
    public void error(CompilerMessage message) {
        // no-op
    }

    @Override
    public void warn(CompilerMessage message) {
        // no-op
    }

    @Override
    public void trace(CompilerMessage message) {
        // no-op
    }

    @Override
    public void end() {
        // no-op
    }

}
