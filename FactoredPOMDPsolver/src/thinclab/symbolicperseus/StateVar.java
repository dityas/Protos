package thinclab.symbolicperseus;

import java.io.Serializable;

public class StateVar implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8412637585727464610L;

	public int arity;
	public String name;
	public int id;
	public String[] valNames;

	public StateVar(int a, String n, int i) {
		arity = a;
		name = n;
		id = i;
		valNames = new String[arity];
	}

	public void addValName(int i, String vname) {
		valNames[i] = vname;
	}

}