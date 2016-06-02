package io.github.theangrydev.sandbox.zdd;

import java.util.Optional;

import static io.github.theangrydev.sandbox.zdd.OneZDD.ONE_ZDD;
import static io.github.theangrydev.sandbox.zdd.ZeroZDD.ZERO_ZDD;

public class RegularZDD extends ValueType implements ZDD {

    private final ZDDFactory zddFactory;
    private final ZDDVariable variable;
    private final ZDD thenZdd;
    private final ZDD elseZdd;

    RegularZDD(ZDDFactory zddFactory, ZDDVariable variable, ZDD thenZdd, ZDD elseZdd) {
        this.zddFactory = zddFactory;
        this.variable = variable;
        this.thenZdd = thenZdd;
        this.elseZdd = elseZdd;
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
            return zddFactory.createZDD(variable, ONE_ZDD, ONE_ZDD);
        }
        int comparison = variable.compareTo(other.variable());
        if (comparison == 0) { // both contain the variable to we need to union each side
            ZDD thenUnion = thenZdd.union(other.thenZDD());
            ZDD elseUnion = elseZdd.union(other.elseZDD());
            return zddFactory.createZDD(variable, thenUnion, elseUnion);
        } else if (comparison < 0) {
            return zddFactory.createZDD(variable, thenZdd, elseZdd.union(other)); // other does not contain this.variable so we only need to union the else side
        } else { // comparison > 0
            return zddFactory.createZDD(other.variable(), other.thenZDD(), union(other.elseZDD())); // this does not contain other.variable so we only need to union the else side
        }
    }

    @Override
    public ZDD extend(ZDD other) {
        if (other == ZERO_ZDD) {
            return this;
        }
        if (other == ONE_ZDD) {
            return zddFactory.createZDD(variable, ONE_ZDD, ONE_ZDD);
        }
        int comparison = variable.compareTo(other.variable());
        if (comparison == 0) { // both contain the variable to we need to union each side
            return zddFactory.createZDD(variable, thenZdd.extend(other.thenZDD()), elseZdd.extend(other.elseZDD()));
        } else if (comparison < 0) {
            return zddFactory.createZDD(variable, thenZdd.extend(other), elseZdd.extend(other)); // other does not contain this.variable so we only need to union the else side
        } else { // comparison > 0
            return zddFactory.createZDD(other.variable(), extend(other.thenZDD()), extend(other.elseZDD())); // this does not contain other.variable so we only need to union the else side
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
            return zddFactory.createZDD(variable, thenIntersection, elseIntersection);
        } else if (comparison < 0) {
            return elseZdd.intersection(other); // other does not contain this.variable so we chop of the this.then branch
        } else { // comparison > 0
            return intersection(other.elseZDD()); // this does not contain other.variable so we chop of the other.then branch
        }
    }

    @Override
    public ZDD retainOverlapping(ZDD other) {
        if (other == ZERO_ZDD) {
            return ZERO_ZDD;
        }
        if (other == ONE_ZDD) {
            return this;
        }
        int comparison = variable.compareTo(other.variable());
        if (comparison == 0) {
            ZDD thenFilterThen = thenZdd.retainOverlapping(other.thenZDD());
            ZDD thenFilterElse = thenZdd.retainOverlapping(other.elseZDD());
            ZDD elseBranch = elseZdd.retainOverlapping(other.elseZDD());
            ZDD thenBranch = thenFilterThen.union(thenFilterElse);
            return zddFactory.createZDD(variable, thenBranch, elseBranch);
        } else if (comparison < 0) {
            return zddFactory.createZDD(variable, thenZdd.retainOverlapping(other), elseZdd.retainOverlapping(other)); // other does not contain this.variable so we just retainOverlapping the remainder
        } else { // comparison > 0
            return retainOverlapping(other.elseZDD()); // this does not contain other.variable so try the sets that don't contain it
        }
    }

    @Override
    public ZDD removeAllElementsIn(ZDD other) {
        if (other == ZERO_ZDD || other == ONE_ZDD) {
            return this;
        }
        int comparison = variable.compareTo(other.variable());
        if (comparison == 0) {
            return thenZdd.removeAllElementsIn(other).union(elseZdd.removeAllElementsIn(other));
        } else if (comparison < 0) {
            return zddFactory.createZDD(variable, thenZdd.removeAllElementsIn(other), elseZdd.removeAllElementsIn(other)); // other does not contain this.variable so we just removeAllElementsIn from the remainder
        } else { // comparison > 0
            return removeAllElementsIn(other.thenZDD()).removeAllElementsIn(other.elseZDD()); // this does not contain other.variable so try the sets that don't contain it
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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('{');
        appendThenAndElseSets(new StringBuilder(), stringBuilder);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    @Override
    public void appendSets(StringBuilder prefix, StringBuilder stringBuilder) {
        stringBuilder.append(',');
        appendThenAndElseSets(prefix, stringBuilder);
    }

    private void appendThenAndElseSets(StringBuilder prefix, StringBuilder stringBuilder) {
        appendThenSet(prefix, stringBuilder);
        appendElseSets(prefix, stringBuilder);
    }

    private void appendThenSet(StringBuilder prefix, StringBuilder stringBuilder) {
        stringBuilder.append('{');
        stringBuilder.append(prefix);
        ZDD current = this;
        while (current != ONE_ZDD) {
            stringBuilder.append(current.variable().toString());
            stringBuilder.append(',');
            current = current.thenZDD();
        }
        stringBuilder.setCharAt(stringBuilder.length() - 1, '}');
    }

    private void appendElseSets(StringBuilder prefix, StringBuilder stringBuilder) {
        ZDD current = this;
        while (current != ONE_ZDD) {
            current.elseZDD().appendSets(new StringBuilder(prefix), stringBuilder);
            prefix.append(current.variable().toString());
            prefix.append(',');
            current = current.thenZDD();
        }
    }
}
