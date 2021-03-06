package thinclab.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import thinclab.legacy.DD;

public class PolicyCache implements Serializable {
	
	/*
	 * Stores alpha vectors for different bellman error values 
	 */
	
	private static final long serialVersionUID = -7254730964758794169L;
	
	public TreeMap<Integer, DD[]> aVecsMap = new TreeMap<Integer, DD[]>();
	public HashMap<Integer, int[]> policyMap = new HashMap<Integer, int[]>();
	
	/*
	 * Using string here because I don't know how to round off a double. I am sure there
	 * is a more efficient way to check for oscillations. But I really want to graduate.
	 * So dirty code it is...
	 */
	public HashSet<String> errorVals = new HashSet<String>();
	
	private int oscPatience;
	private int numOscillations = 0;
	
	public PolicyCache(int oscPatience) {
		this.oscPatience = oscPatience;
	}
	
	public void cachePolicy(int numAlphaVectors, DD[] alphaVectors, int[] policy) {
		this.aVecsMap.put(numAlphaVectors, alphaVectors);
		this.policyMap.put(numAlphaVectors, policy);
	} // public void recordAlphaVectors
	
	public boolean isOscillating(String errorVal) {
		if (this.errorVals.contains(errorVal)) {
			if (this.numOscillations >= this.oscPatience) {
				return true;
			}
			
			else {
				this.numOscillations += 1;
				return false;
			}
		}
		
		else {
			this.errorVals.add(errorVal);
			return false;
		}
	} // public boolean isOscillating
	
	public void resetOscillationTracking() {
		this.errorVals = new HashSet<String>();
		this.aVecsMap.clear();
		this.policyMap.clear();
		this.numOscillations = 0;
	} // public void resetOscillationTracking
	
	public DD[] getMaxAlphaVecs() {
		return this.aVecsMap.get(this.aVecsMap.lastKey());
	} // public DD[] getMaxAlphaVecs
	
	public int[] getMaxPolicy() {
		return this.policyMap.get(this.aVecsMap.lastKey());
	} // public int[] getMaxPolicy
	
	public void resetAlphaVecsMap() {
		this.aVecsMap = new TreeMap<Integer, DD[]>();
	} // public void resetAlphaVecsMap
}
