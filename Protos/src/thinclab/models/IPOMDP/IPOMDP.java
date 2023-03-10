/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.models.IPOMDP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.DDOP;
import thinclab.RandomVariable;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.models.Model;
import thinclab.models.PBVISolvablePOMDPBasedModel;
import thinclab.models.datastructures.PolicyGraph;
import thinclab.models.datastructures.PolicyNode;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.models.datastructures.ReachabilityNode;
import thinclab.utils.Tuple;
import thinclab.utils.Tuple3;

/*
 * @author adityas
 *
 */
public class IPOMDP extends PBVISolvablePOMDPBasedModel {

    public final int H;

    public final List<String> Aj;
    public final int i_Aj;
    public final int i_Mj;
    public final int i_Mj_p;
    public final int i_EC;
    public final int i_EC_p;
    public final int i_Thetaj;
    public final List<String> Thetaj;

    public final List<Integer> mjVars;

    public final List<String> Omj;

    public final List<Integer> i_Omj;
    public final List<Integer> i_Omj_p;

    public final List<Integer> allvars;
    public final List<Integer> gaoivars;

    public final List<DD> jointR;

    public final List<List<DD>> Oj;

    public DD PAjGivenEC;
    public DD PEC_pGivenECAjOj_p;
    public DD PThetajGivenEC;
    public List<DD> Taus;

    public List<Tuple3<Integer, PBVISolvablePOMDPBasedModel, List<ReachabilityNode>>> framesj;
    // public List<BjSpace> framesjSoln;
    public List<MjThetaSpace> ecThetas;
    public HashMap<MjRepr<PolicyNode>, String> ECMap = new HashMap<>();
    public HashMap<String, String> mjToECMap = new HashMap<>();

    private static final Logger LOGGER = 
        LogManager.getFormatterLogger(IPOMDP.class);


    public IPOMDP(IPOMDP i) {

        super(i.S, i.O, Global.varNames.get(i.i_A - 1), 
                i.TF, i.OF, i.R, i.discount);

        jointR = i.jointR;

        this.name = i.name;

        // random variable for opponent's action
        this.i_Aj = i.i_Aj;
        this.Aj = i.Aj;

        // opponent's model variable
        this.i_Mj = i.i_Mj;
        this.i_Mj_p = i.i_Mj_p;

        this.i_EC = i.i_EC;
        this.i_EC_p = i.i_EC_p;

        this.i_S.add(i_EC);
        this.i_S_p.add(i_EC_p);

        // random variable for frame of the opponent
        this.i_Thetaj = i.i_Thetaj;
        this.Thetaj = i.Thetaj;

        this.H = i.H;
        mjVars = i.mjVars;

        Omj = i.Omj;
        i_Omj = i.i_Omj;
        i_Omj_p = i.i_Omj_p;

        Oj = i.Oj;

        allvars = i.allvars;
        gaoivars = i.gaoivars;

        ecThetas = i.ecThetas;
        framesj = i.framesj;

        PAjGivenEC = i.PAjGivenEC;
        PEC_pGivenECAjOj_p = i.PEC_pGivenECAjOj_p;
        PThetajGivenEC = i.PThetajGivenEC;
        Taus = i.Taus;

        ECMap.putAll(i.ECMap);
        mjToECMap.putAll(i.mjToECMap);

    }

