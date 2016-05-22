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
        assertThat(ONE_ZDD.contains(ZDDVariable.newVariable(1))).isTrue();
    }

    @Test
    public void withOneVariableContainsThatVariable() {
        ZDDVariable zddVariable = ZDDVariable.newVariable(0);
        ZDD zdd = RegularZDD.withVariable(zddVariable);

        assertThat(zdd.directAssignment()).contains(zddVariable);
    }

    @Test
    public void withMultipleVariablesContainsAllTheVariables() {
        ZDDVariable zddVariable1 = ZDDVariable.newVariable(0);
        ZDDVariable zddVariable2 = ZDDVariable.newVariable(1);
        ZDDVariable zddVariable3 = ZDDVariable.newVariable(2);
        ZDD zdd = RegularZDD.withVariables(zddVariable1, zddVariable2, zddVariable3);

        assertThat(zdd.contains(zddVariable1)).isTrue();
        assertThat(zdd.contains(zddVariable2)).isTrue();
        assertThat(zdd.contains(zddVariable3)).isTrue();
        assertThat(zdd.contains(ZDDVariable.newVariable(3))).isFalse();
    }

    @Test
    public void union() {
        ZDDVariable variable1 = ZDDVariable.newVariable(0);
        ZDDVariable variable2 = ZDDVariable.newVariable(1);
        ZDDVariable variable3 = ZDDVariable.newVariable(2);

        ZDD union = RegularZDD.withVariable(variable1).union(RegularZDD.withVariable(variable2)).union(RegularZDD.withVariable(variable3));

        assertThat(union.contains(variable1)).isTrue();
        assertThat(union.contains(variable2)).isTrue();
        assertThat(union.contains(variable3)).isTrue();
    }

    @Test
    public void intersection() {
        ZDDVariable variable1 = ZDDVariable.newVariable(0);
        ZDDVariable variable2 = ZDDVariable.newVariable(1);
        ZDDVariable variable3 = ZDDVariable.newVariable(2);

        ZDD oneAndTwo = RegularZDD.withVariable(variable1).union(RegularZDD.withVariable(variable2));
        ZDD twoAndThree = RegularZDD.withVariable(variable2).union(RegularZDD.withVariable(variable3));

        ZDD intersection = oneAndTwo.intersection(twoAndThree);

        assertThat(intersection.contains(variable1)).isFalse();
        assertThat(intersection.contains(variable2)).isTrue();
        assertThat(intersection.contains(variable3)).isFalse();
    }
}
