package thinclab.domain_parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InterpreterUtils {

    // Primitive functions
    
    public static List<Object> flattenCons(Cons list) {

        var objList = new ArrayList<Object>();
        
        while (list != null) {
            objList.add(list.obj);
            list = list.next;
        }

        return objList;
    }

    public static Cons scope(HashMap<String, Object> env) {

        var list = cons("env", null);
        var head = list;

        for (var name: env.keySet()) {
            list.next = cons(cons(name, cons(env.get(name), null)), null);
            list = list.next;
        }

        return head;
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
            HashMap<String, Object> env) {
        env.put(name, obj);
    }

    // End primitive functions
}
