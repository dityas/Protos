package thinclab.domain_parser;

import java.util.ArrayList;
import java.util.List;

import thinclab.legacy.DD;
import thinclab.legacy.DDnode;

public class InterpreterUtils {

    // Primitive functions
    
    public static DD uniform(Integer varIndex) {
        return DDnode.getUniformDist((int) varIndex);
    }

    public static DD dd(int varIndex, Object rest) {

        return null;
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

    // End primitive functions
}