    public void solveOpponent() {

        // create interactive state space using mjs
        updateIS();

        // populate mj to EC map
        Global.modelVars.get(Global.varNames.get(i_Mj - 1)).entrySet().stream().forEach(e ->
                {

                    int frameId = e.getKey().frame;
                    var bel = e.getKey().m.beliefs.stream().findAny().get();
                    var frame = ecThetas.get(frameId);

                    if (frame.m instanceof IPOMDP)
                        bel = ((IPOMDP) frame.m).getECDDFromMjDD(bel);

                    int alphaId = DDOP.bestAlphaIndex(frame.Vn.aVecs, bel, frame.m.i_S());
                    //				int actId = frame.Vn.getBestActionIndex(bel, frame.m.i_S());
                    int actId = frame.Vn.aVecs.get(alphaId)._0();

                    var node = new MjRepr<>(frameId, new PolicyNode(alphaId, actId, frame.m.A().get(actId)));
                    var ec = ECMap.get(node);

                    if (ec == null) {

                        LOGGER.error(String.format("Panic! Could not find equivalence class for belief %s", e.getValue()));
                        LOGGER.debug(String.format("actId is %s, alphaId %s and frameId %s, act %s", 
                                    actId, alphaId, frameId, frame.m.A().get(actId)));
                        LOGGER.debug(String.format("EC map is %s", ECMap));
                        LOGGER.debug(String.format("Belief is %s", bel));

                        DDOP.printValuesForBelief(frame.Vn.aVecs, bel, mjVars, frame.m.A());

                        System.exit(-1);
                    }

                    mjToECMap.put(e.getValue(), ECMap.get(node));
                });
    }

    public IPOMDP(List<String> S, List<String> O, String A, 
            String Aj, String Mj, String EC, String Thetaj,
            List<Tuple<String, Model>> frames_j, 
            List<List<DD>> TF, List<List<DD>> OF, List<DD> R, 
            float discount,
            int H, String name) {

        super(S, O, A, TF, OF, R, discount);

        jointR = new ArrayList<DD>(this.R.size());
        jointR.addAll(this.R);

        this.name = name;

        // random variable for opponent's action
        this.i_Aj = Global.varNames.indexOf(Aj) + 1;
        this.Aj = Global.valNames.get(i_Aj - 1);

        // opponent's model variable
        this.i_Mj = Global.varNames.indexOf(Mj) + 1;
        this.i_Mj_p = this.i_Mj + (Global.NUM_VARS / 2);

        this.i_EC = Global.varNames.indexOf(EC) + 1;
        this.i_EC_p = this.i_EC + (Global.NUM_VARS / 2);

        this.i_S.add(i_EC);
        this.i_S_p.add(i_EC_p);

        // random variable for frame of the opponent
        this.i_Thetaj = Global.varNames.indexOf(Thetaj) + 1;
        this.Thetaj = Global.valNames.get(i_Thetaj - 1);

        this.H = H;

        var _mjVars = new ArrayList<>(i_S());
        _mjVars.remove(_mjVars.size() - 1);
        _mjVars.add(i_Mj);

        mjVars = _mjVars;

        // initialize frames
        this.framesj = frames_j.stream()
            // make tuples (frameId, POMDP/IPOMDP)
            .map(t -> Tuple.of(Global.valNames
                        .get(i_Thetaj - 1).indexOf(t._0()),
                        (PBVISolvablePOMDPBasedModel) t._1()))
            // make tuples (frameId, POMDP/IPOMDP, beliefs 
            // (reachability nodes))
            .map(t -> Tuple.of(t._0(), t._1(),
                        Global.modelVars
                        .get(Global.varNames
                            .get(i_Mj - 1)).keySet().stream()
                        .filter(k -> k.frame == t._0())
                        .map(k -> k._1())
                        .collect(Collectors.toList())))
            .collect(Collectors.toList());

        Collections.sort(this.framesj, (f1, f2) -> f1._0() - f2._0());

        // verify and prepare Oj
        var incorrectFrame = this.framesj.stream()
            .filter(f -> !f._1().Om().equals(
                        this.framesj.get(0)._1().Om()))
            .findAny();

        if (incorrectFrame.isPresent()) {

            LOGGER.error(String.format(
                        "All frame should have the same set of observation variables. %s seems to be different.",
                        incorrectFrame.get()));
            System.exit(-1);
        }

        // prepare opponent's observations and obs functions
        Omj = this.framesj.get(0)._1().Om();
        i_Omj = this.framesj.get(0)._1().i_Om();
        i_Omj_p = this.framesj.get(0)._1().i_Om_p();

        var OjRestrictedAi = IntStream
            .range(0, A().size()).mapToObj(i -> this.framesj.stream()
                    .map(f -> this.prepareOj_pGivenAjAiS_p(i + 1, f._1().O())).collect(Collectors.toList()))
            .collect(Collectors.toList());

        Oj = IntStream
            .range(0,
                    A().size())
            .mapToObj(i -> IntStream.range(0, Omj.size())
                    .mapToObj(o -> DDnode.getDD(i_Thetaj,
                            IntStream.range(0, this.framesj.size())
                            .mapToObj(f -> OjRestrictedAi.get(i).get(f).get(o)).toArray(DD[]::new)))
                    .collect(Collectors.toList()))
            .collect(Collectors.toList());

        allvars = new ArrayList<Integer>();
        allvars.addAll(i_S());
        allvars.add(i_Aj);
        allvars.add(i_Thetaj);
        allvars.addAll(i_Omj_p);

        Collections.sort(allvars);

        gaoivars = new ArrayList<Integer>();
        gaoivars.addAll(i_S_p());
        gaoivars.add(i_Aj);
        gaoivars.add(i_Thetaj);

        Collections.sort(gaoivars);

        this.ecThetas = this.framesj.stream()
            .map(f -> new MjThetaSpace(
                        f._2().stream().flatMap(n -> n.beliefs.stream()).collect(Collectors.toList()), f._0(), f._1()))
            .collect(Collectors.toList());

        solveOpponent();
    }

