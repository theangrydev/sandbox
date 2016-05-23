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
    public ZDD boxUnion(ZDD zdd) {
        return this;
    }

    @Override
    public ZDD boxUnion(RegularZDD zdd) {
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
    public ZDD filter(ZDD zdd) {
        return zdd;
    }

    @Override
    public ZDD filter(RegularZDD zdd) {
        return zdd;
    }

    @Override
    public boolean contains(ZDD zdd) {
        return true;
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
