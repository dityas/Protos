/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.executables;

import thinclab.domain_lang.Parser;
import thinclab.domain_lang.SpuddInterpreter;

/*
 * @author adityas
 *
 */
public class LauncherInteractive {

    public static void main(String[] args) throws Exception {

        var interpreter = new SpuddInterpreter();

        if (args.length > 0) {
            var fileName = args[0];
            var code = Parser.readFromFile(fileName);

            if (code != null)
                interpreter.run(Parser.tokenize(code)); 

            else
                System.err.println(
                        String.format("Could not find %s", fileName));
        }

        else
            interpreter.repl();
    }

}