    public IPOMDP(List<String> S, List<String> O, String A, 
            String Aj, String Mj, String EC, String Thetaj,
            List<Tuple<String, Model>> frames_j, 
            HashMap<String, Model> dynamics, HashMap<String, DD> R, 
            float discount,
            int H, String name) {

        // initialize dynamics like POMDP
        super(S, O, A, dynamics, R, discount);

        jointR = new ArrayList<DD>(this.R.size());
        jointR.addAll(this.R);

        this.name = name;

        // random variable for opponent's action
        this.i_Aj = Global.varNames.indexOf(Aj) + 1;
        this.Aj = Global.valNames.get(i_Aj - 1);

        // opponent's model variable
        this.i_Mj = Global.varNames.indexOf(Mj) + 1;
        this.i_Mj_p = this.i_Mj + (Global.NUM_VARS / 2);

        this.i_EC = Global.varNames.indexOf(EC) + 1;
        this.i_EC_p = this.i_EC + (Global.NUM_VARS / 2);

        this.i_S.add(i_EC);
        this.i_S_p.add(i_EC_p);

        // random variable for frame of the opponent
        this.i_Thetaj = Global.varNames.indexOf(Thetaj) + 1;
        this.Thetaj = Global.valNames.get(i_Thetaj - 1);

        this.H = H;

        var _mjVars = new ArrayList<>(i_S());
        _mjVars.remove(_mjVars.size() - 1);
        _mjVars.add(i_Mj);

        mjVars = _mjVars;

        // initialize frames
        this.framesj = frames_j.stream()
            // make tuples (frameId, POMDP/IPOMDP)
            .map(t -> Tuple.of(Global.valNames.get(i_Thetaj - 1).indexOf(t._0()),
                        (PBVISolvablePOMDPBasedModel) t._1()))
            // make tuples (frameId, POMDP/IPOMDP, beliefs (reachability nodes))
            .map(t -> Tuple.of(t._0(), t._1(),
                        Global.modelVars.get(Global.varNames.get(i_Mj - 1)).keySet().stream()
                        .filter(k -> k.frame == t._0()).map(k -> k._1()).collect(Collectors.toList())))
            .collect(Collectors.toList());

        Collections.sort(this.framesj, (f1, f2) -> f1._0() - f2._0());

        // verify and prepare Oj
        var incorrectFrame = this.framesj.stream().filter(f -> !f._1().Om().equals(this.framesj.get(0)._1().Om()))
            .findAny();

        if (incorrectFrame.isPresent()) {

            LOGGER.error(String.format(
                        "All frame should have the same set of observation variables. %s seems to be different.",
                        incorrectFrame.get()));
            System.exit(-1);
        }

        // prepare opponent's observations and obs functions
        Omj = this.framesj.get(0)._1().Om();
        i_Omj = this.framesj.get(0)._1().i_Om();
        i_Omj_p = this.framesj.get(0)._1().i_Om_p();

        var OjRestrictedAi = IntStream
            .range(0, A().size()).mapToObj(i -> this.framesj.stream()
                    .map(f -> this.prepareOj_pGivenAjAiS_p(i + 1, f._1().O())).collect(Collectors.toList()))
            .collect(Collectors.toList());

        Oj = IntStream
            .range(0,
                    A().size())
            .mapToObj(i -> IntStream.range(0, Omj.size())
                    .mapToObj(o -> DDnode.getDD(i_Thetaj,
                            IntStream.range(0, this.framesj.size())
                            .mapToObj(f -> OjRestrictedAi.get(i).get(f).get(o)).toArray(DD[]::new)))
                    .collect(Collectors.toList()))
            .collect(Collectors.toList());

        allvars = new ArrayList<Integer>();
        allvars.addAll(i_S());
        allvars.add(i_Aj);
        allvars.add(i_Thetaj);
        allvars.addAll(i_Omj_p);

        Collections.sort(allvars);

        gaoivars = new ArrayList<Integer>();
        gaoivars.addAll(i_S_p());
        gaoivars.add(i_Aj);
        gaoivars.add(i_Thetaj);

        Collections.sort(gaoivars);

        this.ecThetas = this.framesj.stream()
            .map(f -> new MjThetaSpace(
                        f._2().stream().flatMap(n -> n.beliefs.stream()).collect(Collectors.toList()), f._0(), f._1()))
            .collect(Collectors.toList());

        solveOpponent();
    }

