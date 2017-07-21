package lense.compiler.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import lense.compiler.Arguments;

public class TestArgumentsParser {

    @Test
    public void testArgumentsParserWithMode() {

        String source = "hello/world";
        
        Arguments args = new ArgumentParser().parse("compile:java", "--source=" + source);
        
        assertEquals(LenseCommand.COMPILE, args.getCommand());
        assertEquals(LenseCommand.Mode.JAVA, args.getMode().get());
        assertTrue(args.getParameter(LenseCommand.Parameter.SOURCE).isPresent());
        assertEquals(source, args.getParameter(LenseCommand.Parameter.SOURCE).get());
    }
}
