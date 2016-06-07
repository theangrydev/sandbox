package io.github.theangrydev.sandbox.zdd;

import com.github.benmanes.caffeine.cache.*;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import static io.github.theangrydev.sandbox.zdd.OneZDD.ONE_ZDD;
import static io.github.theangrydev.sandbox.zdd.ZeroZDD.ZERO_ZDD;

public class ZDDBase {

    private static final Function<ZDDPair, ZDD> COMPUTE_UNION = zddPair -> zddPair.left.computeUnion(zddPair.right);

    private static final long MEMORY_PER_UNION = 2; //TODO: upper bound for this at runtime
    private static final long MEMORY_PER_REGULAR_ZDD = 32; //TODO: upper bound for this at runtime

    private final long targetMemoryUsage = 1024L * 1L + 198976 + 2; //TODO: don't really like this, ideally would just respond to memory demand. could

    private final Lock memoryLock = new ReentrantLock();

    private final AtomicLong activeZDDs;
    private final Interner<ZDD> zdds = Interners.newWeakInterner();
    private long activeZDDThreshold;

    private final LoadingCache<ZDDPair, ZDD> unions;
    private final Policy.Eviction<ZDDPair, ZDD> unionPolicy;

    private final AtomicLong operationCount = new AtomicLong(0);
    private long cacheAlgorithmThreshold;

    private double previousCacheHitRate = 0.0d;

    public ZDDBase() {
        //TODO: listen to GCs and if the free heap space is less than some threshold, shrink the cache
//        MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
//        NotificationEmitter emitter = (NotificationEmitter) mbean;
//        emitter.addNotificationListener(new NotificationListener() {
//            @Override
//            public void handleNotification(Notification notification, Object handback) {
//                System.out.println("notification = " + notification);
//            }
//        }, null, null);

//        ManagementFactory.getMemoryPoolMXBeans().stream().filter(bean -> bean.getName())
        unions = Caffeine.newBuilder()
                .maximumSize(100_000)
                .buildAsync(this::neverUse)
                .synchronous();
        unionPolicy = unions.policy().eviction().orElseThrow(() -> new IllegalStateException("Cache should have a maximum size policy"));
        activeZDDs = new AtomicLong(0);
        cacheAlgorithmThreshold = unionPolicy.getMaximum();
        updateActiveZDDThreshold();
    }

    private ZDD neverUse(ZDDPair ignored) {
        throw new IllegalStateException("Never use the loader");
    }

    public ZDD union(RegularZDD left, ZDD right) {
        maintainCache();
        return unions.get(new ZDDPair(left, right), COMPUTE_UNION);
    }

    private void maintainCache() {
        if (operationCount.incrementAndGet() >= cacheAlgorithmThreshold && memoryLock.tryLock()) {
            try {
                double currentCacheHitRate = unions.stats().hitRate();
                if (currentCacheHitRate > previousCacheHitRate || unionPolicy.getMaximum() < activeZDDs.get() * currentCacheHitRate) {
                    unionPolicy.setMaximum(unionPolicy.getMaximum() * 2); // TODO: clamp to minimum working memory
                    updateActiveZDDThreshold();
                }
                previousCacheHitRate = currentCacheHitRate;
                cacheAlgorithmThreshold *= 2;
            } finally {
                memoryLock.unlock();
            }
        }
    }

    public void decrementActiveZDDs() {
        activeZDDs.decrementAndGet();
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
        RegularZDD newZDD = new RegularZDD(this, variable, thenZdd, elseZdd);
        ZDD returnedZDD = zdds.intern(newZDD);
        if (newZDD == returnedZDD) {
            activeZDDs.incrementAndGet();
            shrinkCachesIfMemoryUsageIsTooHigh();
        }
        return returnedZDD;
    }

    private void shrinkCachesIfMemoryUsageIsTooHigh() {
        if (activeZDDs.intValue() >= activeZDDThreshold && memoryLock.tryLock()) {
            try {
                unionPolicy.setMaximum(unionPolicy.getMaximum() / 2);
                updateActiveZDDThreshold();
            } finally {
                memoryLock.unlock();
            }
        }
    }

    private void updateActiveZDDThreshold() {
        activeZDDThreshold = targetMemoryUsage - unionPolicy.getMaximum() * MEMORY_PER_UNION - activeZDDs.intValue() * MEMORY_PER_REGULAR_ZDD;
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
