package thinclab;


public sealed interface ADD permits ADDNode, ADDLeaf {

    public static ADD of(float val) {
        return new ADDLeaf(val);
    }

    public static ADD of(int X, ADD[] vals) {
        return new ADDNode(X, vals);
    }
}

record ADDNode(int X, ADD[] vals) implements ADD {}
record ADDLeaf(float val) implements ADD {}

