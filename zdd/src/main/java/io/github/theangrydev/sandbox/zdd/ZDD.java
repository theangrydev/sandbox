package io.github.theangrydev.sandbox.zdd;

import java.util.Optional;


public interface ZDD {
    ZDDVariable variable();
    ZDD thenZDD();
    ZDD elseZDD();
    ZDD union(ZDD zdd);
    ZDD intersection(ZDD zdd);
    ZDD retainOverlapping(ZDD zdd);
    ZDD removeAllElementsIn(ZDD zdd);
    ZDD rename(ZDDVariable from, ZDDVariable to);
    boolean contains(ZDD zdd);
    Optional<ZDDVariable> directAssignment();
}
