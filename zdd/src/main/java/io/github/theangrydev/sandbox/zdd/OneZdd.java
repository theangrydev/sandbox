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
    public boolean contains(ZDD zdd) {
        return true;
    }

    @Override
    public boolean contains(RegularZDD zdd) {
        return true;
    }

    @Override
    public boolean isContainedBy(RegularZDD zdd) {
        return false;
    }

    @Override
    public Optional<ZDDVariable> directAssignment() {
        return Optional.empty();
    }

    @Override
    public ZDD relativeProduct(ZDD transitions, ZDD exists) {
        //TODO: i think this should be transitions subtract exists
        return transitions;
    }

    @Override
    public ZDD relativeProduct(RegularZDD transitions, ZDD exists) {
        return transitions;
    }
}
