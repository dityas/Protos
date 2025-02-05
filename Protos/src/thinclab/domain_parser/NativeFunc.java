package thinclab.domain_parser;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NativeFunc extends Closure {

    public Method func = null;
    private static Logger LOGGER = LogManager.getFormatterLogger(NativeFunc.class);

    public NativeFunc(Method func) {
        super(null, null);
        this.func = func;
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

        return String.format("f:[%s -> %s]",
                Arrays.toString(params), ret);
    }
}
