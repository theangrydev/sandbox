package io.github.theangrydev.sandbox.zdd;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

public class ExampleTest implements WithAssertions {

    private final ZDDUnion zddUnion = new ZDDUnion(1000);
    private final ZDDFactory zddFactory = new ZDDFactory();

    @Test
    public void movingFromOneFrontierToTheNext() {
        ZDDVariable fromA = ZDDVariable.newVariable(0);
        ZDDVariable fromB = ZDDVariable.newVariable(1);
        ZDDVariable fromC = ZDDVariable.newVariable(2);
        ZDDVariable charAB = ZDDVariable.newVariable(3);
        ZDDVariable charBC = ZDDVariable.newVariable(4);
        ZDDVariable toA = ZDDVariable.newVariable(5);
        ZDDVariable toB = ZDDVariable.newVariable(6);
        ZDDVariable toC = ZDDVariable.newVariable(7);

        ZDD fromAndCharacters = zddFactory.setOf(fromA, fromB, fromC, charAB, charBC); // {{fromA, fromB, charAB, charBC}}
        ZDD toAndCharacters = zddFactory.setOf(toA, toB, toC, charAB, charBC); // {{toA, toB, charAB, charBC}}
        ZDD fromToTransitionTable = zddFactory.setOf(fromA, charAB, toB).union(zddFactory.setOf(fromB, charBC, toC)); // {{fromA, charAB, toB}, {fromB, charBC, toC}}
        ZDD toFromTransitionTable = zddFactory.setOf(toA, charAB, fromB).union(zddFactory.setOf(toB, charBC, fromC)); // {{toA, charAB, fromB}, {toB, charBC, fromC}}

        ZDD frontier = zddFactory.setOf(fromA); // {{fromA}}
        ZDD frontierWithTransition = frontier.extend(zddFactory.setOf(charAB)); // {{fromA, charAB}}
        ZDD frontierApplicableTransition = fromToTransitionTable.retainOverlapping(frontierWithTransition); // {{fromA, charAB, toB}}

        ZDD nextFrontier = frontierApplicableTransition.removeAllElementsIn(fromAndCharacters); // {{toB}}
        ZDD nextFrontierWithTransition = nextFrontier.extend(zddFactory.setOf(charBC)); // {{toB, charBC}
        ZDD nextFrontierApplicableTransition = toFromTransitionTable.retainOverlapping(nextFrontierWithTransition); // {{toB, charBC, fromC}}

        ZDD finalFrontier = nextFrontierApplicableTransition.removeAllElementsIn(toAndCharacters); // {{fromC}}

        assertThat(finalFrontier.directAssignment()).contains(fromC);
    }
}
