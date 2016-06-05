package io.github.theangrydev.sandbox.zdd;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import static io.github.theangrydev.sandbox.zdd.OneZDD.ONE_ZDD;
import static io.github.theangrydev.sandbox.zdd.ZeroZDD.ZERO_ZDD;

public class ZDDFactoryTest implements WithAssertions {

    @Test
    public void createZDDWithZeroThenReturnsElse() {
        ZDDFactory zddFactory = new ZDDFactory();

        ZDD elseZDD = zddFactory.createZDD(ZDDVariable.newVariable(0), ONE_ZDD, ZERO_ZDD);
        ZDD thenZDD = ZERO_ZDD;

        ZDD zdd = zddFactory.createZDD(ZDDVariable.newVariable(1), thenZDD, elseZDD);

        assertThat(zdd).isSameAs(elseZDD);
    }

    @Test
    public void createZDDThatHasAlreadyBeenCreatedReturnsSameZDD() {
        ZDDFactory zddFactory = new ZDDFactory();

        ZDDVariable variable = ZDDVariable.newVariable(0);
        ZDD thenZDD = ONE_ZDD;
        ZDD elseZDD = ZERO_ZDD;

        ZDD zdd1 = zddFactory.createZDD(variable, thenZDD, elseZDD);
        ZDD zdd2 = zddFactory.createZDD(variable, thenZDD, elseZDD);

        assertThat(zdd1).isSameAs(zdd2);
    }

    @Test
    public void createZDDThatHasAlreadyBeenCreatedReturnsNewZDDIfOldOneHasBeenGarbageCollected() {
        ZDDFactory zddFactory = new ZDDFactory();

        ZDDVariable variable = ZDDVariable.newVariable(0);
        ZDD thenZDD = ONE_ZDD;
        ZDD elseZDD = ZERO_ZDD;

        int originalHash = System.identityHashCode(zddFactory.createZDD(variable, thenZDD, elseZDD));

        System.gc();

        ZDD laterOn = zddFactory.createZDD(variable, thenZDD, elseZDD);

        assertThat(System.identityHashCode(laterOn)).isNotEqualTo(originalHash);
    }
}