package thinclab.domain_parser;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.RandomVariable;
import thinclab.legacy.Global;

public class Interpreter extends InterpreterUtils {

    private static Logger LOGGER =
        LogManager.getFormatterLogger(Interpreter.class);

    public static Object evalList(Cons list, AssocList env) {
        if (list == null || list.obj == null)
            return list;
        else
            return cons(eval(list.obj, env), (Cons) evalList(list.next, env));
    }

    private static RandomVariable makeRVar(Cons varDecl) {
        String varName = (String) car(varDecl);
        var vals = flattenCons((Cons) car(cdr(varDecl))).stream()
            .map(v -> (String) v).collect(Collectors.toList());

        return new RandomVariable(varName, vals);
    }

    public static void evalVars(Cons varList, AssocList env) {

        var rvarList = new ArrayList<RandomVariable>();
        while (varList != null) {
            rvarList.add(makeRVar((Cons) car(varList)));
            varList = cdr(varList);
        }

        Global.primeVarsAndInitGlobals(rvarList);

        for (int i = 0; i < Global.varNames.size(); i++)
            env.put(Global.varNames.get(i), i);
    }

    public static Object evalStmt(Cons list, AssocList env) {
        if (list.obj == null)
            return null;

        if (!(list.obj instanceof String))
            throw new RuntimeException(
                    String.format("%s is not a valid statement", list));

        String first = (String) car(list);
        Cons rest = cdr(list);

        // start
        if (first.equals("start")) {
            while (rest != null) {
                eval(rest.obj, env);
                rest = cdr(rest);
            }
        }

        // defines
        else if (first.equals("def")) {
            if (rest.obj instanceof String name)
                def(name, eval(car(cdr(rest)), env), env);

            else
                throw new RuntimeException(
                        String.format("%s not a valid name", rest));
        }

        // random variables
        else if (first.equals("vars")) {
            evalVars(rest, env);
        }
        
        // scope
        else if (first.equals("scope")) {
            return scope(env);
        }

        // scope
        else if (first.equals("print")) {
            var toPrint = eval(car(rest), env);
            System.out.println(toPrint);
            return null;
        }

        // lambda 
        else if (first.equals("\\")) {
            var params = (Cons) car(rest);
            var body = car(cdr(rest));
            var func = new Closure(params, body);
            return func;
        }

        // exit
        else if (first.equals("exit")) {
            System.exit(0);
        }

        else if (env.containsKey(first)) {

            var fname = env.get(first);
            var params = evalList(rest, env);

            if (fname instanceof Closure cname)
                return cname.eval((Cons) params, env);

            else throw new RuntimeException(
                    String.format("%s is not a defined function", first));
        }

        else throw new RuntimeException(
                String.format("Could not eval %s", first));

        return null;
    }

    public static Object eval(Object statement, AssocList env) {

        if (statement instanceof String symbol) {

            if (!env.containsKey(symbol))
                throw new RuntimeException(
                        String.format("%s not defined", symbol));

            else return env.get(symbol);
        }

        else if (statement instanceof Float num)
            return num;

        else if (statement instanceof Cons list)
            return evalStmt(list, env);

        return null;
    }

    public static void repl() {

        var parser = new Parser(System.in);
        var env = new AssocList();

        while (true) {
            System.out.print(">>> ");
            System.out.println(eval(parser.parse(), env));
        }
    }

}
