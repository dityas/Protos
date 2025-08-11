package thinclab.domain_parser;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NativeFunc extends Closure {

    public Method func = null;
    public String name;
    private static Logger LOGGER = LogManager.getFormatterLogger(NativeFunc.class);

    public NativeFunc(Method func) {
        super(null, null);
        this.name = func.getName();
        this.func = func;
    }

    public static NativeFunc getNativeFunc(String className, String methodName, List<Object> argTypes) throws Exception {

        var clas = Class.forName(className);
        var types = toTypeArray(argTypes);
        var method = clas.getDeclaredMethod(methodName, types);

        return new NativeFunc(method);
    }

    public static Class[] toTypeArray(List<Object> args) throws Exception {

        var types = new Class[args.size()];

        for (int i = 0; i < args.size(); i++) {

            var type = (String) args.get(i);

            if (type.equals("int")) types[i] = Integer.TYPE;
            else if (type.equals("float")) types[i] = Float.TYPE;
            else if (type.equals("double")) types[i] = Double.TYPE;
            else types[i] = Class.forName(type);
        }

        return types;
    }

    @Override
    public Object eval(Cons args, AssocList env) {

        var argList = InterpreterUtils.flattenCons(args).stream()
            .toArray();

        try {
            var ret = func.invoke(null, argList);
            return ret;
        } catch (Exception e) {
            LOGGER.error("Error invoking %s: %s", this, e);
            return null;
        }
    }

    @Override
    public String toString() {
        var params = this.func.getAnnotatedParameterTypes();
        var ret = this.func.getAnnotatedReturnType();

        return String.format("%s:[%s -> %s]", name, Arrays.toString(params), ret);
    }
}
