
package thinclab.domain_lang;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Eval {

    private static final Logger LOGGER =
        LogManager.getFormatterLogger(Eval.class);

    public static Optional<Object> eval(Object parsed, Environment env) {

        if (parsed instanceof String s) {
            
            if (env.containsKey(s))
                return Optional.ofNullable(env.get(s));

            else return Optional.empty();
        }

        else {
            LOGGER.error("Could not evaluate %s", parsed);
            return Optional.empty();
        }
    }
}
