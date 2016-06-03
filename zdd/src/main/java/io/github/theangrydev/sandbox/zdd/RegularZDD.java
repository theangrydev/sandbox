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
            return createZDD(variable, ONE_ZDD, ONE_ZDD);
        }
        int comparison = compareTopVariable(other);
        if (comparison == 0) {
            return unionMatchingSides(other);
        } else if (comparison < 0) {
            return unionElseSideWithOther(other);
        } else {
            return unionOtherElseSideWithThis(other);
        }
    }

    private ZDD unionMatchingSides(ZDD other) {
        return createZDD(variable, thenZdd.union(other.thenZDD()), elseZdd.union(other.elseZDD()));
    }

    private ZDD unionElseSideWithOther(ZDD other) {
        return createZDD(variable, thenZdd, elseZdd.union(other));
    }

    private ZDD unionOtherElseSideWithThis(ZDD other) {
        return createZDD(other.variable(), other.thenZDD(), union(other.elseZDD()));
    }

    @Override
    public ZDD extend(ZDD other) {
        if (other == ZERO_ZDD || other == ONE_ZDD) {
            return this;
        }
        int comparison = compareTopVariable(other);
        if (comparison == 0) {
            return extendMatchingSides(other);
        } else if (comparison < 0) {
            return extendBothSidesOfThisWithOther(other);
        } else {
            return extendBothSidesOfOtherWithThis(other);
        }
    }

    private ZDD extendMatchingSides(ZDD other) {
        return createZDD(variable, other.thenZDD().extend(thenZdd), other.elseZDD().extend(elseZdd));
    }

    private ZDD extendBothSidesOfThisWithOther(ZDD other) {
        return createZDD(variable, thenZdd.extend(other), elseZdd.extend(other));
    }

    private ZDD extendBothSidesOfOtherWithThis(ZDD other) {
        return createZDD(other.variable(), other.thenZDD().extend(this), other.elseZDD().extend(this));
    }

    @Override
    public ZDD intersection(ZDD other) {
        if (other == ZERO_ZDD) {
            return ZERO_ZDD;
        }
        if (other == ONE_ZDD) {
            return this;
        }
        int comparison = compareTopVariable(other);
        if (comparison == 0) {
            return intersectMatchingSides(other);
        } else if (comparison < 0) {
            return elseZdd.intersection(other);
        } else {
            return intersection(other.elseZDD());
        }
    }

    private ZDD intersectMatchingSides(ZDD other) {
        return createZDD(variable, thenZdd.intersection(other.thenZDD()), elseZdd.intersection(other.elseZDD()));
    }

    @Override
    public ZDD retainOverlapping(ZDD other) {
        if (other == ZERO_ZDD) {
            return ZERO_ZDD;
        }
        if (other == ONE_ZDD) {
            return this;
        }
        int comparison = compareTopVariable(other);
        if (comparison == 0) {
            ZDD thenFilterThen = thenZdd.retainOverlapping(other.thenZDD());
            ZDD thenFilterElse = thenZdd.retainOverlapping(other.elseZDD());
            ZDD elseBranch = elseZdd.retainOverlapping(other.elseZDD());
            ZDD thenBranch = thenFilterThen.union(thenFilterElse);
            return createZDD(variable, thenBranch, elseBranch);
        } else if (comparison < 0) {
            return createZDD(variable, thenZdd.retainOverlapping(other), elseZdd.retainOverlapping(other));
        } else {
            return retainOverlapping(other.thenZDD()).retainOverlapping(other.elseZDD());
        }
    }

    @Override
    public ZDD removeAllElementsIn(ZDD other) {
        if (other == ZERO_ZDD || other == ONE_ZDD) {
            return this;
        }
        int comparison = compareTopVariable(other);
        if (comparison == 0) {
            return removeThis(other);
        } else if (comparison < 0) {
            return removeAllElementsInOtherFromBothSidesOfThis(other);
        } else {
            return removeAllElementsIn(other.thenZDD()).removeAllElementsIn(other.elseZDD());
        }
    }

    private ZDD removeThis(ZDD other) {
        return thenZdd.removeAllElementsIn(other).union(elseZdd.removeAllElementsIn(other));
    }

    private ZDD removeAllElementsInOtherFromBothSidesOfThis(ZDD other) {
        return createZDD(variable, thenZdd.removeAllElementsIn(other), elseZdd.removeAllElementsIn(other));
    }

    @Override
    public boolean contains(ZDD other) {
        if (other == ONE_ZDD) {
            return elseZdd == ONE_ZDD;
        }
        if (other == ZERO_ZDD) {
            return true;
        }
        int comparison = compareTopVariable(other);
        if (comparison == 0) {
            return thenZdd.contains(other.thenZDD()) && elseZdd.contains(other.elseZDD());
        } else {
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
        StringBuilder toString = new StringBuilder();
        toString.append('{');
        appendThenAndElseSets(new StringBuilder(), toString);
        toString.append('}');
        return toString.toString();
    }

    @Override
    public void appendSets(StringBuilder prefix, StringBuilder toString) {
        toString.append(',');
        appendThenAndElseSets(prefix, toString);
    }

    private void appendThenAndElseSets(StringBuilder prefix, StringBuilder toString) {
        appendThenSet(prefix, toString);
        appendElseSets(prefix, toString);
    }

    private void appendThenSet(StringBuilder prefix, StringBuilder toString) {
        toString.append('{');
        toString.append(prefix);
        ZDD current = this;
        while (current != ONE_ZDD) {
            toString.append(current.variable().toString());
            toString.append(',');
            current = current.thenZDD();
        }
        toString.setCharAt(toString.length() - 1, '}');
    }

    private void appendElseSets(StringBuilder prefix, StringBuilder toString) {
        ZDD current = this;
        while (current != ONE_ZDD) {
            current.elseZDD().appendSets(new StringBuilder(prefix), toString);
            prefix.append(current.variable().toString());
            prefix.append(',');
            current = current.thenZDD();
        }
    }

    private ZDD createZDD(ZDDVariable variable, ZDD thenZDD, ZDD elseZDD) {
        return zddFactory.createZDD(variable, thenZDD, elseZDD);
    }

    private int compareTopVariable(ZDD other) {
        return variable.compareTo(other.variable());
    }
}
