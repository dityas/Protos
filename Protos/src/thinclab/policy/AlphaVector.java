
package thinclab.policy;

import thinclab.legacy.DD;
import thinclab.utils.Tuple3;

public class AlphaVector extends Tuple3<Integer, DD, Float> {

    public AlphaVector(int action, DD vec, float value) {
        super(action, vec, value);
    }

    public int getActId() {
        return _0();
    }

    public DD getVector() {
        return _1();
    }

    public float getVal() {
        return _2();
    }

}
