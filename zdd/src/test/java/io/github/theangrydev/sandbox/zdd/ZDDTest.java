package io.github.theangrydev.sandbox.zdd;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import static io.github.theangrydev.sandbox.zdd.OneZDD.ONE_ZDD;

public class ZDDTest implements WithAssertions {

    private final ZDDFactory zddFactory = new ZDDFactory(1000);

    @Test
    public void withOneVariableContainsThatVariable() {
        ZDDVariable zddVariable = ZDDVariable.newVariable(0);
        ZDD zdd = setOf(zddVariable);

        assertThat(zdd.directAssignment()).contains(zddVariable);
    }

    @Test
    public void withMultipleVariablesContainsAllTheVariables() {
        ZDDVariable zddVariable1 = ZDDVariable.newVariable(0);
        ZDDVariable zddVariable2 = ZDDVariable.newVariable(1);
        ZDDVariable zddVariable3 = ZDDVariable.newVariable(2);
        ZDD zdd = setOf(zddVariable1, zddVariable2, zddVariable3);

        assertThat(zdd.contains(setOf(zddVariable1, zddVariable2, zddVariable3))).isTrue();
    }

    @Test
    public void unionOfSingleSets() {
        ZDDVariable variable1 = ZDDVariable.newVariable(0);
        ZDDVariable variable2 = ZDDVariable.newVariable(1);
        ZDDVariable variable3 = ZDDVariable.newVariable(2);

        ZDD union = setOf(variable1).union(setOf(variable2)).union(setOf(variable3));

        assertThat(union.contains(setOf(variable1))).isTrue();
        assertThat(union.contains(setOf(variable2))).isTrue();
        assertThat(union.contains(setOf(variable3))).isTrue();
    }

    @Test
    public void unionThreeByTwo() {
        ZDDVariable variable1 = ZDDVariable.newVariable(0);
        ZDDVariable variable2 = ZDDVariable.newVariable(1);
        ZDDVariable variable3 = ZDDVariable.newVariable(2);

        ZDD union = setOf(variable1, variable2, variable3).union(setOf(variable1, variable2));

        assertThat(union.contains(setOf(variable1, variable2, variable3))).isTrue();
        assertThat(union.contains(setOf(variable1, variable2))).isTrue();
    }

    @Test
    public void unionTwoByOne() {
        ZDDVariable variable1 = ZDDVariable.newVariable(0);
        ZDDVariable variable2 = ZDDVariable.newVariable(1);

        ZDD union = setOf(variable1, variable2).union(setOf(variable1));

        assertThat(union.contains(setOf(variable1))).isTrue();
        assertThat(union.contains(setOf(variable1, variable2))).isTrue();
    }

    @Test
    public void unionWithOne() {
        ZDDVariable variable1 = ZDDVariable.newVariable(0);

        ZDD union = setOf(variable1).union(ONE_ZDD);

        assertThat(union.contains(setOf(variable1))).isTrue();
    }

    @Test
    public void intersection() {
        ZDDVariable variable1 = ZDDVariable.newVariable(0);
        ZDDVariable variable2 = ZDDVariable.newVariable(1);
        ZDDVariable variable3 = ZDDVariable.newVariable(2);

        ZDD oneAndTwo = setOf(variable1).union(setOf(variable2));
        ZDD twoAndThree = setOf(variable2).union(setOf(variable3));

        ZDD intersection = oneAndTwo.intersection(twoAndThree);

        assertThat(intersection.contains(setOf(variable1))).isFalse();
        assertThat(intersection.contains(setOf(variable2))).isTrue();
        assertThat(intersection.contains(setOf(variable3))).isFalse();
    }

    @Test
    public void retainOverlapping() {
        ZDDVariable variable1 = ZDDVariable.newVariable(0);
        ZDDVariable variable2 = ZDDVariable.newVariable(1);
        ZDDVariable variable3 = ZDDVariable.newVariable(2);

        ZDD all = setOf(variable1, variable2, variable3).union(setOf(variable1, variable2)).union(setOf(variable2, variable3));

        ZDD filtered = all.retainOverlapping(setOf(variable1, variable2));

        assertThat(filtered.contains(setOf(variable1, variable2, variable3))).isTrue();
        assertThat(filtered.contains(setOf(variable1, variable2))).isTrue();
        assertThat(filtered.contains(setOf(variable2, variable3))).isFalse();
    }

    @Test
    public void retainOverlappingExample() {
        ZDDVariable from1 = ZDDVariable.newVariable(0);
        ZDDVariable from2 = ZDDVariable.newVariable(1);
        ZDDVariable char1 = ZDDVariable.newVariable(2);
        ZDDVariable char2 = ZDDVariable.newVariable(3);
        ZDDVariable to1 = ZDDVariable.newVariable(4);
        ZDDVariable to2 = ZDDVariable.newVariable(5);

        ZDD frontier = setOf(from1, char1);
        ZDD transitions = setOf(from1, char1, to1).union(setOf(from2, char2, to2));

        ZDD applicableTransition = transitions.retainOverlapping(frontier);

        assertThat(applicableTransition.contains(setOf(from1, char1, to1))).isTrue();
        assertThat(applicableTransition.contains(setOf(from2, char2, to2))).isFalse();
    }

