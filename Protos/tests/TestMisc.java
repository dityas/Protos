import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.FQDDleaf;
import thinclab.legacy.Global;
import thinclab.model_ops.belief_exploration.SSGAExploration;
import thinclab.models.POMDP;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.solver.QMDPSolver;
import thinclab.solver.SymbolicPerseusSolver;
import thinclab.spuddx_parser.SpuddXMainParser;

class TestMisc {

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(TestMisc.class);

    public BiFunction<DD, DD, Float> maxDist = 
        (d1, d2) -> DDOP.maxAll(DDOP.abs(DDOP.sub(d1, d2)));

    @BeforeEach
    void setUp() throws Exception {

        Global.clearAll();
    }

    @AfterEach
    void tearDown() throws Exception {

        Global.clearAll();
    }

    void printMemConsumption() throws Exception {

        var total = Runtime.getRuntime().totalMemory() / 1000000.0;
        var free = Runtime.getRuntime().freeMemory() / 1000000.0;

        LOGGER.info(String.format("Free mem: %s", free));
        LOGGER.info(String.format("Used mem: %s", (total - free)));
        Global.logCacheSizes();
    }

    @Test
    void testIndexSampling() throws Exception {

        String domainFile = this.getClass().getClassLoader()
            .getResource("test_domains/test_tiger_domain.spudd")
            .getFile();

        // Run domain
        var domainRunner = new SpuddXMainParser(domainFile);
        domainRunner.run();

        var model = (POMDP) domainRunner.getModel("agentI").get();

        var policy = 
            new SymbolicPerseusSolver<>().solve(
                    List.of(DDleaf.getDD(0.5f)), 
                    model, 100, 10, 
                    AlphaVectorPolicy.fromR(model.R()));

        var QMDPPolicy = QMDPSolver.solveQMDP(model);

        var expansionStrat = new SSGAExploration<>(0.2f);
        var G = ReachabilityGraph.fromDecMakingModel(model);
        G.addNode(DDleaf.getDD(0.5f));

        for (int j = 0; j < 10; j++) {
            expansionStrat.expand(
                    List.of(DDleaf.getDD(0.5f)), 
                    G, model, 10, policy);
        }

        var B = G.getAllNodes();
        LOGGER.info("Exploration collected %s beliefs", B.size());
        
        var ubEvals = 
            DDOP.getBeliefRegionEval(B, QMDPPolicy, model.i_S());

        var lbEvals = 
            DDOP.getBeliefRegionEval(B, policy, model.i_S());

        LOGGER.info("UB evals are %s", ubEvals);
        LOGGER.info("LB evals are %s", lbEvals);

        var evalDiffs = 
            DDOP.getBeliefRegionEvalDiff(
                    B, QMDPPolicy, policy, model.i_S());

        LOGGER.info("Diffs are %s", evalDiffs);

        for (int i = 0; i < 10; i++) {
            var sampled = DDOP.sample(evalDiffs);
            LOGGER.info("Sampled %s", sampled);

            assertTrue(sampled != -1);
            assertTrue(sampled < evalDiffs.size());
        }

        var distOne = List.of(500f, 120f);
        var samples = sampleFor1000Iter(distOne);

        var zeros = samples.stream().filter(i -> i == 0).count();
        var ones = samples.stream().filter(i -> i == 1).count();

        LOGGER.info("Sampling from %s gives %s 0 and %s 1",
                distOne, zeros, ones);

        var distMid = List.of(100f, 200f, 50f);
        samples = sampleFor1000Iter(distMid);

        zeros = samples.stream().filter(i -> i == 0).count();
        ones = samples.stream().filter(i -> i == 1).count();
        var twos = samples.stream().filter(i -> i == 2).count();

        LOGGER.info("Sampling from %s gives %s 0s, %s 1s, %s 2s",
                distMid, zeros, ones, twos);
    }

    private List<Integer> sampleFor1000Iter(List<Float> dist) {
        
        var indices = new ArrayList<Integer>();
        for (int i = 0; i < 1000; i++)
            indices.add(DDOP.sample(dist));

        return indices;
    }

    @Test
    void testReachabilityGraphs() throws Exception {

        String domainFile = this.getClass().getClassLoader()
            .getResource("test_domains/test_tiger_domain.spudd")
            .getFile();

        // Run domain
        var domainRunner = new SpuddXMainParser(domainFile);
        domainRunner.run();

        var model = (POMDP) domainRunner.getModel("agentI").get();

        var policy = AlphaVectorPolicy.fromR(model.R());
        var expansionStrat = new SSGAExploration<>(0.5f);

        var G = ReachabilityGraph.fromDecMakingModel(model);
        G.addNode(DDleaf.getDD(0.5f));

        for (int j = 0; j < 10; j++) {
            expansionStrat.expand(
                    List.of(DDleaf.getDD(0.5f)), 
                    G, model, 10, policy);
        }

        var cache = expansionStrat.getLikelihoodsCache();
        LOGGER.info("Cache has %s entries", cache.size());

        for (var dd: cache.keySet()) {

            var neighbors = cache.keySet().stream()
                .filter(d -> 
                        (DDOP.maxAll(
                                     DDOP.abs(
                                         DDOP.sub(
                                             d._0(), 
                                             dd._0()))) < 1e-4) 
                        && d._1() == dd._1())
                .count();

            LOGGER.info("DD has %s copies in the cache", neighbors);
            assertTrue(neighbors == 1);
        }
    }

    @Test
    void testDDQuantization() throws Exception {

        System.gc();

        LOGGER.info("Running Single agent tiger domain");
        String domainFile = this.getClass().getClassLoader()
            .getResource("test_domains/test_tiger_domain.spudd")
            .getFile();

        // Run domain
        var domainRunner = new SpuddXMainParser(domainFile);
        domainRunner.run();


        var testDDs = new HashSet<DD>();
        var testDDList = new ArrayList<DD>();

        var val = 0.0f;

        while (val < 1.0f) {

            var dd = DDnode.getDD(
                    1, 
                    new DD[] {DDleaf.getDD(val), DDleaf.getDD(1 - val)});

            testDDList.add(dd);
            val += 0.0001f;
        }

        testDDs.addAll(testDDList);

        LOGGER.info("DD set contains %s DDs", testDDs.size());

        var qDDs = testDDList.stream()
            .map(d -> FQDDleaf.quantize(d))
            .collect(Collectors.toList());

        var qDDSet = new HashSet<DD>();
        qDDSet.addAll(qDDs);

        LOGGER.info("Quantized DD set contains %s DDs", qDDSet.size());

        var diff = testDDList.stream()
            .map(d -> DDOP.sub(d, FQDDleaf.unquantize(FQDDleaf.quantize(d))))
            .map(d -> DDOP.maxAll(DDOP.abs(d)))
            .max((d1, d2) -> d1.compareTo(d2)).orElse(0.0f);

        var tolerance = diff - (1.0f / (float) FQDDleaf.BINS);

        LOGGER.info("Max error is: %s", diff);
        assertTrue(tolerance < 1e-5f);

    }
}
