
package thinclab.domain_lang;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Parser {

    private static Logger LOGGER =
        LogManager.getFormatterLogger(Parser.class);

    // Read file into a string
    public static String readFromFile(String filePath) {

        try {
            return Files.readString(Paths.get(filePath)); 
        }

        catch (Exception e) {
            LOGGER.error("While trying to read %s, %s", filePath, e);
            System.exit(-1);
            return null;
        }
    }

    // tokenize string
    public static List<String> tokenize(String codeString) {
        return new ArrayList<>(Arrays.asList(codeString
                    .replaceAll("[(]", " ( ")
                    .replaceAll("[)]", " ) ")
                    .strip()
                    .split("\\s+"))); 
    }

    static boolean isNumber(String s) {
        
        try {
            Float.valueOf(s);
            return true;
        }

        catch (NumberFormatException e) {
            return false;
        }
    }

    // convert to lists/symbols
    public static Object parse(List<String> line) {
        
        var term = line.remove(0);

        if (term.contentEquals("(")) {
            var list = new ArrayList<Object>();
            
            while(true) {

                var next = parse(line);
                if (next instanceof String s) {
                    if (s.contentEquals(")"))
                        break;
                }

                list.add(next);
            }

            return list;
        }

        else if (isNumber(term))
            return Float.valueOf(term);

        else
            return term;
    }
}
