package io.github.theangrydev.sandbox.zdd;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import static io.github.theangrydev.sandbox.zdd.OneZDD.ONE_ZDD;
import static io.github.theangrydev.sandbox.zdd.RegularZDD.setOf;
import static io.github.theangrydev.sandbox.zdd.ZeroZDD.ZERO_ZDD;

public class ZDDTest implements WithAssertions {

    @Test
    public void zeroZDD() {
        assertThat(ZERO_ZDD).isNotNull();
    }

    @Test
    public void oneZDD() {
        assertThat(ONE_ZDD).isNotNull();
    }

    @Test
    public void oneHasNoDirectAssignment() {
        assertThat(ONE_ZDD.directAssignment()).isEmpty();
    }

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
    public void boxUnion() {
        ZDDVariable variable1 = ZDDVariable.newVariable(0);
        ZDDVariable variable2 = ZDDVariable.newVariable(1);

        ZDD union = setOf(variable1).boxUnion(setOf(variable2));

        assertThat(union.contains(setOf(variable1, variable2))).isTrue();
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
    public void filter() {
        ZDDVariable variable1 = ZDDVariable.newVariable(0);
        ZDDVariable variable2 = ZDDVariable.newVariable(1);
        ZDDVariable variable3 = ZDDVariable.newVariable(2);

        ZDD oneAndTwo = setOf(variable1, variable2);

        ZDD all = setOf(variable1, variable2, variable3).union(setOf(variable1, variable2));//.union(setOf(variable2, variable3));

        ZDD filtered = all.filter(oneAndTwo);

        assertThat(filtered.contains(setOf(variable1, variable2, variable3))).isTrue();
        assertThat(filtered.contains(setOf(variable1, variable2))).isTrue();
        assertThat(filtered.contains(setOf(variable2, variable3))).isFalse();
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

    //TODO: think about ( ( (F |_| A_ALL) |-| T) |-| F_ALL ) \ F_ALL)
    // where:
    // F = frontier
    // A = after
    // T = transition
    // A_ALL = all afters
    // F_ALL = all frontiers

//    @Test
//    public void relativeProduct() {
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
