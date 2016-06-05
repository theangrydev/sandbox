package io.github.theangrydev.sandbox.zdd;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

import java.util.Arrays;
import java.util.Comparator;

import static io.github.theangrydev.sandbox.zdd.OneZDD.ONE_ZDD;
import static io.github.theangrydev.sandbox.zdd.ZeroZDD.ZERO_ZDD;

public class ZDDBase {

    private final Interner<ZDD> zdds = Interners.newWeakInterner();
    private final LoadingCache<ZDDBase.ZDDPair, ZDD> unions;

    public ZDDBase() {
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

    public ZDD createZDD(ZDDVariable variable, ZDD thenZdd, ZDD elseZdd) {
        if (thenZdd == ZERO_ZDD) {
            return elseZdd;
        }
        return zdds.intern(new RegularZDD(this, variable, thenZdd, elseZdd));
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
