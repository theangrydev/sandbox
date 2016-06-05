package io.github.theangrydev.sandbox.zdd;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import static io.github.theangrydev.sandbox.zdd.OneZDD.ONE_ZDD;
import static io.github.theangrydev.sandbox.zdd.ZeroZDD.ZERO_ZDD;

public class ZDDBaseTest implements WithAssertions {

    @Test
    public void createZDDWithZeroThenReturnsElse() {
        ZDDBase zddBase = new ZDDBase();

        ZDD elseZDD = zddBase.createZDD(ZDDVariable.newVariable(0), ONE_ZDD, ZERO_ZDD);
        ZDD thenZDD = ZERO_ZDD;

        ZDD zdd = zddBase.createZDD(ZDDVariable.newVariable(1), thenZDD, elseZDD);

        assertThat(zdd).isSameAs(elseZDD);
    }

    @Test
    public void createZDDThatHasAlreadyBeenCreatedReturnsSameZDD() {
        ZDDBase zddBase = new ZDDBase();

        ZDDVariable variable = ZDDVariable.newVariable(0);
        ZDD thenZDD = ONE_ZDD;
        ZDD elseZDD = ZERO_ZDD;

        ZDD zdd1 = zddBase.createZDD(variable, thenZDD, elseZDD);
        ZDD zdd2 = zddBase.createZDD(variable, thenZDD, elseZDD);

        assertThat(zdd1).isSameAs(zdd2);
    }

    @Test
    public void createZDDThatHasAlreadyBeenCreatedReturnsNewZDDIfOldOneHasBeenGarbageCollected() {
        ZDDBase zddBase = new ZDDBase();

        ZDDVariable variable = ZDDVariable.newVariable(0);
        ZDD thenZDD = ONE_ZDD;
        ZDD elseZDD = ZERO_ZDD;

        int originalHash = System.identityHashCode(zddBase.createZDD(variable, thenZDD, elseZDD));

        System.gc();

        ZDD laterOn = zddBase.createZDD(variable, thenZDD, elseZDD);

        assertThat(System.identityHashCode(laterOn)).isNotEqualTo(originalHash);
    }
}