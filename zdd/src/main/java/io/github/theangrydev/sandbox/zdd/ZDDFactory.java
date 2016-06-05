package io.github.theangrydev.sandbox.zdd;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

import java.util.Arrays;
import java.util.Comparator;

import static io.github.theangrydev.sandbox.zdd.OneZDD.ONE_ZDD;
import static io.github.theangrydev.sandbox.zdd.ZeroZDD.ZERO_ZDD;

public class ZDDFactory {

    private final Interner<ZDD> zdds = Interners.newWeakInterner();

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
