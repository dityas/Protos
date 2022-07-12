
package thinclab.utils;

import java.util.List;

import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;

public interface LispExpressible {

    public Object toLisp();

    public static String toString(Object s_expr) {

        if (s_expr instanceof String strObject)
            return strObject;

        else if (s_expr instanceof Number numObject)
            return numObject.toString();

        else if (s_expr instanceof DDnode ddNode) {

            var builder = new StringBuilder();
            builder.append("[")
                .append(Global.varNames.get(ddNode.getVar() - 1));

            for (int i = 0; i < ddNode.getChildren().length; i++) {

                var child = ddNode.getChildren()[i];

                if (child instanceof DDleaf ddLeaf) {

                    if (ddLeaf.getVal() != 0.0f) 
                        builder.append(" ");

                }

                else 
                    builder.append("\r\n\t");

                builder.append("[")
                    .append(Global.valNames.get(
                                ddNode.getVar() - 1).get(i))
                    .append(" ")
                    .append(LispExpressible.toString(child))
                    .append("]");

            }

            builder.append("]");
            return builder.toString();
        }

        else if (s_expr instanceof DDleaf ddLeaf) 
            return Float.valueOf(ddLeaf.getVal()).toString();

        else if (s_expr instanceof List<?> listObject) {

            var builder = new StringBuilder();
            builder.append("(");

            for (Object obj: listObject) {

                if (obj instanceof List<?>)
                    builder.append("\r\n\t");

                builder.append(" ");
                builder.append(LispExpressible.toString(obj));
            }

            builder.append(")");

            return builder.toString();
        }

        else 
            return String.format("(error %s)", s_expr.toString());
    }
}
