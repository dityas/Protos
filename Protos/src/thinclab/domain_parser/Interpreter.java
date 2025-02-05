package thinclab.domain_parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.RandomVariable;
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;

public class Interpreter extends InterpreterUtils {

    private static Logger LOGGER =
        LogManager.getFormatterLogger(Interpreter.class);

    public static void loadStd(AssocList env) throws Exception {

        // cons
        var cons = InterpreterUtils.class.getDeclaredMethod(
                "cons", Object.class, Cons.class);
        env.put("cons", new NativeFunc(cons));

        // type
        var type = InterpreterUtils.class.getDeclaredMethod(
                "type", Object.class);
        env.put("type", new NativeFunc(type));

        // uniform
        var uniform = InterpreterUtils.class.getDeclaredMethod(
                "uniform", Integer.class);
        env.put("uniform", new NativeFunc(uniform));
    }

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

        if (!(list.obj instanceof String))
            throw new RuntimeException(
                    String.format("%s is not a valid statement", list));

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

    public static void repl() throws Exception {

        var parser = new Parser(System.in);
        var env = new AssocList();
        loadStd(env);

        while (true) {
            System.out.print(">>> ");
            System.out.println(eval(parser.parse(), env));
        }
    }

}
