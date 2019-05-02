package thinclab.symbolicperseus;

import java.io.Serializable;

public class Action implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4223311575381670245L;

	public String name;
	public DD[] transFn;
	public DD[] obsFn;
	public DD rewFn;
	public DD[] rewTransFn;
	public DD initialBelState;

	public Action(String n) {
		name = n;
	}

	public void addTransFn(DD[] tf) {
		transFn = new DD[tf.length];
		for (int idx = 0; idx < tf.length; ++idx) {
			transFn[idx] = tf[idx];
		}
	}

	public void addObsFn(DD[] of) {
		obsFn = new DD[of.length];
		for (int idx = 0; idx < of.length; ++idx) {
			obsFn[idx] = of[idx];
		}
	}

	public void buildRewTranFn() {
		rewTransFn = new DD[transFn.length + 1];
		int k = 0;
		rewTransFn[k++] = rewFn;
		for (int idx = 0; idx < transFn.length; ++idx) {
			rewTransFn[k++] = transFn[idx];
		}
	}
}
