package io.github.theangrydev.sandbox.zdd;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import static io.github.theangrydev.sandbox.zdd.OneZDD.ONE_ZDD;
import static io.github.theangrydev.sandbox.zdd.ZeroZDD.ZERO_ZDD;

public class RegularZDD extends ValueType implements ZDD {

    private final ZDDVariable variable;
    private final ZDD thenZdd;
    private final ZDD elseZdd;

    private RegularZDD(ZDDVariable variable, ZDD thenZdd, ZDD elseZdd) {
        this.variable = variable;
        this.thenZdd = thenZdd;
        this.elseZdd = elseZdd;
    }

    private static ZDD createZDD(ZDDVariable variable, ZDD thenZdd, ZDD elseZdd) {
        if (thenZdd == ZERO_ZDD) {
            return elseZdd;
        } else {
            return new RegularZDD(variable, thenZdd, elseZdd);
        }
    }

    public static ZDD setOf(ZDDVariable... zddVariables) {
        Arrays.sort(zddVariables, Comparator.reverseOrder());
        ZDD zdd = ONE_ZDD;
        for (ZDDVariable zddVariable : zddVariables) {
            zdd = createZDD(zddVariable, zdd, ZERO_ZDD);
        }
        return zdd;
    }

    @Override
    public ZDDVariable variable() {
        return variable;
    }

    @Override
    public ZDD thenZDD() {
        return thenZdd;
    }

    @Override
    public ZDD elseZDD() {
        return elseZdd;
    }

    @Override
    public ZDD union(ZDD other) {
        if (other.isZero()) {
            return this;
        }
        if (other.isOne()) {
            return createZDD(variable, ONE_ZDD, ONE_ZDD);
        }
        int comparison = variable.compareTo(other.variable());
        if (comparison == 0) { // both contain the variable to we need to union each side
            ZDD thenUnion = thenZdd.union(other.thenZDD());
            ZDD elseUnion = elseZdd.union(other.elseZDD());
            return createZDD(variable, thenUnion, elseUnion);
        } else if (comparison < 0) {
            return createZDD(variable, thenZdd, elseZdd.union(other)); // other does not contain this.variable so we only need to union the else side
        } else { // comparison > 0
            return createZDD(other.variable(), other.thenZDD(), union(other.elseZDD())); // this does not contain other.variable so we only need to union the else side
        }
    }

    @Override
    public ZDD boxUnion(ZDD zdd) {
        return zdd.boxUnion(this);
    }

    //TODO: page 38 of bdd15.pdf
    @Override
    public ZDD boxUnion(RegularZDD other) {
        //TODO: implement
        int comparison = variable.compareTo(other.variable);
        if (comparison == 0) { // both contain the variable to we need to union each side
            ZDD otherUnion = other.elseZdd.union(other.thenZdd);
            ZDD nextUnion = thenZdd.boxUnion(otherUnion);
            ZDD next2Union = elseZdd.boxUnion(other.thenZdd);
            ZDD nextThen = nextUnion.union(next2Union);
            ZDD nextElse = elseZdd.boxUnion(other.elseZdd);
            return createZDD(variable, nextThen, nextElse);
            //TODO: page 38 of bdd15.pdf
        } else {
            ZDD nextThen = elseZdd.boxUnion(other);
            ZDD nextElse = thenZdd.boxUnion(other);
            //TODO: page 38 of bdd15.pdf
            return createZDD(variable, nextThen, nextElse); // this does not contain other.variable so we only need to union the else side
        }
    }

    @Override
    public ZDD intersection(ZDD zdd) {
        return zdd.intersection(this);
    }

    @Override
    public ZDD intersection(RegularZDD other) {
        int comparison = variable.compareTo(other.variable);
        if (comparison == 0) {
            ZDD thenIntersection = thenZdd.intersection(other.thenZdd);
            ZDD elseIntersection = elseZdd.intersection(other.elseZdd);
            return createZDD(variable, thenIntersection, elseIntersection);
        } else if (comparison < 0) {
            return elseZdd.intersection(other); // other does not contain this.variable so we chop of the this.then branch
        } else { // comparison > 0
            return intersection(other.elseZdd); // this does not contain other.variable so we chop of the other.then branch
        }
    }

    @Override
    public ZDD filter(ZDD zdd) {
        return zdd.filter(this);
    }

    @Override
    public ZDD filter(RegularZDD other) {
        int comparison = variable.compareTo(other.variable);
        if (comparison == 0) {
            ZDD thenFilterThen = thenZdd.filter(other.thenZdd);
            ZDD thenFilterElse = thenZdd.filter(other.elseZdd);
            ZDD elseBranch = elseZdd.filter(other.elseZdd);
            ZDD thenBranch = thenFilterThen.union(thenFilterElse);
            return createZDD(variable, thenBranch, elseBranch);
        } else if (comparison < 0) {
            return createZDD(variable, thenZdd.filter(other), elseZdd.filter(other)); // other does not contain this.variable so we just filter the remainder
        } else { // comparison > 0
            return filter(other.elseZdd); // this does not contain other.variable so try the sets that don't contain it
        }
    }

    @Override
    public boolean contains(ZDD other) {
        if (other.isOne()) {
            return elseZdd == ONE_ZDD;
        }
        if (other.isZero()) {
            return true;
        }
        int comparison = variable.compareTo(other.variable());
        if (comparison == 0) {
            // this isContainedBy other.variable; check it isContainedBy the rest too
            return thenZdd.contains(other.thenZDD()) && elseZdd.contains(other.elseZDD()); // ensure the other variables match
        } else {
            // if the top of this is greater than the top of other then this cannot contain other
            // otherwise, the then or else branches must contain other
            return comparison < 0 && elseZdd.contains(other);
        }
    }

    @Override
    public Optional<ZDDVariable> directAssignment() {
        if (thenZdd == ONE_ZDD && elseZdd == ZERO_ZDD) {
            return Optional.of(variable);
        }
        return Optional.empty();
    }

    @Override
    public ZDD relativeProduct(ZDD transitions, ZDD exists) {
        return exists.relativeProduct(this, exists);
    }

    @Override
    public ZDD relativeProduct(RegularZDD transitions, ZDD exists) {
        int comparison = variable.compareTo(transitions.variable);
        if (comparison == 0) {
            ZDD thenIntersection = thenZdd.intersection(transitions.thenZdd);
            ZDD elseIntersection = elseZdd.intersection(transitions.elseZdd);
            return createZDD(variable, thenIntersection, elseIntersection);
        } else if (comparison < 0) {
            return elseZdd.intersection(transitions); // transitions does not contain this.variable so we chop of the this.then branch
        } else { // comparison > 0
            return intersection(transitions.elseZdd); // this does not contain transitions.variable so we chop of the transitions.then branch
        }
    }
}
