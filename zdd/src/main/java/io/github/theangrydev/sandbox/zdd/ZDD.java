package io.github.theangrydev.sandbox.zdd;

import java.util.Optional;

import static io.github.theangrydev.sandbox.zdd.OneZDD.ONE_ZDD;
import static io.github.theangrydev.sandbox.zdd.ZeroZDD.ZERO_ZDD;

public interface ZDD {
    ZDDVariable variable();
    ZDD thenZDD();
    ZDD elseZDD();
    ZDD union(ZDD zdd);
    ZDD intersection(ZDD zdd);
    ZDD filter(ZDD zdd);
    boolean contains(ZDD zdd);
    Optional<ZDDVariable> directAssignment();
    default boolean isZero() {
        return this == ZERO_ZDD;
    }
    default boolean isOne() {
        return this == ONE_ZDD;
    }
}
