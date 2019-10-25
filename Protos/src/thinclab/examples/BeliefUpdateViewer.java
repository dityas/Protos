/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.examples;

import java.util.List;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cern.colt.Arrays;
import thinclab.belief.FullInteractiveBeliefExpansion;
import thinclab.belief.InteractiveBelief;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.legacy.StateVar;
import thinclab.solvers.OnlineInteractiveSymbolicPerseus;
import thinclab.solvers.OnlineSolver;
import thinclab.solvers.OnlineValueIterationSolver;
import thinclab.utils.CustomConfigurationFactory;

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
	
	public IPOMDP ipomdp;
	public FullInteractiveBeliefExpansion expansionStrat;
	public OnlineSolver solver;
	
	public void initialize() {
		/*
		 * Initializes the IPOMDP and prepares for belief stepping
		 */
		
		System.out.println("Parsing domain...");
		IPOMDPParser parser = new IPOMDPParser(this.domainFile);
		parser.parseDomain();
		
		/* Initialize IPOMDP */
		this.ipomdp = new IPOMDP(parser, this.mjDepth, this.mjLookAhead);
		System.out.println("IPOMDP initialized");
		
		/* Initialize expansion strategy */
		this.expansionStrat = new FullInteractiveBeliefExpansion(this.ipomdp);
		System.out.println("IPOMDP initialized");
		
		/* Initialize solver */
//		this.solver = 
//				new OnlineInteractiveSymbolicPerseus(
//						this.ipomdp, 
//						this.expansionStrat, 
//						1, 1000);
		this.solver = new OnlineValueIterationSolver(ipomdp);
		System.out.println("Solver initialized");
	}
	
	public void loop() {
		
		for (int t=0; t < this.mjDepth - mjLookAhead; t++) {
			
			this.step(t);
		}
	}
	
	public void step(int stepNumber) {
		/*
		 * Ask user input for action and observation and step to the next belief state
		 */
		
		/* compute suggestion */
		this.solver.solveCurrentStep();
		String suggestedAct = this.solver.getActionAtCurrentBelief();
		
		/* Print the current belief state with info about j's beliefs */
		System.out.println("Time Step: " + stepNumber);
		System.out.println("Current belief: ");
		System.out.println(
				InteractiveBelief.getBeliefNodeLabel(
						this.ipomdp, 
						this.ipomdp.lookAheadRootInitBeliefs.get(0)).replace("<br>", "\r\n"));
		
		/* 
		 * compute unfactored state space dimensions by multiplying the arity of all 
		 * state variables. 
		 */
		int unfactoredS = 1;
		for (StateVar st : this.ipomdp.S.subList(0, this.ipomdp.S.size() - 1)) {
			unfactoredS *= st.arity;
		}
		
		System.out.println("Unfactored S dimensions are: " + unfactoredS);
		
		Scanner consoleReader = new Scanner(System.in);
		System.out.println("Enter action from: " + this.ipomdp.Ai + "[suggested: " + suggestedAct
				+"]: ");
		String action = consoleReader.nextLine();
		
		System.out.println("select observation from: " + this.ipomdp.obsCombinations);
		List<String> obs = this.ipomdp.obsCombinations.get(consoleReader.nextInt());
		
		System.out.println("Taking action " + action + " and observing " + obs);
		
		long then = System.currentTimeMillis();
		try {
			this.solver.nextStep(action, obs);
		} 
		
		catch (Exception e) {
			System.out.println("While stepping through the solver");
			e.printStackTrace();
			System.exit(-1);
		}
		
		long now = System.currentTimeMillis();
		System.out.println("That took " + (now - then) + " millis.");
	}
	
	public static void main(String[] args) {
		
		/* Parse CMD args */
		CommandLineParser cliParser = new DefaultParser();
		Options opt = new Options();
		
		/* domain file */
		opt.addOption("d", true, "path to the domain file");
		
		/* log file */
		opt.addOption("l", true, "log file path");
		
		/* max policy tree depth for Mj */
		opt.addOption("h", true, "Max horizon for J's model");
		
		/* look ahead horizon for agent I */
		opt.addOption("m", true, "I's look ahead horizon");
		
		CommandLine line = null;
		BeliefUpdateViewer buViewer = null;
		
		try {
			line = cliParser.parse(opt, args);
			
			/* set CLI args */
			
			/* if log file is given, initialize logging accordingly */
			if (line.hasOption("l"))
				CustomConfigurationFactory.setLogFileName(line.getOptionValue("l"));
			
			CustomConfigurationFactory.initializeLogging();
			
			System.out.println("Starting BeliefUpdateViewer");
			buViewer = new BeliefUpdateViewer();
			
			/* set domain file */
			buViewer.domainFile = line.getOptionValue("d");
			
			/* set mjDepth */
			buViewer.mjDepth = new Integer(line.getOptionValue("h"));
			
			/* set look ahead */
			buViewer.mjLookAhead = new Integer(line.getOptionValue("m"));
		} 
		
		catch (ParseException e) {
			System.out.println("While parsing args: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		
		/* initialize */
		buViewer.initialize();
		
		/*
		 * run forever, or till your computer sparks into flames from computing millions 
		 * of state transitions
		 */
		buViewer.loop();
	}

}
