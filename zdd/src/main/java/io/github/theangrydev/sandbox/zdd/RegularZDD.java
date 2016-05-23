package io.github.theangrydev.sandbox.zdd;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import static io.github.theangrydev.sandbox.zdd.OneZdd.ONE_ZDD;
import static io.github.theangrydev.sandbox.zdd.ZeroZdd.ZERO_ZDD;

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
        if (other == ZERO_ZDD) {
            return this;
        }
        if (other == ONE_ZDD) {
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
    public ZDD intersection(ZDD other) {
        if (other == ZERO_ZDD) {
            return ZERO_ZDD;
        }
        if (other == ONE_ZDD) {
            return this;
        }
        int comparison = variable.compareTo(other.variable());
        if (comparison == 0) {
            ZDD thenIntersection = thenZdd.intersection(other.thenZDD());
            ZDD elseIntersection = elseZdd.intersection(other.elseZDD());
            return createZDD(variable, thenIntersection, elseIntersection);
        } else if (comparison < 0) {
            return elseZdd.intersection(other); // other does not contain this.variable so we chop of the this.then branch
        } else { // comparison > 0
            return intersection(other.elseZDD()); // this does not contain other.variable so we chop of the other.then branch
        }
    }

    @Override
    public ZDD filter(ZDD other) {
        if (other == ZERO_ZDD) {
            return ZERO_ZDD;
        }
        if (other == ONE_ZDD) {
            return this;
        }
        int comparison = variable.compareTo(other.variable());
        if (comparison == 0) {
            ZDD thenFilterThen = thenZdd.filter(other.thenZDD());
            ZDD thenFilterElse = thenZdd.filter(other.elseZDD());
            ZDD elseBranch = elseZdd.filter(other.elseZDD());
            ZDD thenBranch = thenFilterThen.union(thenFilterElse);
            return createZDD(variable, thenBranch, elseBranch);
        } else if (comparison < 0) {
            return createZDD(variable, thenZdd.filter(other), elseZdd.filter(other)); // other does not contain this.variable so we just filter the remainder
        } else { // comparison > 0
            return filter(other.elseZDD()); // this does not contain other.variable so try the sets that don't contain it
        }
    }

    @Override
    public boolean contains(ZDD other) {
        if (other == ONE_ZDD) {
            return elseZdd == ONE_ZDD;
        }
        if (other == ZERO_ZDD) {
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
}
