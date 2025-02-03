package thinclab.domain_parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.RandomVariable;
import thinclab.legacy.Global;

public class Interpreter extends InterpreterUtils {

    public HashMap<String, Object> env = new HashMap<>();

    private static Logger LOGGER =
        LogManager.getFormatterLogger(Interpreter.class);

    public Object evalList(Cons list) {
        if (list.obj == null)
            return list;
        else
            return cons(eval(list.obj), (Cons) evalList(list.next));
    }

    private RandomVariable makeRVar(Cons varDecl) {
        String varName = (String) car(varDecl);
        var vals = flattenCons((Cons) car(cdr(varDecl))).stream()
            .map(v -> (String) v).collect(Collectors.toList());

        return new RandomVariable(varName, vals);
    }

    public void evalVars(Cons varList) {

        var rvarList = new ArrayList<RandomVariable>();
        while (varList != null) {
            rvarList.add(makeRVar((Cons) car(varList)));
            varList = cdr(varList);
        }

        Global.primeVarsAndInitGlobals(rvarList);

        for (int i = 0; i < Global.varNames.size(); i++)
            env.put(Global.varNames.get(i), i);
    }

    public Object evalStmt(Cons list) {

        if (list.obj == null)
            return null;

        if (!(list.obj instanceof String))
            throw new RuntimeException(
                    String.format("%s is not a valid statement", list));

        String first = (String) car(list);
        Cons rest = cdr(list);

        // defines
        if (first.equals("def")) {
            if (rest.obj instanceof String name)
                def(name, eval(car(cdr(rest))), env);

            else
                throw new RuntimeException(
                        String.format("%s not a valid name", rest));
        }

        // random variables
        if (first.equals("vars")) {
            evalVars(rest);
        }
        
        // scope
        if (first.equals("scope")) {
            return scope(env);
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
