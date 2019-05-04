package thinclab.policyhelper;

public class PolicyEdge {
	
	public String observation;
	public int from;
	public int to;
	
	public PolicyEdge(int from, String observation, int to) {
		this.from = from;
		this.observation = observation;
		this.to = to;
	}

	@Override
	public String toString() {
		return "PolicyEdge [observation=" + observation + ", from=" + from + ", to=" + to + "]";
	}
}
