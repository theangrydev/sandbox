package io.github.theangrydev.sandbox.zdd;

public class Renames {

    private final Renames next;

    public final ZDDVariable from;
    public final ZDDVariable to;

    public Renames(Renames next, ZDDVariable from, ZDDVariable to) {
        this.next = next;
        this.from = from;
        this.to = to;
    }

    public boolean hasNext() {
        return next != null;
    }

    public Renames next() {
        return next;
    }
}
