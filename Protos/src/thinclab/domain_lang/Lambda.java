
package thinclab.domain_lang;

import java.util.List;

import thinclab.utils.types.Either;
import thinclab.utils.types.Left;

class Lambda {

    public final List<String> argNames;
    public final Object body;

    public Lambda(List<String> args, Object body) {
        this.argNames = args;
        this.body = body;
    }

    public Either<Exception, Object> eval(List<Object> args, 
            Environment env) {
        
        if (args.size() != argNames.size())
            return Left.of(Eval.error(
                    String.format("args do not match for %s", this)));

        for (int i = 0 ; i < argNames.size(); i++)
            env.map.put(argNames.get(i), args.get(i));

        return Eval.eval(body, env);
    }

    @Override
    public String toString() {
        return String.format("%s -> %s", argNames, body);
    }
}