    public List<DD> prepareOj_pGivenAjAiS_p(int ai, List<List<DD>> _Oj) {

        var Oj_ = _Oj.stream().map(oja -> oja.stream().map(oj -> DDOP.restrict(oj, List.of(i_A), List.of(ai)))
                .collect(Collectors.toList())).collect(Collectors.toList());

        var Oj_pGivenAjAiS_p = IntStream.range(0, Omj.size())
            .mapToObj(i -> DDOP.reorder(DDnode.getDD(i_Aj,
                            IntStream.range(0, Aj.size()).mapToObj(j -> Oj_.get(j).get(i)).toArray(DD[]::new))))
            .collect(Collectors.toList());

        return Oj_pGivenAjAiS_p;
    }

    public DD getECDDFromMjDD(DD mjDD) {

        var enumerated = DDOP.getEnumeratedFactor(mjDD, i_Mj);

        // accumulate
        var _ecDDMap = Global.valNames.get(i_EC - 1).stream().collect(Collectors.toMap(v -> v, v -> DD.zero));

        IntStream.range(0, Global.valNames.get(i_Mj - 1).size()).forEach(i ->
                {

                    var mj = Global.valNames.get(i_Mj - 1).get(i);
                    var ec = mjToECMap.get(mj);
                    var dd = _ecDDMap.get(ec);

                    _ecDDMap.put(ec, DDOP.add(dd, enumerated.get(i)));
                });

        var ecDD = DDnode.getDD(i_EC,
                Global.valNames.get(i_EC - 1).stream().map(v -> _ecDDMap.get(v)).toArray(DD[]::new));

        return DDOP.reorder(ecDD);
    }

    public void updateIS() {

        createIS();
        createPAjEC();
        createPThetajEC();
        createPEC_pECAjOj_p();

        R = jointR.stream().map(jr -> DDOP.addMultVarElim(List.of(PAjGivenEC, jr), List.of(i_Aj)))
            .collect(Collectors.toList());
    }

