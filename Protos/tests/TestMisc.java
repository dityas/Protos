import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;
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
import thinclab.legacy.Global;
import thinclab.model_ops.belief_exploration.MDPExploration;
import thinclab.models.POMDP;
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
            new SymbolicPerseusSolver<>(model).solve(
                    List.of(DDleaf.getDD(0.5f)), 
                    100, 10);

        var QMDPPolicy = QMDPSolver.solveQMDP(model);

        var expansionStrat = new MDPExploration<>(QMDPPolicy, 0.1f);
        var exploredRegion = 
            expansionStrat.explore(
                    List.of(DDleaf.getDD(0.5f)), 
                    model, 10, 10);

        var B = exploredRegion.getAllNodes()
            .stream()
            .collect(Collectors.toList());
        LOGGER.info("Exploration collected %s beliefs", B.size());
        
        var ubEvals = 
            DDOP.getBeliefRegionEval(B, QMDPPolicy);

        var lbEvals = 
            DDOP.getBeliefRegionEval(B, policy);

        LOGGER.info("UB evals are %s", ubEvals);
        LOGGER.info("LB evals are %s", lbEvals);

        var evalDiffs = 
            DDOP.getBeliefRegionEvalDiff(
                    B, QMDPPolicy, policy);

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

        var policy = AlphaVectorPolicy.getLowerBound(model);
        var expansionStrat = new MDPExploration<>(policy, 0.1f);
        var exploredRegion = 
            expansionStrat.explore(
                    List.of(DDleaf.getDD(0.5f)), 
                    model, 10, 10);

        var B = exploredRegion.getAllNodes()
            .stream()
            .collect(Collectors.toList());
        LOGGER.info("Exploration collected %s beliefs", B.size());

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
    void testDDEquality() throws Exception {

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

        LOGGER.info("DD list contains %s DDs and DD set contains %s",
                testDDList.size(), testDDs.size());

        var dd1 = testDDs.parallelStream()
            .reduce(DD.zero, (d1, d2) -> DDOP.add(d1, d2));

        Global.clearHashtables();
        var dd2 = testDDList.parallelStream()
            .reduce(DD.zero, (d1, d2) -> DDOP.add(d1, d2));

        LOGGER.info("DD 1 is %s and DD2 is %s", dd1, dd2);

        var equality = dd1.equals(dd2);
        LOGGER.info("Equality is %s", equality);

        var hashDD1 = dd1.hashCode();
        var hashDD2 = dd2.hashCode();

        LOGGER.info("Hashes are %s and %s", hashDD1, hashDD2);
        LOGGER.info("Addresses are %s and %s", 
                dd1.getAddress(), dd2.getAddress());
    
    }

}
