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

    @Override
    public ZDD union(ZDD zdd) {
        return zdd.union(this);
    }

    @Override
    public ZDD union(RegularZDD other) {
        int comparison = variable.compareTo(other.variable);
        if (comparison == 0) { // both contain the variable to we need to union each side
            ZDD thenUnion = thenZdd.union(other.thenZdd);
            ZDD elseUnion = elseZdd.union(other.elseZdd);
            return new RegularZDD(variable, thenUnion, elseUnion);
        } else if (comparison < 0) {
            return new RegularZDD(variable, thenZdd, elseZdd.union(other)); // other does not contain this.variable so we only need to union the else side
        } else { // comparison > 0
            return new RegularZDD(other.variable, other.thenZdd, union(other.elseZdd)); // this does not contain other.variable so we only need to union the else side
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
            return new RegularZDD(variable, thenIntersection, elseIntersection);
        } else if (comparison < 0) {
            return elseZdd.intersection(other); // other does not contain this.variable so we chop of the this.then branch
        } else { // comparison > 0
            return intersection(other.elseZdd); // this does not contain other.variable so we chop of the other.then branch
        }
    }

    @Override
    public boolean contains(ZDDVariable zddVariable) {
        return this.variable == zddVariable
                || thenZdd != ONE_ZDD && thenZdd.contains(zddVariable)
                || elseZdd != ZERO_ZDD && elseZdd.contains(zddVariable);
    }

    @Override
    public boolean contains(ZDD zdd) {
        return zdd.isContainedBy(this);
    }

    @Override
    public boolean isContainedBy(RegularZDD other) {
        return other.contains(this);
    }

    @Override
    public boolean contains(RegularZDD other) {
        int comparison = variable.compareTo(other.variable);
        if (comparison == 0) {
            // this isContainedBy other.variable; check it isContainedBy the rest too
            return thenZdd.contains(other.thenZdd) && elseZdd.contains(other.elseZdd); // ensure the other variables match
        } else {
            // if the top of this is greater than the top of other then this cannot contain other
            // otherwise, the then or else branches must contain other
            return comparison < 0 && (thenZdd.contains(other) || elseZdd.contains(other));
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
            return new RegularZDD(variable, thenIntersection, elseIntersection);
        } else if (comparison < 0) {
            return elseZdd.intersection(transitions); // transitions does not contain this.variable so we chop of the this.then branch
        } else { // comparison > 0
            return intersection(transitions.elseZdd); // this does not contain transitions.variable so we chop of the transitions.then branch
        }
    }

    public static ZDD withVariables(ZDDVariable... zddVariables) {
        Arrays.sort(zddVariables, Comparator.reverseOrder());
        ZDD zdd = ONE_ZDD;
        for (ZDDVariable zddVariable : zddVariables) {
            zdd = new RegularZDD(zddVariable, zdd, ZERO_ZDD);
        }
        return zdd;
    }
}