    // ------------------------------------------------------------
    // Helper functions for extracting relevant structures from
    // lower level opponents
    public static void getIS() {


    }

    // Gets combined Mj set for multiple frames
    public static List<MjRepr<PolicyNode>> getOpponentModels(
            Collection<Tuple<Integer, PolicyGraph>> frames) {

        return frames.stream()
            .flatMap(f -> getOpponentModelsForFrame(
                        f._0(), f._1()).stream())
            .collect(Collectors.toList());
            }

    // Get Mj set for a single frame from policy graph
    // Should be extended to belief graph for non-EC IPOMDPs
    public static List<MjRepr<PolicyNode>> getOpponentModelsForFrame(
            int frame, PolicyGraph G) {

        return G.adjMap.keySet().stream()
            .map(n -> new MjRepr<>(frame, G.nodeMap.get(n)))
            .collect(Collectors.toList());
            }

    public static RandomVariable getMjAsRandomVariable(
            Collection<MjRepr<?>> MjSet) {


        return null;
            }

    public void createIS() {

        var ecList = this.ecThetas.stream().flatMap(m -> m.allModels().stream()).collect(Collectors.toList());

        IntStream.range(0, ecList.size()).forEach(i ->
                {

                    ECMap.put(ecList.get(i), String.format("ec%s", i));
                });

        // Make EC variable
        var sortedECs = ECMap.values().stream().collect(Collectors.toList());
        Collections.sort(sortedECs);

        Global.replaceValues(i_EC, sortedECs);
        Global.replaceValues(i_EC_p, sortedECs);

        LOGGER.debug(
                String.format("Eq class space for %s has %s models", getName(), Global.valNames.get(i_EC - 1).size()));
        LOGGER.debug(String.format("IPOMDP %s has %s interactive states in total", getName(),
                    i_S().stream().map(i -> Global.valNames.get(i - 1).size()).reduce(1, (p, q) -> p * q)));

    }

    public void createPAjEC() {

        var optAGivenEC = ecThetas.stream().flatMap(mj -> mj.allModels().stream())
            .collect(Collectors.toMap(mj -> ECMap.get(mj), mj -> DDnode.getDDForChild(i_Aj, mj._1().actId)));

        var AjDDs = Global.valNames.get(i_EC - 1).stream().map(v -> optAGivenEC.get(v)).toArray(DD[]::new);

        PAjGivenEC = DDOP.reorder(DDnode.getDD(i_EC, AjDDs));

        // Sanity check
        IntStream.range(0, Global.valNames.get(i_EC - 1).size()).forEach(i ->
                {

                    var dd = DDOP.restrict(PAjGivenEC, List.of(i_EC), List.of(i + 1));
                    if (DDOP.maxAll(DDOP
                                .abs(DDOP.sub(DDOP.addMultVarElim(List.of(dd), List.of(i_Aj)), DDleaf.getDD(1.0f)))) > 1e-5f) {

                        LOGGER.debug(String.format("Panic! Sanity check for P(Aj|EC) failed, P(Aj|EC) = %s", PAjGivenEC));
                        System.exit(-1);

                                }
                });

        LOGGER.info("Sanity check passed for P(Aj|EC)");
    }

    public void createPThetajEC() {

        var mjToThetaj = ecThetas.stream().flatMap(mj -> mj.allModels().stream())
            .collect(Collectors.toMap(mj -> ECMap.get(mj), mj -> DDnode.getDDForChild(i_Thetaj, mj.frame)));

        var thetajDDs = Global.valNames.get(i_EC - 1).stream().map(v -> mjToThetaj.get(v)).toArray(DD[]::new);

        PThetajGivenEC = DDOP.reorder(DDnode.getDD(i_EC, thetajDDs));

        // Sanity check
        IntStream.range(0, Global.valNames.get(i_EC - 1).size()).forEach(i ->
                {

                    var dd = DDOP.restrict(PThetajGivenEC, List.of(i_EC), List.of(i + 1));
                    if (DDOP.maxAll(DDOP.abs(
                                    DDOP.sub(DDOP.addMultVarElim(List.of(dd), List.of(i_Thetaj)), DDleaf.getDD(1.0f)))) > 1e-5f) {

                        LOGGER.debug(String.format("Panic! Sanity check for P(Thetaj|EC) failed, P(Thetaj|EC) = %s",
                                    PThetajGivenEC));
                        System.exit(-1);

                                    }
                });

        LOGGER.info("Sanity check passed for P(Thetaj|EC)");
    }

