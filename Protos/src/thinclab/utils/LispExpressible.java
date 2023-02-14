
package thinclab.utils;

import java.util.List;

import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;

public interface LispExpressible {

    public Object toLisp();

    public static String toString(Object obj) {
        return toString(obj, "");
    }

    public static String toString(Object s_expr, String spaces) {

        if (s_expr instanceof String strObject)
            return strObject;

        else if (s_expr instanceof Number numObject)
            return numObject.toString();

        else if (s_expr instanceof List<?> listObject) {

            var builder = new StringBuilder();
            builder.append("(");

            for (Object obj: listObject) {

                if (obj instanceof List<?>)
                    builder.append("\r\n\t").append(spaces);

                builder.append(" ");
                builder.append(LispExpressible.toString(obj, spaces.concat("\t")));
            }

            builder.append(")");

            return builder.toString();
        }

        else 
            return String.format("(error %s)", s_expr.toString());
    }
}
