
package thinclab.domain_lang;

import java.io.StreamTokenizer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DomainLangInterpreter {

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(DomainLangInterpreter.class);

    public static List<String> readFromFile(String filePath) {

        try {
            return Arrays.asList(Files.readString(Paths.get(filePath))
                    .replaceAll("[(]", " ( ")
                    .replaceAll("[)]", " ) ")
                    .strip()
                    .split("\\s+")); 
        }

        catch (Exception e) {
            LOGGER.error("While trying to read %s, %s", filePath, e);
            System.exit(-1);
            return null;
        }
    }

    // Read loop for the interpreter
    public static Object read(List<String> tokens) {

        // parse as a list
        if (tokens.get(0).contentEquals("(")) {
        
        }

        return null;
    }

    public static Object parseLispObject(StreamTokenizer tokenizer) {

        if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
            LOGGER.debug("Parsed number %s", tokenizer.nval);
            return Float.valueOf((float) tokenizer.nval);
        }

        else if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
            LOGGER.debug("Parsed word %s", tokenizer.sval);
            return tokenizer.sval;
        }

        else if (tokenizer.ttype == '(') {

            var statement = new ArrayList<Object>();

            try {
                while (tokenizer.nextToken() != ')') {
                    statement.add(parseLispObject(tokenizer));
                }
            }

            catch (Exception e) {
                LOGGER.error("Error while parsing %s", e);
                System.exit(-1);
            }

            LOGGER.debug("Parsed statement %s", statement);
            return statement;
        }

        else {
            LOGGER.error("Could not understand token %s", tokenizer);
            System.exit(-1);
            return null;
        }

    }

    public static void repl(StreamTokenizer tokenizer) {
        while(true) {

            if (tokenizer.ttype == StreamTokenizer.TT_EOF)
                break;
        }
    }
}