    public void createPEC_pECAjOj_p() {

        var then = System.nanoTime();

        // var mjMap = Global.modelVars.get(Global.varNames.get(i_Mj - 1));

        var triples = ecThetas.stream().flatMap(f -> f.getTriples().stream())
            .map(f -> Tuple.of(ECMap.get(f._0()), f._1(), ECMap.get(f._2()))).map(t ->
                    {

                        // convert everything into list
                        List<Integer> valList = new ArrayList<>(t._1().size() + 2);
                        valList.add(Global.valNames.get(i_EC - 1).indexOf(t._0()) + 1);
                        valList.addAll(t._1());
                        valList.add(Global.valNames.get(i_EC_p - 1).indexOf(t._2()) + 1);

                        return valList;
                    })
        .collect(Collectors.toList());

        var varList = new ArrayList<Integer>(Omj.size() + 2);
        varList.add(i_EC);
        varList.add(i_Aj);
        varList.addAll(i_Omj_p);
        varList.add(i_EC_p);

        PEC_pGivenECAjOj_p = triples.stream().map(t -> DDOP.reorder(DDnode.getDD(varList, t, 1.0f)))
            .reduce(DDleaf.getDD(0.0f), (d1, d2) -> DDOP.add(d1, d2));

        var ddSum = DDOP.addMultVarElim(List.of(PEC_pGivenECAjOj_p), List.of(i_EC_p));
        if (DDOP.maxAll(DDOP.abs(DDOP.sub(ddSum, DD.one))) > 1e-5f) {

            LOGGER.debug(String.format("Models are %s", Global.modelVars));
            LOGGER.error(String.format("# (EC') P(EC'|EC, Aj, Oj') is %s != 1", ddSum));
            System.exit(-1);
        }

        LOGGER.info("Sanity check passed for P(EC'|EC, Aj, Oj')");

        Taus = IntStream.range(0, A().size()).mapToObj(i ->
                {

                    var factors = new ArrayList<DD>(Omj.size() + 1);
                    factors.addAll(Oj.get(i));
                    factors.add(PEC_pGivenECAjOj_p);

                    return DDOP.addMultVarElim(factors, i_Omj_p);
                }).collect(Collectors.toList());

        var now = System.nanoTime();
        LOGGER.debug(String.format("Building P(EC'|EC, Aj, Oj') for %s took %s msecs", getName(),
                    ((now - then) / 1000000.0)));
    }

    @Override
    public DD beliefUpdate(DD b, int a, List<Integer> o) {

        var Ofao = DDOP.restrict(O().get(a), i_Om_p, o);

        var factors = new ArrayList<DD>(S().size() + S().size() + Omj.size() + 3);

        factors.add(b);
        factors.add(PAjGivenEC);
        factors.add(PThetajGivenEC);
        factors.add(Taus.get(a));
        factors.addAll(T().get(a));
        factors.addAll(Ofao);

        var vars = new ArrayList<Integer>(factors.size());
        vars.addAll(i_S());
        vars.add(i_Thetaj);
        vars.add(i_Aj);

        var b_p = DDOP.primeVars(DDOP.addMultVarElim(factors, vars), -(Global.NUM_VARS / 2));
        var stateVars = new ArrayList<Integer>(i_S());

        var prob = DDOP.addMultVarElim(List.of(b_p), stateVars);

        if (DDOP.abs(DDOP.sub(prob, DD.zero)).getVal() < 1e-6)
            return DD.zero;

        b_p = DDOP.div(b_p, prob);

        return b_p;
    }

