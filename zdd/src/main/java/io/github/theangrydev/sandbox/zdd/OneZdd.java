package io.github.theangrydev.sandbox.zdd;

import java.util.Optional;

public class OneZDD extends ValueType implements ZDD {
    public static ZDD ONE_ZDD = new OneZDD();

    private OneZDD() {
        // Singleton
    }

    @Override
    public ZDD union(ZDD zdd) {
        return this;
    }

    @Override
    public ZDD union(RegularZDD zdd) {
        return this;
    }

    @Override
    public ZDD intersection(ZDD zdd) {
        return zdd;
    }

    @Override
    public ZDD intersection(RegularZDD zdd) {
        return zdd;
    }

    @Override
    public boolean contains(ZDDVariable zddVariable) {
        return true;
    }

    @Override
    public Optional<ZDDVariable> directAssignment() {
        return Optional.empty();
    }
}
