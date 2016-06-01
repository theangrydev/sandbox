package io.github.theangrydev.sandbox.zdd;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.assertj.core.util.VisibleForTesting;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import static io.github.theangrydev.sandbox.zdd.OneZDD.ONE_ZDD;
import static io.github.theangrydev.sandbox.zdd.ZeroZDD.ZERO_ZDD;

public class ZDDFactory {

    private final LoadingCache<ZDD, ZDD> zdds;

    public ZDDFactory(int maximumSize) {
        this(maximumSize, ForkJoinPool.commonPool());
    }

    @VisibleForTesting
    ZDDFactory(int maximumSize, Executor executor) {
        zdds = Caffeine.newBuilder()
                .executor(executor)
                .maximumSize(maximumSize)
                .build(key -> key);
    }

    public ZDD createZDD(ZDDVariable variable, ZDD thenZdd, ZDD elseZdd) {
        if (thenZdd == ZERO_ZDD) {
            return elseZdd;
        }
        return zdds.get(new RegularZDD(this, variable, thenZdd, elseZdd));
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
