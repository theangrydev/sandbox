package io.github.theangrydev.sandbox.zdd;

import java.util.Optional;

public class OneZDD extends ValueType implements ZDD {
    public static ZDD ONE_ZDD = new OneZDD();

    private OneZDD() {
        // Singleton
    }

    @Override
    public ZDDVariable variable() {
        return ZDDVariable.newVariable(Integer.MIN_VALUE);
    }

    @Override
    public ZDD thenZDD() {
        return this;
    }

    @Override
    public ZDD elseZDD() {
        return this;
    }

    @Override
    public ZDD union(ZDD zdd) {
        return this;
    }

    @Override
    public ZDD intersection(ZDD zdd) {
        return zdd;
    }

    @Override
    public ZDD retainOverlapping(ZDD zdd) {
        return zdd;
    }

    @Override
    public ZDD removeAllElementsIn(ZDD zdd) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(ZDD zdd) {
        return true;
    }

    @Override
    public Optional<ZDDVariable> directAssignment() {
        return Optional.empty();
    }

}
