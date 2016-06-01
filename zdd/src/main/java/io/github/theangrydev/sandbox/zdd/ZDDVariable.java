package io.github.theangrydev.sandbox.zdd;

public class ZDDVariable extends ValueType implements Comparable<ZDDVariable> {

    private final int ordering;

    private ZDDVariable(int ordering) {
        this.ordering = ordering;
    }

    public static ZDDVariable newVariable(int ordering) {
        return new ZDDVariable(ordering);
    }

    @Override
    public int compareTo(ZDDVariable other) {
        return Integer.compare(ordering, other.ordering);
    }

    @Override
    public String toString() {
        return String.valueOf(ordering);
    }
}
