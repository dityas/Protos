package thinclab.symbolicperseus;


import java.io.*;
import java.util.*;
import java.lang.*;


public class Solver implements Serializable {

    // default values
    public static int nRounds = 5;
    public static int nIterations =30;  // backup iterations  per round
    public static int maxAlphaSetSize = 100;
    public static int numBelStates = 100;
    public static int maxBelStates = 10000;
    public static int episodeLength = 50;  // when generating belief points
    public static double threshold = 0.001;
    public static double explorProb=0.4;

    public static String iofile;
    public static boolean generate=false;
    public static boolean multinits=false;
    public static boolean simulate=false;
    public static boolean havepolicy=false;
    public static int nits=100;
    

    public static void usage() {
	    System.out.println("Usage: java Solver <spuddfile> [<flags>]");
	    System.out.println("where: ");
	    System.out.println("\t spuddfile (required): name of POMDP input file in SPUDD format");
	    System.out.println("\t flags can be one of :");
	    System.out.println("\t\t -g \t - generate policy");
	    System.out.println("\t\t -i [filename]\t - read input policy from filename (default <spuddfile base name>.pomdp");
	    System.out.println("\t\t\t\t   if -i is not given, the poilcy is generated and writted to <spuddfile base name>.pomdp)");
	    System.out.println("\t\t -j \t - input contains multiple initial states as adjuncts and these should be used");
	    System.out.println("\t\t -s <int>\t - do simulation for <int> iterations (with generated or read policy if available, or with user input if not");
	    System.out.println("\t\t -b <int>\t - number of belief points to use [100]");
	    System.out.println("\t\t -m <int>\t - max number of alpha vectors [100]");
	    System.out.println("\t\t -t <int>\t - number of iterations of symbolic Perseus per round [30]");
	    System.out.println("\t\t -e <int>\t - episode length to use when generating belief points [50]");
	    System.out.println("\t\t -x <double>\t - exploration probability to use when generating belief points [0.4]");
	    System.out.println("\t\t -h <double>\t - threshold for beliefs [0.001]");
	    System.out.println("\t\t -r <int>\t - number of rounds to run [5]");
	    
    }
    public static void printDotDDs(POMDP pomdp) {
	String fname; 
	FileOutputStream f_out;
	PrintStream outp; 

	try {
	    for (int i=0; i<pomdp.nActions; i++) {
		fname="action_"+i+"_rewFn.dot";
		f_out = new FileOutputStream (fname);
		outp = new PrintStream(f_out);
		pomdp.actions[i].rewFn.printDotDD(outp);
		outp.close();
		f_out.close();
		
		System.out.println("number of state vars "+pomdp.nStateVars);
		for (int k=0; k<pomdp.nStateVars; k++) {
		    
		    fname="action_"+i+"_CPT_"+k+".dot";
		    f_out = new FileOutputStream (fname);
		    outp = new PrintStream(f_out);
		    pomdp.actions[i].transFn[k].printDotDD(outp);
		    outp.close();
		    f_out.close();
		}
		for (int k=0; k<pomdp.nObsVars; k++) {
		    
		    fname="action_"+i+"_OBSF_"+k+".dot";
		    f_out = new FileOutputStream (fname);
		    outp = new PrintStream(f_out);
		    pomdp.actions[i].obsFn[k].printDotDD(outp);
		    outp.close();
		    f_out.close();
		}
	    }
	} catch (IOException err) {
	    System.out.println("file not found error "+err);
	    return;
	}
    }
    public static boolean parseArgs(String [] args) {
	String ioarg;

	if (args.length >= 2) {
	    int numargs = args.length-1;
	    int thearg=1;
	    ioarg=args[thearg];
	    while (numargs > 0 && ioarg.startsWith("-")) {
		//System.out.println("numargs "+numargs+" thearg "+thearg+" ioarg "+ioarg);
		if (ioarg.equals("-i"))  {
		    if (numargs-1 > 0 &&  !(args[thearg+1].startsWith("-"))) {
			iofile = args[thearg+1];
			thearg += 2;
			havepolicy=true;
		    } else {
			thearg++;
			numargs++;
		    }
		    generate = false;
		} else if (ioarg.equals("-s")) {
		    if (numargs-1 > 0 &&  !(args[thearg+1].startsWith("-"))) {
			simulate=true;
			nits = Integer.parseInt(args[thearg+1]);
			thearg+=2;
		    } else {
			numargs=0;
		    }
		} else if (ioarg.equals("-g")) {
		    generate=true;
		    thearg++;
		    numargs++;
		} else if (ioarg.equals("-j")) {
		    multinits=true;
		    thearg++;
		    numargs++;
		} else if (ioarg.equals("-b")) {
		    if (numargs-1 > 0 &&  !(args[thearg+1].startsWith("-"))) {
			numBelStates = Integer.parseInt(args[thearg+1]);
			thearg += 2;
		    } else {
			numargs=0;
		    }
		} else if (ioarg.equals("-m")) {
		    if (numargs-1 > 0 &&  !(args[thearg+1].startsWith("-"))) {
			maxAlphaSetSize = Integer.parseInt(args[thearg+1]);
			thearg += 2;
		    } else {
			numargs=0;
		    }
		} else if (ioarg.equals("-t")) {
		    if (numargs-1 > 0 &&  !(args[thearg+1].startsWith("-"))) {
			nIterations = Integer.parseInt(args[thearg+1]);
			thearg += 2;
		    } else {
			numargs=0;
		    }
		}  else if (ioarg.equals("-e")) {
		    if (numargs-1 > 0 &&  !(args[thearg+1].startsWith("-"))) {
			episodeLength = Integer.parseInt(args[thearg+1]);
			thearg += 2;
		    } else {
			numargs=0;
		    }
		}   else if (ioarg.equals("-r")) {
		    if (numargs-1 > 0 &&  !(args[thearg+1].startsWith("-"))) {
			nRounds = Integer.parseInt(args[thearg+1]);
			thearg += 2;
		    } else {
			numargs=0;
		    }
		}  else if (ioarg.equals("-x")) {
		    if (numargs-1 > 0 &&  !(args[thearg+1].startsWith("-"))) {
			explorProb  = Double.parseDouble(args[thearg+1]);
			thearg += 2;
		    } else {
			numargs=0;
		    }
		}  else if (ioarg.equals("-h")) {
		    if (numargs-1 > 0 &&  !(args[thearg+1].startsWith("-"))) {
			threshold  = Double.parseDouble(args[thearg+1]);
			thearg += 2;
		    } else {
			numargs=0;
		    }
		} 
			
		numargs-=2;
		if (numargs > 0)
		    ioarg=args[thearg];

	    }
	    if (numargs != 0) {
		return false;
	    } else {
		return true;
	    }
	} else {
	    return true;
	}
    }
    public static void main(String args[]) {
	if (args.length < 1 || args[0].startsWith("-")) {
	    usage();
	    return;
	}
	String spuddfile = args[0];
	String basename = spuddfile.substring(0,spuddfile.lastIndexOf("."));
	boolean debug=false;
	// default output/input file
	iofile = basename+".pomdp";
	if (!parseArgs(args)) {
	    usage();
	    return;
	}
	if (generate) {
	    System.out.println("Solving pomdp in file "+spuddfile+" for \n\t\t "+nRounds+" rounds, \n\t\t "+nIterations+" iterations per round, \n\t\t "+maxAlphaSetSize+" alpha vectors maximum, using \n\t\t "+numBelStates+" belief states generated with an episode length of \n\t\t "+episodeLength+" and an exploration probability of \n\t\t "+explorProb+" and a threshold of \n\t\t "+threshold);

	    POMDP pomdp = new POMDP(spuddfile,debug);

	    //printDotDDs(pomdp);
	    
	    //DD goalDD = pomdp.getGoalDD();
	    //goalDD.printSpuddDD(System.out);
	    //boolean isgoal = pomdp.checkGoal(goalDD,pomdp.initialBelState,0.1);
	    //System.out.println("is goal? "+isgoal);

	    if (generate) {
		pomdp.solve(nRounds, numBelStates, maxBelStates, episodeLength, threshold, explorProb, nIterations, maxAlphaSetSize, basename, multinits);
	    }


	    //double [] obsfit = pomdp.evaluateObservations(1);
	    //for (int i =0; i<obsfit.length; i++) 
	    //	System.out.println("fitness of "+i+":"+obsfit[i]);
	    //double v0 = pomdp.evaluatePolicyStationary(30,30);
	    //System.out.println("value is "+v0);
	    
	    // save fname version
	    if (generate) {
		FileOutputStream f_out;
		try {
		    // save to disk
		    // Use a FileOutputStream to send data to a file
		    // called myobject.data.
		    f_out = new FileOutputStream (iofile);
		} catch (FileNotFoundException err) {
		    System.out.println("file not found error "+err);
		    return;
		}
		try {
		    // Use an ObjectOutputStream to send object data to the
		    // FileOutputStream for writing to disk.
		    ObjectOutputStream obj_out = new
			ObjectOutputStream (f_out);
		    
		    // Pass our object to the ObjectOutputStream's
		    // writeObject() method to cause it to be written out
		    // to disk.
		    obj_out.writeObject (pomdp);
		} catch (IOException err) {
		    System.out.println("file write error"+err);
		}
	    }
	} else if (simulate) {
	    if (!havepolicy) {
		POMDP pomdp = new POMDP(spuddfile,debug);

		//double polval = pomdp.evaluatePolicyStationary(10,100,true);
		System.out.println("policy value is ");
		pomdp.simulateGeneric(nits);
		
	    } else {
		// Read from disk using FileInputStream.
		FileInputStream f_in;
		try {
		    f_in = new FileInputStream (iofile);
		} catch (FileNotFoundException err) {
		    System.out.println("file not found error "+err);
		    return;
		}
		Object obj;
		ObjectInputStream obj_in;
		try {
		    
		    // Read object using ObjectInputStream.
		    obj_in = new ObjectInputStream (f_in);
		} catch (IOException err) {
		    System.out.println("file read error"+err);
		    return;
		} 
		try {
		    // Read an object.
		    obj = obj_in.readObject ();
		} catch (IOException err) {
		    System.out.println("file read error"+err);
		    return;
		} catch (ClassNotFoundException err) {
		    System.out.println("class error"+err);
		    return;
		} 
		boolean heuristic=false;
		// Is the object that you read in, say, an instance
		// of the POMDP class?
		if (obj instanceof POMDP) {
		    // Cast object to a POMDP
		    POMDP pomdp = (POMDP) obj;
		    
		    // Do something with pomdp
		    //now we have to get all the global information and variable name
		    // stuff from a file
		    // this will stomp over some stuff in the above read, but works
		    pomdp.readFromFile(spuddfile,debug);
		    /*
		    pomdp.actions[0].transFn[0].display();
		    pomdp.actions[0].transFn[1].display();
		    pomdp.actions[0].transFn[2].display();
		    
		    DD tmp = OP.mult(pomdp.actions[0].transFn[1],pomdp.actions[0].transFn[2]);
		    tmp = OP.mult(tmp,pomdp.actions[0].obsFn[1]);
		    tmp.display();
		    */
		    //pomdp.displayPolicy();
		    //pomdp.addbeldiff = true;
		    //System.out.println("addbeldiff is "+pomdp.addbeldiff);
		    
		    //    double [] obsfit = pomdp.evaluateObservations(1);
		    //for (int i =0; i<obsfit.length; i++) 
		    //System.out.println("fitness of "+i+":"+obsfit[i]);
		    //double v0 = pomdp.evaluatePolicyStationary(30,30);
		    //System.out.println("value is "+v0);


		    // evaluate the policy
		    //double polval = pomdp.evaluatePolicyStationary(1,100,true);
		    //System.out.println("policy value is "+polval);
		    
		    
		    // do simulation
		    DD belState = pomdp.initialBelState;
		    // use the adjunct initial belief state if there is one
		    if (pomdp.adjunctNames != null) {
			for (int i=0; i<pomdp.adjunctNames.length; i++) 
			    if (pomdp.adjunctNames[i].startsWith("init")) {
				System.out.println("Using "+pomdp.adjunctNames[i]+" adjunct dd as initial state");
			    belState = pomdp.adjuncts[i];
			    }
		    }


		    int actId,cactId;
		    String [] obsnames = new String[pomdp.nObsVars];
		    int nits = 100;
		    String inobs,inact;  
		    DD obsDist;
		    InputStreamReader cin = new InputStreamReader(System.in);
		    try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			
			while (nits > 0) {
			    System.out.println("current belief state: ");
			    pomdp.printBeliefState(belState);
			    actId = pomdp.policyQuery(belState,heuristic);
			    System.out.println("action suggested by policy: "+actId+" which is "+pomdp.actions[actId].name);
			    System.out.print("enter action to use:");
			    inact = in.readLine();
			    cactId = pomdp.findActionByName(inact);
			    if (cactId >= 0) {
				actId = cactId;
			    }
			    System.out.println("action used: "+actId+" which is "+pomdp.actions[actId].name);
			    
			    // to see a distribution over observations given the belief state and the action selected
			    //obsDist = OP.addMultVarElim(pomdp.concatenateArray(belState,pomdp.actions[actId].transFn,pomdp.actions[actId].obsFn), 
			    //pomdp.concatenateArray(pomdp.varIndices,pomdp.primeVarIndices));
			    //obsDist.display();


			    for (int o=0; o<pomdp.nObsVars; o++) {
				obsnames[o]=pomdp.obsVars[o].valNames[1];
				System.out.print("enter observation "+pomdp.obsVars[o].name+" ["+obsnames[o]+"]: ");
				inobs = in.readLine();
				for (int k=0; k<pomdp.obsVars[o].arity; k++) {
				    if (inobs.equalsIgnoreCase(pomdp.obsVars[o].valNames[k])) 
					obsnames[o]=inobs;
				}
			    }
			    System.out.print("observations: ");
			    for (int o=0; o<pomdp.nObsVars; o++)
				System.out.print(" "+obsnames[o]);
			    System.out.println();
			    belState = pomdp.beliefUpdate(belState,actId,obsnames);
			    nits--;
			}
		    }  catch (IOException e) {
		    }
		    

		} else {
		    System.out.println("that file does not contain a pomdp");
		}
	    }
	}
    }
}
