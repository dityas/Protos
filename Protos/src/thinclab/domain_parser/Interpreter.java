package thinclab.domain_parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Interpreter {

    public HashMap<String, Object> env = new HashMap<>();

    private static Logger LOGGER =
        LogManager.getFormatterLogger(Interpreter.class);

    public Object evalList(Cons list) {
        if (list.obj == null)
            return list;
        else
            return InterpreterUtils.cons(eval(list.obj),
                    (Cons) evalList(list.next));
    }

    public void evalRVar() {
    }

    public Object evalStmt(Cons list) {

        if (list.obj == null)
            return null;

        if (!(list.obj instanceof String))
            throw new RuntimeException(
                    String.format("%s is not a valid statement", list));

        String first = (String) InterpreterUtils.car(list);
        Cons rest = InterpreterUtils.cdr(list);

        // defines
        if (first.equals("def")) {
            if (rest.obj instanceof String name)
                InterpreterUtils.def(name,
                        eval(InterpreterUtils.car(InterpreterUtils.cdr(rest))),
                        env);

            else
                throw new RuntimeException(
                        String.format("%s not a valid name", rest));
        }

        // random variables
        if (first.equals("rvar")) {
            if (rest.obj instanceof String name) {

            }

            else
                throw new RuntimeException(
                        String.format("%s not a valid name", rest));
        }
        
        // scope
        if (first.equals("scope")) {
            return InterpreterUtils.scope(env);
        }

        return null;
    }

    public Object eval(Object statement) {

        if (statement instanceof String symbol) {

            if (!env.containsKey(symbol))
                throw new RuntimeException(
                        String.format("%s not defined", symbol));

            else return env.get(symbol);
        }

        else if (statement instanceof Float num)
            return num;

        else if (statement instanceof Cons list)
            return evalStmt(list);

        return null;
    }

    public void repl() {

        var parser = new Parser(System.in);

        while (true) {
            System.out.print(">>> ");
            System.out.println(eval(parser.parse()));
        }
    }

}