    @Override
    public DD beliefUpdate(DD b, String a, List<String> o) {

        int actIndex = Collections.binarySearch(this.A, a);
        var obs = new ArrayList<Integer>(i_Om.size());

        for (int i = 0; i < i_Om_p.size(); i++) {

            obs.add(Collections.binarySearch(Global.valNames.get(i_Om.get(i) - 1), o.get(i)) + 1);
        }

        return this.beliefUpdate(b, actIndex, obs);
    }

    @Override
    public DD obsLikelihoods(DD b, int a) {

        // Do not forget to clear cache after every IPOMDP step
        var key = Tuple.of(b, a);
        if (lCache.containsKey(key))
            return lCache.get(key);

        var factors = new ArrayList<DD>(S().size() + S().size() + Omj.size() + 3);

        factors.add(b);
        factors.add(PAjGivenEC);
        factors.add(PThetajGivenEC);
        factors.add(Taus.get(a));
        factors.addAll(T().get(a));
        factors.addAll(O().get(a));

        var vars = new ArrayList<Integer>(factors.size());
        vars.addAll(i_S());
        // vars.addAll(i_Omj_p);
        vars.add(i_Aj);
        vars.add(i_Thetaj);
        vars.addAll(i_S_p());

        var l = DDOP.addMultVarElim(factors, vars);

        lCache.clearIfFull();
        lCache.put(key, l);

        return l;
    }

    // ----------------------------------------------------------------------------------------
    // MjSpace transformations

    @Override
    public void step() {

    }

    @Override
    public DD step(DD b, int a, List<Integer> o) {

        var b_n = beliefUpdate(b, a, o);
        return b_n;
    }

    @Override
    public DD step(DD b, String a, List<String> o) {

        return null;
    }

    // ----------------------------------------------------------------------------------------
    // PBVISolvable implementations

    public Tuple<Float, Integer> Gaoi(final DD b, final int a, final List<Integer> o, final List<DD> alphas) {

        float bestVal = Float.NEGATIVE_INFINITY;
        int bestAlpha = -1;

        for (int i = 0; i < alphas.size(); i++) {

            float val = DDOP.dotProduct(b, alphas.get(i), i_S());
            if (val >= bestVal) {

                bestVal = val;
                bestAlpha = i;

            }

        }

        return Tuple.of(bestVal, bestAlpha);
    }

    public DD project(DD d, int a, List<Integer> o) {

        var factors = new ArrayList<DD>(S.size() + Om().size() + 1);
        factors.addAll(DDOP.restrict(O().get(a), i_Om_p, o));
        factors.addAll(T().get(a));
        factors.add(PAjGivenEC);
        factors.add(PThetajGivenEC);
        factors.add(Taus.get(a));
        factors.add(DDOP.primeVars(d, Global.NUM_VARS / 2));

        var results = DDOP.addMultVarElim(factors, gaoivars);

        return results;
    }

    public List<Tuple3<Integer, DD, Float>> computeNextBaParallel(DD b, DD likelihoods, int a, ReachabilityGraph g) {

        var res = IntStream.range(0, oAll.size()).parallel().boxed()
            .map(o -> Tuple.of(DDOP.restrict(likelihoods, i_Om_p, oAll.get(o)).getVal(), o))
            .filter(o -> o._0() > 1e-6f).map(o ->
                    {
                        var b_n = beliefUpdate(b, a, oAll.get(o._1()));
                        return Tuple.of(o._1(), b_n, o._0());
                    })
        .collect(Collectors.toList());

        return res;
    }

    public Tuple3<Integer, DD, Float> getBestAlpha(Tuple3<Integer, DD, Float> nextBa, List<DD> alphas, int a) {

        var obsIndex = nextBa._0();
        var b_n = nextBa._1();
        var prob = nextBa._2();

        var bestAlpha = Gaoi(b_n, a, oAll.get(obsIndex), alphas);

        return Tuple.of(obsIndex, alphas.get(bestAlpha._1()), (prob * bestAlpha._0()));

    }

