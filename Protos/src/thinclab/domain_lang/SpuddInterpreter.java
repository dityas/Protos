
package thinclab.domain_lang;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

public class SpuddInterpreter {

    public Environment env = new Environment();

    public SpuddInterpreter() {

        try {
            // load base file
            var base = this.getClass()
                .getClassLoader()
                .getResourceAsStream("base.spudd");

            var code = new String(
                    base.readAllBytes(), StandardCharsets.UTF_8);
            run(Parser.tokenize(code));
        }

        catch (Exception e) {
            System.err.println(
                    String.format(
                        "Error while loading base lib: %s",
                        e));
            System.exit(-1);
        }
    }

    public void run(List<String> line) {

        while(!line.isEmpty()) {
            var res = Eval.eval(Parser.parse(line), env);
            System.out.println(res);
        }
    }

    public void repl() {

        var cons = System.console();

        while(true) {

            var parsed = Parser.parse(
                    Parser.tokenize(
                        cons.readLine(" \u03BB > ")));
            var res = Eval.eval(parsed, env);

            if (res != null)
                System.out.println(res);
        }
    }
}
