package io.github.theangrydev.sandbox.zdd;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static io.github.theangrydev.sandbox.zdd.OneZDD.ONE_ZDD;
import static io.github.theangrydev.sandbox.zdd.ZeroZDD.ZERO_ZDD;
import static org.mockito.Mockito.*;

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


    @Test
    public void unionIsCached() {
        ZDDBase zddBase = new ZDDBase();
        RegularZDD left = mock(RegularZDD.class);
        ZDD right = mock(ZDD.class);
        ZDD union = mock(ZDD.class);
        when(left.computeUnion(right)).thenReturn(union);

        ZDD firstUnion = zddBase.union(left, right);
        ZDD secondUnion = zddBase.union(left, right);

        assertThat(firstUnion).isSameAs(union);
        assertThat(secondUnion).isSameAs(union);

        verify(left).computeUnion(right);
        verifyNoMoreInteractions(left, right);
    }

    @Test
    public void cacheIsClearedWhenSoftReferencesAreFreed() throws InterruptedException {
        ZDDBase zddBase = new ZDDBase();

        ZDDWithFixedUnion left = new ZDDWithFixedUnion();
        ZDD right = mock(ZDD.class);

        zddBase.union(left, right);
        zddBase.union(left, right);
        assertThat(left.unionCount).isEqualTo(1);

        left.union = new RegularZDD(null, ZDDVariable.newVariable(1), OneZDD.ONE_ZDD, OneZDD.ONE_ZDD);

        forceSoftReferencesToBeCleared();

        zddBase.union(left, right);
        zddBase.union(left, right);
        assertThat(left.unionCount).isEqualTo(2);
    }

    private void forceSoftReferencesToBeCleared() {
        try {
            List<long[]> memoryHog = new LinkedList<>();
            while (true) {
                memoryHog.add(new long[102400]);
            }
        } catch (OutOfMemoryError outOfMemoryError) {
            System.out.println("Out of memory error produced; the GC must have cleared all the soft references by now!");
        }
    }

    private static class ZDDWithFixedUnion extends RegularZDD {

        private RegularZDD union = new RegularZDD(null, ZDDVariable.newVariable(1), OneZDD.ONE_ZDD, OneZDD.ONE_ZDD);
        private int unionCount = 0;

        ZDDWithFixedUnion() {
            super(null, null, null, null);
        }

        @Override
        public ZDD computeUnion(ZDD zdd) {
            unionCount++;
            return union;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "@" + hashCode();
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }

        @Override
        public boolean equals(Object object) {
            return this == object;
        }
    }
}