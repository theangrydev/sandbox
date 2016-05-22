package io.github.theangrydev.sandbox.zdd;

import java.util.Optional;

public class ZeroZDD extends ValueType implements ZDD {
    public static ZDD ZERO_ZDD = new ZeroZDD();

    @Override
    public ZDD union(ZDD zdd) {
        return zdd;
    }

    @Override
    public ZDD union(RegularZDD zdd) {
        return zdd;
    }

    @Override
    public ZDD intersection(ZDD zdd) {
        return this;
    }

    @Override
    public ZDD intersection(RegularZDD zdd) {
        return this;
    }

    @Override
    public boolean contains(ZDD zdd) {
        return zdd == this;
    }

    @Override
    public boolean contains(RegularZDD zdd) {
        return false;
    }

    @Override
    public boolean isContainedBy(RegularZDD zdd) {
        return true;
    }

    @Override
    public Optional<ZDDVariable> directAssignment() {
        return Optional.empty();
    }

    @Override
    public ZDD relativeProduct(ZDD transitions, ZDD exists) {
        return this;
    }

    @Override
    public ZDD relativeProduct(RegularZDD transitions, ZDD exists) {
        return this;
    }
}
