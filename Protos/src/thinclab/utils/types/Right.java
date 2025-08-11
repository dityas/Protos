
package thinclab.utils.types;

import java.util.function.Function;

final public class Right<L, R> extends Either<L, R> {

    public Right(R right) {
        super(null, right);
    }

    public static <L, R> Either<L, R> of(R right) {
        return new Right<L,R>(right);
    }

    public Either<L, ?> map(Function<R, ?> func) {
        return Right.of(func.apply(right));
    }

    public Either<L, ?> flatMap(Function<R, ? extends Either<L, ?>> func) {
        return func.apply(right);
    }
}
