package thinclab.policyhelper;

import java.util.List;

import thinclab.utils.visualizers.VizEdge;

public class PolicyEdge extends VizEdge {
	
	public List<String> observation;
	public int from;
	public int to;
	
	public PolicyEdge(int from, List<String> observation, int to) {
		
		super(from, observation.toString(), to);
		
		this.from = from;
		this.observation = observation;
		this.to = to;
	}

	@Override
	public String toString() {
		return "PolicyEdge [observation=" + observation + ", from=" + from + ", to=" + to + "]";
	}
}
