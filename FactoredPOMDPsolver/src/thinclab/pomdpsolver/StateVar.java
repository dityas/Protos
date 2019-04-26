package thinclab.pomdpsolver;
import java.io.Serializable;
import java.util.Arrays;

public class StateVar implements Serializable {
		/**
	 * 
	 */
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

		@Override
		public String toString() {
			return "StateVar [arity=" + arity + ", name=" + name + ", id=" + id + ", valNames="
					+ Arrays.toString(valNames) + "]";
		}

	}