package io.github.theangrydev.sandbox.zdd;

import java.util.Optional;

public class ZeroZDD extends ValueType implements ZDD {
    public static ZDD ZERO_ZDD = new ZeroZDD();

    @Override
    public ZDDVariable variable() {
        return ZDDVariable.newVariable(Integer.MAX_VALUE);
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
        return zdd;
    }

    @Override
    public ZDD boxUnion(ZDD zdd) {
        return zdd;
    }

    @Override
    public ZDD boxUnion(RegularZDD zdd) {
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
    public ZDD filter(ZDD zdd) {
        return this;
    }

    @Override
    public ZDD filter(RegularZDD zdd) {
        return this;
    }

    @Override
    public boolean contains(ZDD zdd) {
        return zdd == this;
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
