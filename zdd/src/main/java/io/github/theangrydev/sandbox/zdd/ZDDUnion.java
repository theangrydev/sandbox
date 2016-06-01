package io.github.theangrydev.sandbox.zdd;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.assertj.core.util.VisibleForTesting;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public class ZDDUnion {

    private final LoadingCache<ZDDPair, ZDD> unions;

    public ZDDUnion(int maximumSize) {
        this(maximumSize, ForkJoinPool.commonPool());
    }

    @VisibleForTesting
    ZDDUnion(int maximumSize, Executor executor) {
        unions = Caffeine.newBuilder()
                .executor(executor)
                .maximumSize(maximumSize)
                .build(zddPair -> zddPair.left.union(zddPair.right));
    }

    public ZDD union(ZDD left, ZDD right) {
        return unions.get(new ZDDPair(left, right));
    }

    private static class ZDDPair extends ValueType {
        private final ZDD left;
        private final ZDD right;

        private ZDDPair(ZDD left, ZDD right) {
            this.left = left;
            this.right = right;
        }
    }
}
