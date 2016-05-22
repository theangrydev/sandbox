package io.github.theangrydev.sandbox.zdd;

import java.util.Optional;

public interface ZDD {
    ZDD union(ZDD zdd);
    ZDD union(RegularZDD zdd);
    ZDD intersection(ZDD zdd);
    ZDD intersection(RegularZDD zdd);
    boolean contains(ZDDVariable zddVariable);
    Optional<ZDDVariable> directAssignment();
}
