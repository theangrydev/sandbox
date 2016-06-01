package io.github.theangrydev.sandbox.zdd;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import static io.github.theangrydev.sandbox.zdd.OneZDD.ONE_ZDD;
import static io.github.theangrydev.sandbox.zdd.ZeroZDD.ZERO_ZDD;

public class ZDDFactoryTest implements WithAssertions {

    @Test
    public void createZDDWithZeroThenReturnsElse() {
        ZDDFactory zddFactory = new ZDDFactory(0, Runnable::run);

        ZDD elseZDD = zddFactory.createZDD(ZDDVariable.newVariable(0), ONE_ZDD, ZERO_ZDD);
        ZDD thenZDD = ZERO_ZDD;

        ZDD zdd = zddFactory.createZDD(ZDDVariable.newVariable(1), thenZDD, elseZDD);

        assertThat(zdd).isSameAs(elseZDD);
    }

    @Test
    public void createZDDThatHasAlreadyBeenCreatedReturnsSameZDD() {
        ZDDFactory zddFactory = new ZDDFactory(1, Runnable::run);

        ZDDVariable variable = ZDDVariable.newVariable(0);
        ZDD thenZDD = ONE_ZDD;
        ZDD elseZDD = ZERO_ZDD;

        ZDD zdd1 = zddFactory.createZDD(variable, thenZDD, elseZDD);
        ZDD zdd2 = zddFactory.createZDD(variable, thenZDD, elseZDD);

        assertThat(zdd1).isSameAs(zdd2);
    }

    @Test
    public void createZDDThatHasAlreadyBeenCreatedReturnsNewZDDIfCacheSizeIsReachedAndAnotherEntryIsMorePopular() {
        ZDDFactory zddFactory = new ZDDFactory(0, Runnable::run);

        ZDDVariable variable = ZDDVariable.newVariable(0);
        ZDD thenZDD = ONE_ZDD;
        ZDD elseZDD = ZERO_ZDD;

        ZDD original = zddFactory.createZDD(variable, thenZDD, elseZDD);
        zddFactory.createZDD(ZDDVariable.newVariable(1), thenZDD, elseZDD);
        zddFactory.createZDD(ZDDVariable.newVariable(1), thenZDD, elseZDD);
        ZDD laterOn = zddFactory.createZDD(variable, thenZDD, elseZDD);

        assertThat(original).isNotSameAs(laterOn);
    }
}