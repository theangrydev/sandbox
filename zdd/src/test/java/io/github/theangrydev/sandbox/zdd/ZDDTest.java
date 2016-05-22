package io.github.theangrydev.sandbox.zdd;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import static io.github.theangrydev.sandbox.zdd.OneZDD.ONE_ZDD;
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
        ZDD zdd = RegularZDD.withVariables(zddVariable);

        assertThat(zdd.directAssignment()).contains(zddVariable);
    }

    @Test
    public void withMultipleVariablesContainsAllTheVariables() {
        ZDDVariable zddVariable1 = ZDDVariable.newVariable(0);
        ZDDVariable zddVariable2 = ZDDVariable.newVariable(1);
        ZDDVariable zddVariable3 = ZDDVariable.newVariable(2);
        ZDD zdd = RegularZDD.withVariables(zddVariable1, zddVariable2, zddVariable3);

        assertThat(zdd.contains(RegularZDD.withVariables(zddVariable1, zddVariable2, zddVariable3))).isTrue();
    }

    @Test
    public void union() {
        ZDDVariable variable1 = ZDDVariable.newVariable(0);
        ZDDVariable variable2 = ZDDVariable.newVariable(1);
        ZDDVariable variable3 = ZDDVariable.newVariable(2);

        ZDD union = RegularZDD.withVariables(variable1).union(RegularZDD.withVariables(variable2)).union(RegularZDD.withVariables(variable3));

        assertThat(union.contains(RegularZDD.withVariables(variable1))).isTrue();
        assertThat(union.contains(RegularZDD.withVariables(variable2))).isTrue();
        assertThat(union.contains(RegularZDD.withVariables(variable3))).isTrue();
    }

    @Test
    public void intersection() {
        ZDDVariable variable1 = ZDDVariable.newVariable(0);
        ZDDVariable variable2 = ZDDVariable.newVariable(1);
        ZDDVariable variable3 = ZDDVariable.newVariable(2);

        ZDD oneAndTwo = RegularZDD.withVariables(variable1).union(RegularZDD.withVariables(variable2));
        ZDD twoAndThree = RegularZDD.withVariables(variable2).union(RegularZDD.withVariables(variable3));

        ZDD intersection = oneAndTwo.intersection(twoAndThree);

        assertThat(intersection.contains(RegularZDD.withVariables(variable1))).isFalse();
        assertThat(intersection.contains(RegularZDD.withVariables(variable2))).isTrue();
        assertThat(intersection.contains(RegularZDD.withVariables(variable3))).isFalse();
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
//        ZDD frontier = RegularZDD.withVariables(from1, from2, character);
//        ZDD transitions = RegularZDD.withVariables(from2, character, to);
//        ZDD fromAndChar = RegularZDD.withVariables(from1, from2, character);
//
//        ZDD relativeProduct = frontier.relativeProduct(transitions, fromAndChar);
//
//        assertThat(relativeProduct.contains(from1)).isFalse();
//        assertThat(relativeProduct.contains(from2)).isFalse();
//        assertThat(relativeProduct.contains(to)).isTrue();
//    }
}
