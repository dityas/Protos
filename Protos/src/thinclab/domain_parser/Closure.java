package thinclab.domain_parser;


public class Closure {

    public AssocList closureEnv = null;
    public Cons params;
    public Object body;

    public Closure(Cons params, Object body) {
        this.params = params;
        this.body = body;
    }

    public Object eval(Cons args, AssocList env) {

        if (args.length() != params.length()) {
            var error = String.format(
                    "Incorrect args passed, expected %s, for %s",
                    params, args);

            throw new RuntimeException(error);
        }

        closureEnv = new AssocList(env);
        closureEnv.putAll(params, args);

        return Interpreter.eval(body, closureEnv);
    }

    @Override
    public String toString() {
        return String.format("f:[%s -> %s]", params, body);
    }
}
