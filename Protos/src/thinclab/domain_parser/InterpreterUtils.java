package thinclab.domain_parser;

import java.util.ArrayList;
import java.util.List;

import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;

public class InterpreterUtils {

    // Primitive functions

    public static String std = new StringBuilder()
        .append("(start\r\n")
        .append("(def cons (jmethod thinclab.domain_parser.InterpreterUtils cons java.lang.Object thinclab.domain_parser.Cons))\r\n")
        .append("(def alist (jmethod thinclab.domain_parser.InterpreterUtils flattenCons thinclab.domain_parser.Cons))\r\n")
        .append("(def uniform (jmethod thinclab.legacy.DDnode getUniformDist int))\r\n")
        .append("(def + (jmethod thinclab.DDOP add thinclab.legacy.DD thinclab.legacy.DD))\r\n")
        .append("(def * (jmethod thinclab.DDOP mult thinclab.legacy.DD thinclab.legacy.DD))\r\n")
        .append("(def / (jmethod thinclab.DDOP div thinclab.legacy.DD thinclab.legacy.DD))\r\n")
        .append("(def - (jmethod thinclab.DDOP sub thinclab.legacy.DD thinclab.legacy.DD))\r\n")
        .append(")\r\n")
        .toString();

    public static DD dd(int varIndex, Object rest) {

        return null;
    }

    public static DD evalDD(Cons list, AssocList env) {

        String varName = (String) list.obj;
        Cons valDefs = cdr(list);

        int ddVar = Global.varNames.indexOf(varName) + 1;
        var valNames = Global.valNames.get(ddVar - 1);
        DD[] ddVals = new DD[valNames.size()];
        for (int i = 0; i< ddVals.length; i++)
            ddVals[i] = DDleaf.getDD(0.0f);

        while (valDefs != null) {

            Cons valDef = (Cons) car(valDefs);
            String valName = (String) car(valDef);

            // Recursively parse rest
            DD ddVal = (DD) Interpreter.eval(car(cdr(valDef)), env);

            // Place parsed DD at proper index
            var valIndex = valNames.indexOf(valName);
            ddVals[valIndex] = ddVal;

            valDefs = cdr(valDefs);
        }

        return DDnode.getDD(ddVar, ddVals);
    }

    public static Object type(Object obj) {
        return obj.getClass();
    }
    
    public static List<Object> flattenCons(Cons list) {

        var objList = new ArrayList<Object>();
        
        while (list != null) {
            objList.add(list.obj);
            list = list.next;
        }

        return objList;
    }

    public static Cons scope(AssocList env) {
        return env.vars;
    }

    public static Object car(Cons list) {
        return list.obj;
    }

    public static Cons cdr(Cons list) {
        return list.next;
    }
    
    public static Cons cons(Object obj, Cons rest) {
        return new Cons(obj, rest);
    }

    public static void def(String name, Object obj,
            AssocList env) {
        env.put(name, obj);
    }

    public static NativeFunc jmethod(Cons def) {
        try {
            var className = (String) car(def);
            var methodName = (String) car(cdr(def));
            var argTypes = flattenCons(cdr(cdr(def)));

            return NativeFunc.getNativeFunc(className, methodName, argTypes);
        } catch (Exception e) {
            Interpreter.LOGGER.error("Could not make %s: %s", def, e);
            return null;
        }
    }

    // End primitive functions
}
