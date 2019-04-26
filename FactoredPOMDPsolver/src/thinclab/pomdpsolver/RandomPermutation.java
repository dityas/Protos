package thinclab.pomdpsolver;
import java.math.*;
import java.util.*;
import java.io.*;
import java.lang.*;

class RandomPermutation implements Serializable {
    
    public int [] permutation;
    // ids of those that are 'done' - used in perseus
    public boolean [] dids;
    public RandomPermutation(Random g, int len) {
	setUp(g,len,false);
    }
    public RandomPermutation(Random g, int len, boolean debug) {
	setUp(g,len,debug);
    }
    public void setUp(Random g, int len, boolean debug) {
	permutation = new int[len];
	dids = new boolean[len];
	for (int i=0; i<len; i++) {
	    permutation[i] = i;
	    // should be by default, but what the .
	    dids[i]=false;
	}
	int r,t;
	// JH TEST - comment this out to fix the permutation for comparison with matlab code only
	if (!debug) {
	    for (int i=0; i<len; i++) {
		r = i+g.nextInt(len-i);
		t = permutation[r];
		permutation[r]=permutation[i];
		permutation[i]=t;
	    }
	}
    }
    public int getFirst() {
	boolean notdone=true;
	int i;
	for (i=0; notdone && i<permutation.length; i++) {
	    notdone  = (notdone && dids[i]);
	}
	return i-1;
    }
    public int getSetDone(int i) {
	setDone(i);
	return get(i);
    }
    public int getNumLeft() {
	int numleft=0;
	for (int i=0; i<dids.length; i++)  {
	    if (!dids[i]) 
		numleft++;
	}
	return numleft;
    }
    public int get(int i) {
	return permutation[i];
    }
    public void setDone(int i) {
	dids[i]=true;
    }
    public boolean isempty() {
	boolean notdone = true;
	for (int i=0; notdone && i<dids.length; i++) 
	    notdone = (notdone && dids[i]);
	return notdone;
    }
    public void display() {
	for (int i=0; i<permutation.length; i++) {
	    if (!dids[i]) 
		System.out.print("  "+permutation[i]);
	}
	System.out.println();
    }

    public double [] getDiffs(double [] currv, double [] newv, double tol) {
	double [] diffs = new double[currv.length];
	for (int i=0; i<currv.length; i++) {
	    diffs[i]=0.0;
	    if (!dids[i]) 
		diffs[i]=currv[i]-newv[i]+tol;
	}
	return diffs;
    }
    public void getNewDoneIds(double [] currv, double [] newv, double tol) {
	int numkeep=0;
	boolean[] newdids = new boolean[dids.length];
	for (int i=0; i<currv.length; i++) {
	    newdids[i] = dids[i];
	    if (!dids[i] && (newv[i]-tol) >= currv[i]) 
		newdids[i] = true;
	}
	for (int i=0; i<dids.length; i++) 
	    dids[i] = newdids[i];
    }

    public static void main(String args[]) {
	System.out.println("len is "+args[0]);
	Random g = new Random();
	RandomPermutation r = new RandomPermutation(g, 10);
	r.display();
    }

}
