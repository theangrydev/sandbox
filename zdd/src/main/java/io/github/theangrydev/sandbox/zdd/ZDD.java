package io.github.theangrydev.sandbox.zdd;

import java.util.Optional;


public interface ZDD {
    ZDDVariable variable();
    ZDD thenZDD();
    ZDD elseZDD();
    ZDD union(ZDD zdd);
    ZDD intersection(ZDD zdd);
    ZDD filter(ZDD zdd);
    boolean contains(ZDD zdd);
    Optional<ZDDVariable> directAssignment();
}
