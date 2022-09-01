
package thinclab.utils.types;

import java.util.function.Function;

final public class Left<L, R> extends Either<L, R> {

    public Left(L left) {
        super(left, null);
    }
    
    public static <L, R> Either<L, R> of(L left) {
        return new Left<L,R>(left);
    }
    
    public Either<?, R> map(Function<L, ?> func) {
        return Left.of(func.apply(left));
    }

    public Either<?, R> flatMap(Function<L, ? extends Either<?, R>> func) {
        return func.apply(left);
    }
}
