package io.github.theangrydev.sandbox.zdd;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class ZDDUnionTest implements WithAssertions {

    @Test
    public void unionIsCached() {
        ZDDUnion zddUnion = new ZDDUnion();
        ZDD left = mock(ZDD.class);
        ZDD right = mock(ZDD.class);
        ZDD union = mock(ZDD.class);
        when(left.union(right)).thenReturn(union);

        ZDD firstUnion = zddUnion.union(left, right);
        ZDD secondUnion = zddUnion.union(left, right);

        assertThat(firstUnion).isSameAs(union);
        assertThat(secondUnion).isSameAs(union);

        verify(left).union(right);
        verifyNoMoreInteractions(left, right);
    }

    @Test
    public void cacheIsClearedWhenSoftReferencesAreFreed() throws InterruptedException {
        ZDDUnion zddUnion = new ZDDUnion();

        ZDDWithFixedUnion left = new ZDDWithFixedUnion();
        ZDD right = mock(ZDD.class);

        zddUnion.union(left, right);
        zddUnion.union(left, right);
        assertThat(left.unionCount).isEqualTo(1);

        left.union = new RegularZDD(null, ZDDVariable.newVariable(1), OneZDD.ONE_ZDD, OneZDD.ONE_ZDD);;

        forceSoftReferencesToBeCleared();

        zddUnion.union(left, right);
        zddUnion.union(left, right);
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

    private static class ZDDWithFixedUnion implements ZDD {

        private RegularZDD union = new RegularZDD(null, ZDDVariable.newVariable(1), OneZDD.ONE_ZDD, OneZDD.ONE_ZDD);
        private int unionCount = 0;

        @Override
        public ZDDVariable variable() {
            return null;
        }

        @Override
        public ZDD thenZDD() {
            return null;
        }

        @Override
        public ZDD elseZDD() {
            return null;
        }

        @Override
        public ZDD union(ZDD zdd) {
            unionCount++;
            return union;
        }

        @Override
        public ZDD intersection(ZDD zdd) {
            return null;
        }

        @Override
        public ZDD extend(ZDD zdd) {
            return null;
        }

        @Override
        public ZDD retainOverlapping(ZDD zdd) {
            return null;
        }

        @Override
        public ZDD removeAllElementsIn(ZDD zdd) {
            return null;
        }

        @Override
        public boolean contains(ZDD zdd) {
            return false;
        }

        @Override
        public Optional<ZDDVariable> directAssignment() {
            return null;
        }

        @Override
        public void appendSets(StringBuilder prefix, StringBuilder stringBuilder) {

        }
    }
}
