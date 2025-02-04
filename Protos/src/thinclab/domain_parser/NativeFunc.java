package thinclab.domain_parser;

import java.lang.reflect.Method;
import java.util.Arrays;

public class NativeFunc extends Closure {

    public Method func = null;

    public NativeFunc(Method func) {
        super(null, null);
        this.func = func;
    }

    @Override
    public String toString() {
        var params = this.func.getAnnotatedParameterTypes();
        var ret = this.func.getAnnotatedReturnType();

        return String.format("f:[%s -> %s]",
                Arrays.toString(params), ret);
    }
}
