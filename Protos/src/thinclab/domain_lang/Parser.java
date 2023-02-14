
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

    public static Object parse(List<String> tokens) {

        var token = tokens.remove(0);
        
        // If it's a list, parse it here as a Java ArrayList<>
        if (token.equals("(")) {
            var parsed = new ArrayList<Object>();
            
            // Till ")" is encountered
            while (true) {

                var _parsed = parse(tokens);
                if (_parsed == null)
                    break;

                else
                    parsed.add(_parsed);
            }

            return parsed;
        }

        else if (token.equals(")"))
            return null;

        else if (isNumber(token))
            return Float.valueOf(token);

        else
            return token;
    }
}
