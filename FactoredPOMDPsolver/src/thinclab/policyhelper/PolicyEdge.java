package thinclab.policyhelper;

import java.util.List;

public class PolicyEdge {
	
	public List<String> observation;
	public int from;
	public int to;
	
	public PolicyEdge(int from, List<String> observation, int to) {
		this.from = from;
		this.observation = observation;
		this.to = to;
	}

	@Override
	public String toString() {
		return "PolicyEdge [observation=" + observation + ", from=" + from + ", to=" + to + "]";
	}
}
