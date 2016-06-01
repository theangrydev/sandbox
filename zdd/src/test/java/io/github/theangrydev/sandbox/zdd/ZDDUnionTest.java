package io.github.theangrydev.sandbox.zdd;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ZDDUnionTest implements WithAssertions {

    @Test
    public void unionIsCached() {
        ZDDUnion zddUnion = new ZDDUnion(1000, Runnable::run);
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
    public void fullUnionCacheForcesARecompute() {
        ZDDUnion zddUnion = new ZDDUnion(1, Runnable::run);

        ZDD left1 = mock(ZDD.class);
        ZDD left2 = mock(ZDD.class);
        ZDD right1 = mock(ZDD.class);
        ZDD right2 = mock(ZDD.class);
        ZDD union1 = mock(ZDD.class);
        ZDD union2 = mock(ZDD.class);
        when(left1.union(right1)).thenReturn(union1);
        when(left2.union(right2)).thenReturn(union2);

        zddUnion.union(left1, right1);
        zddUnion.union(left2, right2);
        zddUnion.union(left2, right2);
        zddUnion.union(left1, right1);

        verify(left1, times(2)).union(right1);
        verify(left2, times(1)).union(right2);
    }
}
