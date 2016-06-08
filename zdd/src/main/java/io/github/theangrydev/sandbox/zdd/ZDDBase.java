package io.github.theangrydev.sandbox.zdd;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Policy;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import static io.github.theangrydev.sandbox.zdd.OneZDD.ONE_ZDD;
import static io.github.theangrydev.sandbox.zdd.ZeroZDD.ZERO_ZDD;

public class ZDDBase {

    private static final Function<ZDDPair, ZDD> COMPUTE_UNION = zddPair -> zddPair.left.computeUnion(zddPair.right);

    private final Interner<ZDD> zdds = Interners.newWeakInterner();

    private final LoadingCache<ZDDPair, ZDD> unions;
    private final Policy.Eviction<ZDDPair, ZDD> unionPolicy;

    private final Lock memoryLock = new ReentrantLock();
    private final LongAdder operationCount = new LongAdder();
    private volatile long cacheAlgorithmThreshold;

    private static final Runtime RUNTIME = Runtime.getRuntime();

    public ZDDBase() {
        unions = Caffeine.newBuilder()
                .maximumSize(100_000)
                .buildAsync(this::neverUse)
                .synchronous();
        unionPolicy = unions.policy().eviction().orElseThrow(() -> new IllegalStateException("Cache should have a maximum size policy"));
        cacheAlgorithmThreshold = unionPolicy.getMaximum();

        NotificationListener garbageCollectionListener = (notification, handback) -> {
            long usedMemory = RUNTIME.totalMemory() - RUNTIME.freeMemory();
            long maxMemory = RUNTIME.maxMemory();
            long freeMemory = maxMemory - usedMemory;
            double percentageFree = (double) freeMemory / (double) maxMemory;
            if (percentageFree < 0.1) {
                unionPolicy.setMaximum(0); //TODO: determine new size based on estimated size (half?)
                unions.invalidateAll(); //TODO: reduce rather than invalidate
                System.out.println(String.format("Free memory is at %.2f%% (%d / %d)", percentageFree, freeMemory, maxMemory));
            }
        };

        for (GarbageCollectorMXBean garbageCollectorMXBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            NotificationEmitter emitter = (NotificationEmitter) garbageCollectorMXBean;
            emitter.addNotificationListener(garbageCollectionListener, null, null);
        }
    }

    private ZDD neverUse(ZDDPair ignorejd) {
        throw new IllegalStateException("Never use the loader");
    }

    public ZDD union(RegularZDD left, ZDD right) {
        maintainCache();
        return unions.get(new ZDDPair(left, right), COMPUTE_UNION);
    }

    private void maintainCache() {
        if (operationCount.longValue() >= cacheAlgorithmThreshold && memoryLock.tryLock()) {
            try {
                unionPolicy.setMaximum(unionPolicy.getMaximum() * 2); // TODO: clamp to minimum working memory
//                cacheAlgorithmThreshold = Runtime.getRuntime().freeMemory(); //TODO: update based on free memory
            } finally {
                memoryLock.unlock();
            }
        }
    }

    private static class ZDDPair extends ValueType {
        private final RegularZDD left;
        private final ZDD right;

        private ZDDPair(RegularZDD left, ZDD right) {
            this.left = left;
            this.right = right;
        }
    }

    public ZDD createZDD(ZDDVariable variable, ZDD thenZDD, ZDD elseZDD) {
        if (thenZDD == ZERO_ZDD) {
            return elseZDD;
        }
        return internZDD(variable, thenZDD, elseZDD);
    }

    private ZDD internZDD(ZDDVariable variable, ZDD thenZdd, ZDD elseZdd) {
        return zdds.intern(new RegularZDD(this, variable, thenZdd, elseZdd));
    }

    //TODO: use this in gc listener and look at RUNTIME memory
    private void shrinkCachesIfMemoryUsageIsTooHigh() {
        if (memoryLock.tryLock()) {
            try {
                unionPolicy.setMaximum(unionPolicy.getMaximum() / 2);
            } finally {
                memoryLock.unlock();
            }
        }
    }

    public ZDD setOf(ZDDVariable... zddVariables) {
        Arrays.sort(zddVariables, Comparator.reverseOrder());
        ZDD zdd = ONE_ZDD;
        for (ZDDVariable zddVariable : zddVariables) {
            zdd = createZDD(zddVariable, zdd, ZERO_ZDD);
        }
        return zdd;
    }
}
