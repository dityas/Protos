
package thinclab.domain_lang;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

import thinclab.utils.types.Either;
import thinclab.utils.types.Left;
import thinclab.utils.types.Right;

class JavaMethod extends Lambda {

    private Method method;
    private Class<?>[] argTypes;

    private JavaMethod() {
        super(null, null);
    }

    @Override
    public Either<Exception, Object> eval(List<Object> args, 
            Environment env) {
        
        try {
            
            var obj = Modifier.isStatic(method.getModifiers()) ? 
                args.remove(0) : null;

            Object[] _args = new Object[argTypes.length];
            for (int i = 0 ; i < args.size(); i++) 
                _args[i] = args.get(i);
            
            return Right.of(method.invoke(obj, _args)); 
        }

        catch (Exception e) {
            return Left.of(e);
        }
    }

    @Override
    public String toString() {
        return String.format("%s -> %s", 
                Arrays.toString(argTypes), method.getReturnType().getName());
    }

    public static Either<Exception, JavaMethod> makeMethod(
            String className, String methodName, List<String> argTypes) {

        try {

            var func = new JavaMethod();

            Class<?>[] _argTypes = new Class[argTypes.size()];
            for (int i = 0; i < argTypes.size(); i++)
                _argTypes[i] = Class.forName(argTypes.get(i));

            func.argTypes = _argTypes;

            var _class = Class.forName(className);
            func.method = _class.getMethod(methodName, func.argTypes);

            return Right.of(func);
        }

        catch (Exception e) {
            return Left.of(e);
        }
    }
}
