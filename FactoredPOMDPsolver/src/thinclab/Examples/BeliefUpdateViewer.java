/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.Examples;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import thinclab.exceptions.SolverException;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.ipomdpsolver.IPOMDP;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.ipomdpsolver.InteractiveBelief.InteractiveBelief;
import thinclab.symbolicperseus.StateVar;

/*
 * @author adityas
 *
 */
public class BeliefUpdateViewer {
	/*
	 * Just a console based helper to step through the belief update
	 */
	
	public int mjLookAhead = -1;
	public int mjDepth = -1;
	public String domainFile;
	
	public boolean bt = false;
	
	IPOMDP ipomdp;
	
	public void initialize() {
		/*
		 * Initializes the IPOMDP and prepares for belief stepping
		 */
		
		System.out.println("Parsing domain...");
		IPOMDPParser parser = new IPOMDPParser(this.domainFile);
		parser.parseDomain();
		
		
		this.ipomdp = new IPOMDP(parser, this.mjDepth, this.mjLookAhead);
		System.out.println("IPOMDP initialized");
	}
	
	public void loop() {
		
		for (int t=0; t < this.mjDepth - mjLookAhead; t++) {
			
			System.out.println("\r\n============================================================");
			
			if (this.bt) {
				System.out.println("--------------------------TRACE-----------------------------");
//				String[] traces = this.ipomdp.oppModel.getDebugTraces();
//				
//				for (String trace : traces)
//					System.out.println(trace);
				System.out.println("------------------------------------------------------------");
			}
			this.step(t);
			System.out.println("============================================================\r\n");
		}
	}
	
	public void step(int stepNumber) {
		/*
		 * Asks user input for aciton and observation and steps to the next belief state
		 */
		System.out.println("Time Step: " + stepNumber);
		System.out.println("Current belief: ");
		System.out.println(
				InteractiveBelief.getBeliefNodeLabel(
						this.ipomdp, 
						this.ipomdp.lookAheadRootInitBeliefs.get(0)).replace("<br>", "\r\n"));
		
		int unfactoredS = 1;
		for (StateVar st : this.ipomdp.S.subList(0, this.ipomdp.S.size() - 1)) {
			unfactoredS *= st.arity;
		}
		
		System.out.println("Unfactored S dimensions are: " + unfactoredS);
		
		Scanner consoleReader = new Scanner(System.in);
		System.out.println("Enter action from: " + this.ipomdp.Ai);
		String action = consoleReader.nextLine();
		
		System.out.println("select observation from: " + this.ipomdp.obsCombinations);
		List<String> obs = this.ipomdp.obsCombinations.get(consoleReader.nextInt());
		
		System.out.println("Taking action " + action + " and observing " + obs);
		
		long then = System.currentTimeMillis();
		try {
			
			this.ipomdp.step(
					this.ipomdp.lookAheadRootInitBeliefs.get(0), 
					action, 
					obs.toArray(new String[obs.size()]));
		} 
		
		catch (Exception e) {
			System.err.println("While stepping through the IPOMDP");
			e.printStackTrace();
			System.exit(-1);
		}
		
		long now = System.currentTimeMillis();
		System.out.println("That took " + (now - then) + " millis.");
	}
	
	public void parseArgs(String[] args) {
		/*
		 * Parse command line args
		 */
		
		Deque<String> argQueue = new ArrayDeque<String>();
		argQueue.addAll(Arrays.asList(args));
		
		while(!argQueue.isEmpty()) {
			
			String arg = argQueue.pop();
			
			switch (arg.charAt(0)) {
			
				case '-':
					
					/* domain file */
					if (arg.contains("-f")) this.domainFile = argQueue.pop();
					
					/* parse look ahead horizon */
					else if (arg.contains("-l")) 
						this.mjLookAhead = Integer.parseInt(argQueue.pop());
					
					/* depth of j's policy tree */
					else if (arg.contains("-d"))
						this.mjDepth = Integer.parseInt(argQueue.pop());
					
					/* back trace switch */
					else if (arg.contains("--bt"))
						this.bt = true;
					
					else {
						System.err.println("Unknown option " + arg);
						System.exit(-1);
					}
					
					break;
					
				default:
					break;
			}
		}
		
		/* required args not provided */
		if (this.domainFile == null || this.mjDepth == -1 || this.mjLookAhead == -1) {
			System.err.println("Missing required args");
			System.err.println("Required args are");
			System.err.println("-f <ipomdp l1 domain file> path");
			System.err.println("-l <look ahead horizon> int");
			System.err.println("-d <j's policy tree depth> int");
			System.exit(-1);
		}
	}
	
	public static void main(String[] args) {
		
//		LoggerFactory.stopLogging();
		
		System.out.println("Starting BeliefUpdateViewer");
		BeliefUpdateViewer buViewer = new BeliefUpdateViewer();
		
		/* Parse arguments */
		buViewer.parseArgs(args);
		System.out.println("Args parsed");
		System.out.println("Domain file is " + buViewer.domainFile);
		System.out.println("J's policy tree depth is " + buViewer.mjDepth);
		System.out.println("I's look ahead horizon is " + buViewer.mjLookAhead);
		
		/* initialize */
		buViewer.initialize();
		
		/*
		 * run forever, or till your computer sparks into flames from computing millions 
		 * of state transitions
		 */
		buViewer.loop();
	}

}
