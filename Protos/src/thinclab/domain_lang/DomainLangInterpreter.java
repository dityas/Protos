
package thinclab.domain_lang;

import java.io.StreamTokenizer;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DomainLangInterpreter {

    public static Environment env = new Environment();

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(DomainLangInterpreter.class);

    public static void run(List<String> line) {

        while(!line.isEmpty()) {
            
            LOGGER.debug("Parsed: %s", Parser.parse(line));
        }
    }

    // Read loop for the interpreter
    public static Object read(List<String> tokens) {

        // parse as a list
        if (tokens.get(0).contentEquals("(")) {
        
        }

        return null;
    }

    public static void repl(StreamTokenizer tokenizer) {
        while(true) {

            if (tokenizer.ttype == StreamTokenizer.TT_EOF)
                break;
        }
    }
}
