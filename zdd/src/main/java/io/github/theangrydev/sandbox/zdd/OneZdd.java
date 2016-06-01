package io.github.theangrydev.sandbox.zdd;

import java.util.Optional;

import static io.github.theangrydev.sandbox.zdd.ZeroZDD.ZERO_ZDD;

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
    public ZDD extend(ZDD zdd) {
        return zdd;
    }

    @Override
    public ZDD retainOverlapping(ZDD zdd) {
        return zdd;
    }

    @Override
    public ZDD removeAllElementsIn(ZDD zdd) {
        if (zdd == ZERO_ZDD) {
            return ZERO_ZDD;
        } else {
            return this;
        }
    }

    @Override
    public boolean contains(ZDD zdd) {
        return zdd == ONE_ZDD || zdd == ZERO_ZDD;
    }

    @Override
    public Optional<ZDDVariable> directAssignment() {
        return Optional.empty();
    }

    @Override
    public void appendSets(StringBuilder prefix, StringBuilder stringBuilder) {

    }

    @Override
    public String toString() {
        return "{∅}";
    }
}