    public Tuple<Integer, DD> backup(DD b, List<DD> alphas, ReachabilityGraph g) {

        int bestA = -1;
        float bestVal = Float.NEGATIVE_INFINITY;

        // float quantization
        var nextBels = belCache.get(b);

        if (nextBels == null) {

            // build cache entry for this belief
            nextBels = new HashMap<Integer, List<Tuple3<Integer, DD, Float>>>(A().size());

            var res = IntStream.range(0, A().size()).parallel().boxed()
                .map(a -> Tuple.of(a, 
                            computeNextBaParallel(
                                b, obsLikelihoods(b, a), a, g)))
                .collect(Collectors.toList());

            for (var r : res)
                nextBels.put(r._0(), r._1());

            // Cache computed entry
            belCache.put(b, nextBels);

        }

        // compute everything from the cached entries
        var Gao = new ArrayList<ArrayList<Tuple<Integer, DD>>>(A().size());

        for (int a = 0; a < A().size(); a++) {

            var nextBa = nextBels.get(a);
            float val = 0.0f;

            var _a = a;
            var argmaxGois = nextBa.parallelStream()
                .map(nb -> getBestAlpha(nb, alphas, _a))
                .collect(Collectors.toList());

            var argmax_iGaoi = argmaxGois.stream()
                .map(a_ -> Tuple.of(a_._0(), a_._1()))
                .collect(Collectors.toList());

            val = argmaxGois.stream()
                .map(a_ -> a_._2())
                .reduce(0.0f, (a1_, a2_) -> a1_ + a2_);

            val *= discount;
            val += DDOP.dotProduct(b, R().get(a), i_S());

            if (val >= bestVal) {

                bestVal = val;
                bestA = a;
            }

            Gao.add((ArrayList<Tuple<Integer, DD>>) argmax_iGaoi);

        }

        var vec = constructAlphaVector(Gao.get(bestA), bestA);
        var newVec = DDOP.add(R().get(bestA), DDOP.mult(DDleaf.getDD(discount), vec));

        return Tuple.of(bestA, DDOP.approximate(newVec));
    }

    private DD constructAlphaVector(ArrayList<Tuple<Integer, DD>> Gao, int bestA) {

        var vec = Gao.parallelStream().map(g -> project(g._1(), bestA, oAll.get(g._0()))).reduce(DD.zero,
                (a, b) -> DDOP.add(a, b));

        return vec;
    }

    private DD project(int a, DD vec) {

        var ddArray = new ArrayList<DD>();
        ddArray.addAll(TF.get(a));
        ddArray.add(Taus.get(a));
        ddArray.add(DDOP.primeVars(vec, (Global.NUM_VARS / 2)));

        var dd = DDOP.approximate(DDOP.addMultVarElim(ddArray, i_S_p));
        return DDOP.addMultVarElim(List.of(PAjGivenEC, dd), List.of(i_Aj));
    }

    @Override
    public List<DD> MDPValueIteration(List<DD> Qfn) {

        // Vn = max_a[Qfn]
        DD Vn = Qfn.stream()
            .reduce(
                    DDleaf.getDD(Float.NEGATIVE_INFINITY),
                    (q1, q2) -> DDOP.max(q1, q2));

        // Vn = R(a) + gamma * Sumout[S'][P(S'|S, a) * Tau(a) * Vn']
        int A = this.A.size();
        var gamma = DDleaf.getDD(discount);
        List<DD> nextVn = IntStream.range(0, A).boxed().parallel()
            .map(a -> Tuple.of(
                        R.get(a), 
                        project(a, Vn)))
            .map(q -> DDOP.add(q._0(), DDOP.mult(gamma, q._1())))
            .collect(Collectors.toList());

        return nextVn;
    }

}
