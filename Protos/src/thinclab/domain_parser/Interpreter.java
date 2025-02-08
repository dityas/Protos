package thinclab.domain_parser;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.RandomVariable;
import thinclab.legacy.DDleaf;
import thinclab.legacy.Global;

public class Interpreter extends InterpreterUtils {

    public static Logger LOGGER =
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
            env.put(Global.varNames.get(i), i + 1);
    }

    public static Object evalStmt(Cons list, AssocList env) {
        if (list.obj == null)
            return null;

        if (!(list.obj instanceof String)) {
            var fname = eval(car(list), env);
            var params = evalList(cdr(list), env);

            if (fname instanceof Closure cname)
                return cname.eval((Cons) params, env);

            else throw new RuntimeException(
                    String.format("Cannot eval %s", list));
        }

        String first = (String) car(list);
        Cons rest = cdr(list);

        // start
        if (first.equals("start")) {
            Object res = null;
            while (rest != null) {
                res = eval(rest.obj, env);
                rest = cdr(rest);
            }

            return res;
        }

        // defines
        else if (first.equals("def")) {
            if (rest.obj instanceof String name)
                def(name, eval(car(cdr(rest)), env), env);

            else
                throw new RuntimeException(
                        String.format("%s not a valid name", rest));

            return null;
        }

        // random variables
        else if (first.equals("vars")) {
            evalVars(rest, env);
            return null;
        }

        // if statement
        else if (first.equals("if")) {

            var cond = car(rest);
            var thenStmt = car(cdr(rest));
            var elseStmt = car(cdr(cdr(rest)));

            return (eval(cond, env).equals(true)) ? eval(thenStmt, env) : eval(elseStmt, env);
        }
        
        // load files
        else if (first.equals("load")) {
            String filename = (String) car(rest);
            try {
                Parser fParser = new Parser(new FileInputStream(filename));
                return eval(fParser.parse(), env);
            } catch (Exception e) {
                LOGGER.error("Could not load %s: %s", filename, e.getMessage());
                return null;
            }
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

        // static java method 
        else if (first.equals("jmethod")) {
            var method = InterpreterUtils.jmethod(rest);
            return method;
        }

        // cons list 
        else if (first.equals("list")) {
            return evalList(rest, env);
        }

        // dd cons 
        else if (first.equals("dd")) {
            return evalDD(rest, env);
        }

        // exit
        else if (first.equals("exit")) {
            System.exit(0);
            return null;
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

    }

    public static Object eval(Object statement, AssocList env) {

        if (statement instanceof String symbol) {

            if (!env.containsKey(symbol))
                throw new RuntimeException(
                        String.format("%s not defined", symbol));

            else return env.get(symbol);
        }

        else if (statement instanceof DDleaf dd)
            return dd;

        else if (statement instanceof Cons list)
            return evalStmt(list, env);

        else throw new RuntimeException(
                String.format("Could not eval %s", statement));
    }

    public static AssocList evalStream(InputStream stream) {

        try {

            // load std lib
            var env = new AssocList();
            var stdParser = new Parser(new ByteArrayInputStream(InterpreterUtils.std.getBytes()));
            eval(stdParser.parse(), env);

            // eval input stream
            var parser = new Parser(stream);
            eval(parser.parse(), env);
            return env;

        } catch (Exception e) {
            LOGGER.error("Could not parse stream: %s", e.getMessage());
            return null;
        }
    }

    public static void repl() throws Exception {

        var env = new AssocList();
        var stdParser = new Parser(new ByteArrayInputStream(InterpreterUtils.std.getBytes()));
        eval(stdParser.parse(), env);

        var parser = new Parser(System.in);

        while (true) {
            System.out.print(">>> ");
            System.out.println(eval(parser.parse(), env));
        }
    }

}
