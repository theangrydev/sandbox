package io.github.theangrydev.sandbox.zdd;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

public class ZDDUnion {

    private final LoadingCache<ZDDPair, ZDD> unions;

    public ZDDUnion() {
        unions = Caffeine.newBuilder()
                .softValues()
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
