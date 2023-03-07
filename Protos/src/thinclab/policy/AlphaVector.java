
package thinclab.policy;

import thinclab.legacy.DD;
import thinclab.utils.Tuple3;

public class AlphaVector extends Tuple3<Integer, DD, DD> {

    public AlphaVector(int action, DD vec, DD witness) {
        super(action, vec, witness);
    }

    public int getActId() {
        return _0();
    }

    public DD getVector() {
        return _1();
    }

    public DD getWitness() {
        return _2();
    }

}
