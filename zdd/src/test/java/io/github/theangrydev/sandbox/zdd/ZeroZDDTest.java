package io.github.theangrydev.sandbox.zdd;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import static io.github.theangrydev.sandbox.zdd.ZeroZDD.ZERO_ZDD;

public class ZeroZDDTest implements WithAssertions {

    @Test
    public void zeroZDD() {
        assertThat(ZERO_ZDD).isNotNull();
    }

    @Test
    public void toStringIsTheEmptySet() {
        assertThat(ZERO_ZDD).hasToString("∅");
    }
}