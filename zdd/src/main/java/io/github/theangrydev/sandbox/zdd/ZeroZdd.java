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
    public boolean contains(ZDDVariable zddVariable) {
        return false;
    }

    @Override
    public Optional<ZDDVariable> directAssignment() {
        return Optional.empty();
    }
}
