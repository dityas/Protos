package thinclab.domain_parser;


public class AssocList {
    /*
     * Just an associative list to use as a hashmap for the parser
     */

    public AssocList parent = null;
    public Cons vars = null;
    public Cons vals = null;

    public AssocList() {}

    public AssocList(AssocList parent) {
        this.parent = parent;
    }

    public boolean containsKey(String varName) {

        var varHead = vars;
        var valHead = vals;

        while (varHead != null) {

            if (varHead.obj.equals(varName))
                return true;

            varHead = varHead.next;
            valHead = valHead.next;
        }

        if (this.parent != null)
            return this.parent.containsKey(varName);

        return false;
    }

    public void put(String varName, Object val) {

        if (vars == null) {
            vars = new Cons(varName, null);
            vals = new Cons(val, null);
        }
        
        else {
            var varHead = vars;
            var valHead = vals;

            while (varHead.next != null) {
                varHead = varHead.next;
                valHead = valHead.next;
            }

            varHead.next = new Cons(varName, null);
            valHead.next = new Cons(val, null);
        }
    }

    public void putAll(Cons varNames, Cons values) {

        if (varNames.length() != values.length()) {
            var error = String.format("Unequal kv pairs, %s & %s",
                    varNames, values);
            throw new RuntimeException(error);
        }
        
        if (vars == null) {
            vars = varNames;
            this.vals = values;
        }
        
        else {
            var varHead = vars;
            var valHead = vals;

            while (varHead.next != null) {
                varHead = varHead.next;
                valHead = valHead.next;
            }

            varHead.next = varNames;
            valHead.next = values;
        }
    }

    public Object get(String varName) {

        var varHead = vars;
        var valHead = vals;

        while (varHead != null) {

            if (varHead.obj.equals(varName))
                return valHead.obj;

            varHead = varHead.next;
            valHead = valHead.next;
        }

        if (this.parent != null)
            return this.parent.get(varName);

        return null;
    }

    @Override
    public String toString() {
        return new Cons(vars, new Cons(vals, null)).toString();
    }
}