    @Test
    public void removeAllElementsInSingle() {
        ZDDVariable variable1 = ZDDVariable.newVariable(0);
        ZDDVariable variable2 = ZDDVariable.newVariable(1);

        ZDD remove = setOf(variable1, variable2).removeAllElementsIn(setOf(variable1));

        assertThat(remove.contains(setOf(variable2))).isTrue();
        assertThat(remove.contains(setOf(variable1, variable2))).isFalse();
        assertThat(remove.contains(setOf(variable1))).isFalse();
    }

    @Test
    public void removeAllElementsInMultiple() {
        ZDDVariable variable1 = ZDDVariable.newVariable(0);
        ZDDVariable variable2 = ZDDVariable.newVariable(1);
        ZDDVariable variable3 = ZDDVariable.newVariable(2);

        ZDD remove = setOf(variable1, variable2, variable3).removeAllElementsIn(setOf(variable1, variable2));

        assertThat(remove.contains(setOf(variable3))).isTrue();
        assertThat(remove.contains(setOf(variable1, variable2, variable3))).isFalse();
    }

    @Test
    public void removeAllElementsInExample() {
        ZDDVariable from1 = ZDDVariable.newVariable(0);
        ZDDVariable from2 = ZDDVariable.newVariable(1);
        ZDDVariable char1 = ZDDVariable.newVariable(2);
        ZDDVariable char2 = ZDDVariable.newVariable(3);
        ZDDVariable to1 = ZDDVariable.newVariable(4);
        ZDDVariable to2 = ZDDVariable.newVariable(5);

        ZDD allFromStates = setOf(from1, from2, char1, char2);
        ZDD transitions = setOf(from1, char1, to1).union(setOf(from2, char2, to2));

        ZDD next = transitions.removeAllElementsIn(allFromStates);

        assertThat(next.contains(setOf(to1))).isTrue();
        assertThat(next.contains(setOf(to2))).isTrue();
    }

    @Test
    public void containsSingleElement() {
        ZDDVariable variable1 = ZDDVariable.newVariable(0);
        ZDDVariable variable2 = ZDDVariable.newVariable(1);

        assertThat(setOf(variable1).contains(setOf(variable1))).isTrue();
        assertThat(setOf(variable1).contains(setOf(variable2))).isFalse();
    }

    @Test
    public void containsSubset() {
        ZDDVariable variable1 = ZDDVariable.newVariable(0);
        ZDDVariable variable2 = ZDDVariable.newVariable(1);
        ZDDVariable variable3 = ZDDVariable.newVariable(2);

        ZDD setOfThree = setOf(variable1, variable2, variable3);

        assertThat(setOfThree.contains(setOfThree)).isTrue();
        assertThat(setOfThree.contains(setOf(variable1, variable2))).isFalse();
        assertThat(setOfThree.contains(setOf(variable2, variable3))).isFalse();
        assertThat(setOfThree.contains(setOf(variable1, variable3))).isFalse();
        assertThat(setOfThree.contains(setOf(variable1))).isFalse();
        assertThat(setOfThree.contains(setOf(variable2))).isFalse();
        assertThat(setOfThree.contains(setOf(variable3))).isFalse();
    }

    @Test
    public void doesNotContainSubsetForSetThatHasAOneFork() {
        ZDDVariable variable1 = ZDDVariable.newVariable(0);
        ZDDVariable variable2 = ZDDVariable.newVariable(1);

        ZDD all = setOf(variable1, variable2).union(setOf(variable1));

        assertThat(all.contains(setOf(variable1, variable2))).isTrue();
        assertThat(all.contains(setOf(variable1))).isTrue();
        assertThat(all.contains(setOf(variable2))).isFalse();
    }

    private ZDD setOf(ZDDVariable... variables) {
        return zddFactory.setOf(variables);
    }

    //TODO: think about ( ( (F |_| A_ALL) |-| T) |-| F_ALL ) \ F_ALL)
    // where:
    // F = frontier
    // A = after
    // T = transition
    // A_ALL = all afters
    // F_ALL = all frontiers

//        ZDDVariable from1 = ZDDVariable.newVariable(0);
//        ZDDVariable from2 = ZDDVariable.newVariable(1);
//        ZDDVariable to = ZDDVariable.newVariable(2);
//        ZDDVariable character = ZDDVariable.newVariable(3);
//
//        ZDD frontier = RegularZDD.setOf(from1, from2, character);
//        ZDD transitions = RegularZDD.setOf(from2, character, to);
//        ZDD fromAndChar = RegularZDD.setOf(from1, from2, character);
//
//        ZDD relativeProduct = frontier.relativeProduct(transitions, fromAndChar);
//
//        assertThat(relativeProduct.contains(from1)).isFalse();
//        assertThat(relativeProduct.contains(from2)).isFalse();
//        assertThat(relativeProduct.contains(to)).isTrue();
//    }
}
