
package thinclab.domain_lang;

import java.util.ArrayList;
import java.util.List;

public class SpuddUtils {

    public static Object car(List<Object> l) {
        return l.size() > 0 ? l.get(0) : null;
    }

    public static List<Object> cdr(List<Object> l) {
        return l.size() > 1 ? 
            new ArrayList<Object>(l.subList(1, l.size())) : null;
    }
}
