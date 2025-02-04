package thinclab.domain_parser;

import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.legacy.DDleaf;

public class Parser {
    /*
     * Parses domain files and DDs as S expressions
     */

    private InputStream in;
    private int currentChar = -1;

    private static Logger LOGGER =
        LogManager.getFormatterLogger(Parser.class);

    public Parser(InputStream stream) {
        this.in = stream;
    }
    
    public int next() {
        try {
            if (currentChar != -1) {
                int c = currentChar;
                currentChar = -1;
                return c;
            }

            else return in.read();
        }
        catch (Exception e) {
            LOGGER.error("Stream exception %s", e);
            System.exit(-1);
        }

        return -1;
    }

    public String getNextToken() {

        var expr = new StringBuilder();

        while (true) {
            var c = next();

            if (c == -1)
                break;

            else if (Character.isWhitespace(c))
                continue;

            else if (c == '(' || c == ')') {
                expr.append((char) c);
                break;
            }

            // single line comment
            else if (c == ';') {
                while (next() != '\n') {}
            }

            else {
                expr.append((char) c);
                while (true) {
                    var s = next();
                    if (s == -1 || s == '(' || s == ')') {
                        currentChar = s;
                        break;
                    }

                    else if (Character.isWhitespace(s))
                        break;

                    else
                        expr.append((char) s);
                }

                break;
            }
        }

        return expr.toString();
    }

    private boolean isNumber(String num) {
        try {
            Float.parseFloat(num);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Object parse() {

        String token = getNextToken();

        if (isNumber(token))
            return DDleaf.getDD(Float.parseFloat(token));

        else if (token.equals("(")) {
            
            Cons head = new Cons(null, null);
            var cons = head;

            while (true) {

                var parsed = parse();

                if (parsed == null) {
                    cons.next = null;
                    break;
                }

                else if (cons.obj == null)
                    cons.obj = parsed;

                else {
                    cons.next = new Cons(parsed, null);
                    cons = cons.next;
                }
            }

            return head;
        }

        else if (token.equals(")") || token.length() < 1)
            return null;
        
        return token;
    }
}
