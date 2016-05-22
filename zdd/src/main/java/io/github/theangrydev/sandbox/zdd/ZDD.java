package io.github.theangrydev.sandbox.zdd;

import java.util.Optional;

public interface ZDD {
    ZDD union(ZDD zdd);
    ZDD boxUnion(ZDD zdd);
    ZDD boxUnion(RegularZDD zdd);
    ZDD union(RegularZDD zdd);
    ZDD intersection(ZDD zdd);
    ZDD intersection(RegularZDD zdd);
    ZDD relativeProduct(ZDD transitions, ZDD exists);
    ZDD relativeProduct(RegularZDD transitions, ZDD exists);
    boolean contains(ZDD zdd);
    boolean contains(RegularZDD zdd);
    boolean isContainedBy(RegularZDD zdd);
    Optional<ZDDVariable> directAssignment();
}
