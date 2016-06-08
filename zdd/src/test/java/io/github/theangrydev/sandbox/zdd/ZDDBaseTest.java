package io.github.theangrydev.sandbox.zdd;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static io.github.theangrydev.sandbox.zdd.OneZDD.ONE_ZDD;
import static io.github.theangrydev.sandbox.zdd.ZeroZDD.ZERO_ZDD;
import static java.lang.String.format;
import static org.mockito.Mockito.*;

public class ZDDBaseTest implements WithAssertions {

    public static final int MEMORY_HOG_CHUNK = 102400;

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
    public void cacheIsClearedWhenMemoryIsTight() throws InterruptedException {
        ZDDBase zddBase = new ZDDBase();

        RegularZDD left = mock(RegularZDD.class);
        ZDD right = mock(ZDD.class);
        when(left.computeUnion(right)).thenReturn(mock(ZDD.class));

        zddBase.union(left, right);
        zddBase.union(left, right);
        verify(left).computeUnion(right);

        forceVeryLowHeapSpace();

        zddBase.union(left, right);
        verify(left, times(2)).computeUnion(right);
    }

    private void forceVeryLowHeapSpace() throws InterruptedException {
        CountDownLatch waitForGarbageCollection = new CountDownLatch(1);
        List<long[]> memoryHog = forceMostMemoryToBeUsedUp();
        NotificationListener garbageCollectionListener = new NotificationListener() {
            @Override
            public void handleNotification(Notification notification, Object handback) {
                removeGarbageCollectionListener(this);
                waitForGarbageCollection.countDown();
                System.out.println("Noticed a GC");
            }
        };
        addGarbageCollectionListener(garbageCollectionListener);
        System.out.println(format("Memory hog is %d longs", + memoryHog.size() * MEMORY_HOG_CHUNK));

        waitForGarbageCollection.await();
        System.out.println("Garbage collection assumed to have taken place now");
    }

    private void addGarbageCollectionListener(NotificationListener garbageCollectionListener) {
        for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            NotificationEmitter emitter = (NotificationEmitter) garbageCollectorMXBean;
            emitter.addNotificationListener(garbageCollectionListener, null, null);
        }
    }

    private void removeGarbageCollectionListener(NotificationListener garbageCollectionListener) {
        for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            NotificationEmitter emitter = (NotificationEmitter) garbageCollectorMXBean;
            try {
                emitter.removeNotificationListener(garbageCollectionListener);
            } catch (ListenerNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    private List<long[]> forceMostMemoryToBeUsedUp() {
        List<long[]> memoryHog = new LinkedList<>();
        try {
            while (true) {
                memoryHog.add(new long[MEMORY_HOG_CHUNK]);
            }
        } catch (OutOfMemoryError outOfMemoryError) {
            System.out.println("Out of memory error produced; the GC must have been run by now");
            return memoryHog;
        }
    }
}