
package thinclab.domain_lang;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.RandomVariable;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.Global;
import thinclab.utils.Tuple;
import thinclab.utils.types.Either;
import thinclab.utils.types.Left;
import thinclab.utils.types.Right;

public class Eval {

    private static final Logger LOGGER =
        LogManager.getFormatterLogger(Eval.class);

    // helpers
    public static Exception error(String msg) {
        return new Exception(msg);
    }

    private static List<String> castToStrings(Object obj) {

        if (obj instanceof List<?> l)
            return l.stream()
                .map(e -> (String) e)
                .collect(Collectors.toList());
        else
            return null;
    }

    // Evaluate a list
    public static Either<Exception, List<Object>> evalList(
            List<Object> l, 
            Environment env) {

        var args = l.stream()
            .map(a -> eval(a, env)).collect(Collectors.toList());

        var error = args.stream()
            .filter(a -> a instanceof Left)
            .findFirst();

        if (error.isPresent())
            return Left.of(error.get().left());

        else
            return Right.of(args.stream()
                    .map(a -> a.right())
                    .collect(Collectors.toList()));

            }

    // try and eval object as a reference
    public static Either<Exception, Object> evalRef(String s, 
            Environment env) {

        if (env.map.containsKey(s))
            return Right.of(env.map.get(s));

        else
            return Left.of(error(String.format("%s not defined", s)));
    }

    // eval a number as a DD
    public static Object evalNum(float n) {
        return DDleaf.getDD(n);
    }

    // add an object to the env
    public static Object addToEnv(String name, 
            Object obj, Environment env) {
        env.map.put(name, obj);
        return null;
    }

    // evaluate define statements
    public static Either<Exception, Object> evalDefine(List<?> l, 
            Environment env) {

        if (l.size() == 3) {
            if (l.get(1) instanceof String s) { 

                var r = eval(l.get(2), env);
                return (Either<Exception, Object>) 
                    r.rightMap(o -> addToEnv(s, o, env));
            }

            else
                return Left.of(
                        error(
                            String.format(
                                "%s not an identifier", l.get(0))));
        }

        else
            return Left.of(error("Incorrect define statement"));
    }

    public static Either<Exception, Object> evalDefVar(
            List<?> l, Environment env) {

        if (l.size() == 3) {

            if (l.get(1) instanceof String s 
                    && l.get(2) instanceof List<?> v) {

                var vals = v.stream()
                    .map(e -> (String) e)
                    .collect(Collectors.toList());

                env.vars.add(new RandomVariable(s, vals));
                return Right.of(null);
                    }

            else
                return Left.of(error("defvar format incorrect"));

        }

        else 
            return Left.of(error("Not enough args in defvar"));
            }

    // eval lambdas
    public static Either<Exception, Object> makeLambda(List<?> defn) {

        if (defn.size() == 3) {
            var args = castToStrings(defn.get(1));
            return Right.of(new Lambda(args, defn.get(2)));
        }

        else
            return Left.of(
                    error("Could not understand lambda definition"));
    }

    public static Either<Exception, Object> evalLambda(Object func, 
            List<Object> args, Environment env) {

        if (func instanceof Lambda l) 
            return l.eval(args, new Environment(env.vars, env.map));

        else if (Function.class.isInstance(func)) {
            var f = Function.class.cast(func);
            return Right.of(f.apply(args));
        }

        else
            return Left.of(
                    error(String.format(
                            "Could not evaluate %s", func)));
    }
    
    // eval DDs
    public static Either<Exception, Object> evalDDDef(Object parsed,
            Environment env) {


        
        return null;
    }


    // main eval loop
    public static Either<Exception, Object> eval(Object parsed, 
            Environment env) {

        if (parsed == null)
            return Right.of(null);

//        else if (parsed instanceof String s)
//            return evalRef(s, env);
//
//        else if (parsed instanceof DD n)
//            return Right.of(n);
//
//        else if (parsed instanceof Number n)
//            return Right.of(Float.valueOf((float) n));
//
        else if (parsed instanceof List l) {
            return null;

//            var fn = SpuddUtils.car(l);
//            var fargs = SpuddUtils.cdr(l);
//
//            // regular defines
//            if (fn.equals("define")) 
//                return evalDefine(l, env);
//
//            // random variables
//            else if (fn.equals("defvar"))
//                return evalDefVar(l, env);
//
//            // set random vars
//            else if (fn.equals("compile_vars")) {
//
//                Global.primeVarsAndInitGlobals(env.vars);
//                IntStream.range(0, Global.varNames.size())
//                    .mapToObj(i -> 
//                            Tuple.of(Global.varNames.get(i), i + 1))
//                    .forEach(t -> addToEnv(t._0(), t._1(), env));
//
//                return Right.of(Global.varNames);
//            }
//
//            // lambda
//            else if (fn.equals("\\")) 
//                return makeLambda(l);
//
//            // java method
//            else if (fn.equals("java-method")) {
//
//                l.remove(0);
//
//                if (l.size() >= 2) {
//                    var className = (String) l.remove(0);
//                    var method = (String) l.remove(0);
//
//                    var types = l.size() > 0 ? l : List.of();
//
//                    return JavaMethod.makeMethod(
//                            className, method, types);
//                }
//
//                else return Left.of(
//                        error("java-mathod not defined properly"));
//            }
//
//            else {
//
//                var f = eval(fn, env);
//                Either<Exception, List<Object>> args = 
//                    evalList(fargs, env);
//
//                if (f instanceof Left)
//                    return f;
//
//                else if (args instanceof Left)
//                    return Left.of(args.left());
//
//                else
//                    return (Either<Exception, Object>) 
//                        evalLambda(f.right(), args.right(), env);
//            }
        }

        else {
            return Left.of(
                    error(String.format(
                            "Did not understand %s", parsed)));
        }
    }
}
