/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.belief.SSGABeliefExpansion;
import thinclab.belief.SparseFullBeliefExpansion;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.MAPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.parsers.IPOMDPParser;
import thinclab.simulations.MultiAgentSimulation;
import thinclab.simulations.StochasticSimulation;
import thinclab.solvers.BaseSolver;
import thinclab.solvers.DefaultActionPolicySolver;
import thinclab.solvers.OfflineSymbolicPerseus;
import thinclab.solvers.OnlineInteractiveSymbolicPerseus;
import thinclab.solvers.RandomActionPolicySolver;
import thinclab.utils.CustomConfigurationFactory;
import thinclab.utils.NextBelStateCache;

/*
 * @author adityas
 *
 */
@SuppressWarnings("unused")
class TestStateSimulator {

	public String l1DomainFile;
	public String l1DomainFileM;
	public String l0DomainFile;
	
	private static Logger LOGGER;
	
	@BeforeEach
	void setUp() throws Exception {
		CustomConfigurationFactory.initializeLogging();
		LOGGER = LogManager.getLogger(TestStateSimulator.class);
		this.l1DomainFileM = "/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt";
//		this.l0DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt";
		this.l1DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.L1.enemy.txt";
//		this.l0DomainFile = "/home/adityas/UGA/THINCLab/DomainFiles/final_domains/exfil.5S.L0.domain";
//		this.l0DomainFile = "/home/adityas/git/repository/Protos/domains/coffee3po.dat";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testStateSimInit() throws Exception {
		
		/* init L0 */
		POMDP pomdp = new POMDP(this.l0DomainFile);
		OfflineSymbolicPerseus S0 = 
				new OfflineSymbolicPerseus(
						pomdp, 
						new SSGABeliefExpansion(pomdp, 10, 10), 
						10, 100);
		
		S0.solve();
		
		StochasticSimulation SS = new StochasticSimulation(S0, 10);
		SS.runSimulation();
		
		LOGGER.info(SS.getJSONString());
		LOGGER.info(SS.getDotString());
	}
	
	@Test
	void testMultiAgentStateSimInit() throws Exception {
		
		/* init L1 */
		NextBelStateCache.useCache();
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFileM);
		parser.parseDomain();
		
		IPOMDP ipomdp = new IPOMDP(parser, 4, 10);
		
//		SSGABeliefExpansion BE = new SSGABeliefExpansion(ipomdp, 30);
		SparseFullBeliefExpansion BE = new SparseFullBeliefExpansion(ipomdp, 10);
		
		/* init solver */
		OnlineInteractiveSymbolicPerseus S1 = 
				new OnlineInteractiveSymbolicPerseus(
						ipomdp, 
						BE, 
						1, 100);
		
		/* init L0 */
		BaseSolver S0 = ipomdp.lowerLevelSolutions.get(0); 
				
//		String jAction = ipomdp.getActions().get(0) + "__" + pomdp.getActions().get(0);
//		String jAction = "listen__open-left";
		MultiAgentSimulation Sim = new MultiAgentSimulation(S1, S0, 10);
//		Sim.envStep(jAction);
		Sim.runSimulation();
		LOGGER.info(Sim.getDotString());
		LOGGER.info(Sim.getJSONString());
		
		Sim.logToFile("/tmp/res.json");
		
//		LOGGER.info(SS.getJSONString());
//		LOGGER.info(SS.getDotString());
	}
	
	@Test
	void testMultiAgentStateSimMAPOMDP() throws Exception {
		
		/* init L1 */
		NextBelStateCache.useCache();
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFileM);
		parser.parseDomain();
		
		MAPOMDP mapomdp = new MAPOMDP(parser, 10);
		
		SSGABeliefExpansion BE = new SSGABeliefExpansion((POMDP) mapomdp, 10, 30);
//		SparseFullBeliefExpansion BE = new SparseFullBeliefExpansion(ipomdp, 10);
		
		/* init solver */
		OfflineSymbolicPerseus S1 = new OfflineSymbolicPerseus(mapomdp, BE, 10, 100);
		S1.solve();
		
		/* init L0 */
		BaseSolver S0 = mapomdp.lowerLevelSolutions.get(0); 
				
//		String jAction = ipomdp.getActions().get(0) + "__" + pomdp.getActions().get(0);
//		String jAction = "listen__open-left";
		MultiAgentSimulation Sim = new MultiAgentSimulation(S1, S0, 10);
//		Sim.envStep(jAction);
		Sim.runSimulation();
		LOGGER.info(Sim.getDotString());
		LOGGER.info(Sim.getJSONString());
		
		Sim.logToFile("/tmp/res.json");
		
//		LOGGER.info(SS.getJSONString());
//		LOGGER.info(SS.getDotString());
	}
	
	@Test
	void testMultiAgentStateSimWithDefaultActionSolver() throws Exception {
		
		/* init L1 */
		NextBelStateCache.useCache();
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		IPOMDP ipomdp = new IPOMDP(parser, 4, 10);
		
//		SSGABeliefExpansion BE = new SSGABeliefExpansion(ipomdp, 30);
		SparseFullBeliefExpansion BE = new SparseFullBeliefExpansion(ipomdp, 10);
		
		/* init solver */
		DefaultActionPolicySolver DS = new DefaultActionPolicySolver(ipomdp, ipomdp.lowerLevelGuessForAi);
		
		/* init L0 */
		BaseSolver S0 = ipomdp.lowerLevelSolutions.get(0); 

		MultiAgentSimulation Sim = new MultiAgentSimulation(DS, S0, 5);

		Sim.runSimulation();
		LOGGER.info(Sim.getDotString());
		LOGGER.info(Sim.getJSONString());
		
		Sim.logToFile("/tmp/res.json");
	}
	
	@Test
	void testMultiAgentStateSimWithRandomActionSolver() throws Exception {
		
		/* init L1 */
		NextBelStateCache.useCache();
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		IPOMDP ipomdp = new IPOMDP(parser, 4, 10);
		
//		SSGABeliefExpansion BE = new SSGABeliefExpansion(ipomdp, 30);
		SparseFullBeliefExpansion BE = new SparseFullBeliefExpansion(ipomdp, 10);
		
		/* init solver */
		RandomActionPolicySolver RS = new RandomActionPolicySolver(ipomdp);
		
		/* init L0 */
		BaseSolver S0 = ipomdp.lowerLevelSolutions.get(0); 

		MultiAgentSimulation Sim = new MultiAgentSimulation(RS, S0, 5);

		Sim.runSimulation();
		LOGGER.info(Sim.getDotString());
		LOGGER.info(Sim.getJSONString());
		
		Sim.logToFile("/tmp/res.json");
	}

}
