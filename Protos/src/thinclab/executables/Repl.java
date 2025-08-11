package thinclab.executables;

import thinclab.domain_parser.Interpreter;

class Repl {

    public static void main(String[] args) throws Exception {
        var interpreter = new Interpreter();
        interpreter.repl();
    }
}
