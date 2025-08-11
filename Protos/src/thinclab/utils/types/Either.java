
package thinclab.utils.types;

import java.util.function.Function;

public class Either<L, R> {

    final L left;
    final R right;

    Either(L left, R right) {
        
        this.left = left;
        this.right = right;
    }

    public Either<?, R> leftMap(Function<L, ?> func) {
        if (this instanceof Left<L, R> l)
            return l.map(func);
        else
            return this;
    }
    
    public Either<L, ?> rightMap(Function<R, ?> func) {
        if (this instanceof Right<L, R> r)
            return r.map(func);
        else
            return this;
    }

    public Either<?, R> leftFlatMap(
            Function<L, ? extends Either<?, R>> func) {
        if (this instanceof Left<L, R> l)
            return l.map(func);
        else
            return this;
    }
    
    public Either<L, ?> rightFlatMap(
            Function<R, ? extends Either<L, ?>> func) {
        if (this instanceof Right<L, R> r)
            return r.map(func);
        else
            return this;
    }
    
    public L left() {
        return left;
    }

    public R right() {
        return right;
    }

    @Override
    public String toString() {

        if (this instanceof Left<L, R> l)
            return new StringBuilder()
                .append("(left ")
                .append(left)
                .append(")").toString();

        else 
            return new StringBuilder()
                .append("(right ")
                .append(right)
                .append(")").toString();
    }
}
