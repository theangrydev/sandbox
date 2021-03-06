package io.github.theangrydev.sandbox.zdd;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import static io.github.theangrydev.sandbox.zdd.OneZDD.ONE_ZDD;
import static io.github.theangrydev.sandbox.zdd.ZeroZDD.ZERO_ZDD;

public class ZDDTest implements WithAssertions {

    private final ZDDBase zddBase = new ZDDBase();

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
    public void extendSingleSets() {
        ZDDVariable variable1 = ZDDVariable.newVariable(0);
        ZDDVariable variable2 = ZDDVariable.newVariable(1);

        ZDD extended = setOf(variable1).extend(setOf(variable2));

        assertThat(extended.contains(setOf(variable1, variable2))).isTrue();
        assertThat(extended.contains(setOf(variable1))).isFalse();
        assertThat(extended.contains(setOf(variable2))).isFalse();
    }

    @Test
    public void extendFamilyOfSetsWithSingleSet() {
        ZDDVariable variable1 = ZDDVariable.newVariable(1);
        ZDDVariable variable2 = ZDDVariable.newVariable(2);
        ZDDVariable variable3 = ZDDVariable.newVariable(3);
        ZDDVariable variable4 = ZDDVariable.newVariable(4);

        ZDD family = setOf(variable1, variable2, variable3).union(setOf(variable1, variable2));
        ZDD singleSet = setOf(variable4);

        ZDD extended = family.extend(singleSet);

        assertThat(extended.contains(setOf(variable1, variable2, variable3, variable4))).isTrue();
        assertThat(extended.contains(setOf(variable1, variable2, variable4))).isTrue();
        assertThat(extended).hasToString("{{1,2,3,4},{1,2,4}}");
    }

    @Test
    public void extendSingleSetWithFamilyOfSets() {
        ZDDVariable variable1 = ZDDVariable.newVariable(1);
        ZDDVariable variable2 = ZDDVariable.newVariable(2);
        ZDDVariable variable3 = ZDDVariable.newVariable(3);
        ZDDVariable variable4 = ZDDVariable.newVariable(4);

        ZDD family = setOf(variable1, variable2, variable3).union(setOf(variable1, variable2));
        ZDD singleSet = setOf(variable4);

        ZDD extended = singleSet.extend(family);

        assertThat(extended.contains(setOf(variable1, variable2, variable3, variable4))).isTrue();
        assertThat(extended.contains(setOf(variable1, variable2, variable4))).isTrue();
        assertThat(extended).hasToString("{{1,2,3,4},{1,2,4}}");
    }

    @Test
    public void extendSingleSetWithEmptyFamily() {
        ZDDVariable variable1 = ZDDVariable.newVariable(1);

        ZDD singleSet = setOf(variable1);

        ZDD extended = singleSet.extend(ZERO_ZDD);

        assertThat(extended.contains(setOf(variable1))).isTrue();
        assertThat(extended).hasToString("{{1}}");
    }

    @Test
    public void extendSingleSetWithOne() {
        ZDDVariable variable1 = ZDDVariable.newVariable(1);

        ZDD singleSet = setOf(variable1);

        ZDD extended = singleSet.extend(ONE_ZDD);

        assertThat(extended.contains(setOf(variable1))).isTrue();
        assertThat(extended).hasToString("{{1}}");
    }

    @Test
    public void extendSingleSetWithFamilyThatContainsEmptySet() {
        ZDDVariable variable1 = ZDDVariable.newVariable(1);
        ZDDVariable variable2 = ZDDVariable.newVariable(2);

        ZDD setThatContainsEmptySet = setOf(variable1).union(ONE_ZDD);
        ZDD singleSet = setOf(variable2);

        ZDD extended = singleSet.extend(setThatContainsEmptySet);

        assertThat(extended.contains(setOf(variable1, variable2))).isTrue();
        assertThat(extended.contains(setOf(variable2))).isTrue();
        assertThat(extended).hasToString("{{1,2},{2}}");
    }

