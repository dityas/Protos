package thinclab.symbolicperseus;

import java.io.Serializable;
import java.util.Arrays;

public class StateVar implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8412637585727464610L;

	public int arity;
	public String name;
	public int id;
	public String[] valNames;

	// ----------------------------------------------------------------------
	
	public StateVar(int a, String n, int i) {
		arity = a;
		name = n;
		id = i;
		valNames = new String[arity];
	}
	
	public StateVar(String n, int i, String[] valNames) {
		
		this(valNames.length, n, i);
		this.valNames = valNames;
	}
	
	// ----------------------------------------------------------------------
	
	public void setName(String name) {
		/*
		 * Setter for variable name
		 */
		this.name = name;
	}
	
	public void setId(int id) {
		/*
		 * Setter for id
		 */
		this.id = id;
	}

	public void addValName(int i, String vname) {
		valNames[i] = vname;
	}

	@Override
	public String toString() {
		return "StateVar [name=" + name 
				+ ", id=" + id 
				+ ", valNames=" + Arrays.toString(valNames) 
				+ "]";
	}
	
	// ----------------------------------------------------------------------
	
	

}