package thinclab.exceptions;

public class DDNotDefinedException extends Exception {
	
	public DDNotDefinedException() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	public DDNotDefinedException(String msg) {
		super("Missing DD " + msg);
	}
}