    @Test
    public void extendSingleSetWithFamilyOfOverlappingSets() {
        ZDDVariable variable1 = ZDDVariable.newVariable(1);
        ZDDVariable variable2 = ZDDVariable.newVariable(2);
        ZDDVariable variable3 = ZDDVariable.newVariable(3);
        ZDDVariable variable4 = ZDDVariable.newVariable(4);

        ZDD familyOfOverlappingSets = setOf(variable1, variable2, variable3).union(setOf(variable1, variable2));
        ZDD singleSet = setOf(variable4);

        ZDD extended = singleSet.extend(familyOfOverlappingSets);

        assertThat(extended.contains(setOf(variable1, variable2, variable3, variable4))).isTrue();
        assertThat(extended.contains(setOf(variable1, variable2, variable4))).isTrue();
        assertThat(extended).hasToString("{{1,2,3,4},{1,2,4}}");
    }

    @Test
    public void extendOverlapping() {
        ZDDVariable variable1 = ZDDVariable.newVariable(0);
        ZDDVariable variable2 = ZDDVariable.newVariable(0);

        ZDD extended = setOf(variable1).extend(setOf(variable1, variable2));

        assertThat(extended.contains(setOf(variable1, variable2))).isTrue();
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
        ZDDVariable variable4 = ZDDVariable.newVariable(3);
        ZDDVariable variable5 = ZDDVariable.newVariable(4);

        ZDD union = setOf(variable1, variable2, variable3).union(setOf(variable4, variable5));

        assertThat(union.contains(setOf(variable1, variable2, variable3))).isTrue();
        assertThat(union.contains(setOf(variable4, variable5))).isTrue();
        assertThat(union.contains(setOf(variable1, variable2, variable3, variable4, variable5))).isFalse();
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
        assertThat(union.contains(ZERO_ZDD)).isTrue();
        assertThat(union).hasToString("{{0},∅}");
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

        assertThat(remove).hasToString("{{2}}");
        assertThat(remove.contains(setOf(variable3))).isTrue();
        assertThat(remove.contains(setOf(variable1, variable2, variable3))).isFalse();
    }

    @Test
    public void removeAllElementsInMultipleForFamilyThatStartsAfterThis() {
        ZDDVariable variable2 = ZDDVariable.newVariable(2);
        ZDDVariable variable4 = ZDDVariable.newVariable(4);
        ZDDVariable variable6 = ZDDVariable.newVariable(6);

        ZDD remove = setOf(variable2, variable4, variable6).removeAllElementsIn(setOf(variable4, variable6));

        assertThat(remove).hasToString("{{2}}");
        assertThat(remove.contains(setOf(variable2))).isTrue();
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
        assertThat(next).hasToString("{{4},{5}}");
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

    @Test
    public void toStringSingleElementSet() {
        ZDDVariable variable1 = ZDDVariable.newVariable(0);

        assertThat(setOf(variable1)).hasToString("{{0}}");
    }

    @Test
    public void toStringSingleMultipleElementSet() {
        ZDDVariable variable1 = ZDDVariable.newVariable(1);
        ZDDVariable variable2 = ZDDVariable.newVariable(2);

        assertThat(setOf(variable1, variable2)).hasToString("{{1,2}}");
    }

    @Test
    public void toStringFamilyOfDisjointSets() {
        ZDDVariable variable1 = ZDDVariable.newVariable(1);
        ZDDVariable variable2 = ZDDVariable.newVariable(2);
        ZDDVariable variable3 = ZDDVariable.newVariable(3);
        ZDDVariable variable4 = ZDDVariable.newVariable(4);

        assertThat(setOf(variable1, variable2).union(setOf(variable3, variable4))).hasToString("{{1,2},{3,4}}");
    }

    @Test
    public void toStringFamilyOfOverlappingSets() {
        ZDDVariable variable1 = ZDDVariable.newVariable(1);
        ZDDVariable variable2 = ZDDVariable.newVariable(2);
        ZDDVariable variable3 = ZDDVariable.newVariable(3);
        ZDDVariable variable4 = ZDDVariable.newVariable(4);

        ZDD familyOfOverlappingSets = setOf(variable1, variable2, variable3, variable4).union(setOf(variable1, variable2, variable4));

        assertThat(familyOfOverlappingSets).hasToString("{{1,2,3,4},{1,2,4}}");
    }

    private ZDD setOf(ZDDVariable... variables) {
        return zddBase.setOf(variables);
    }
}
