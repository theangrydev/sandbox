package io.github.theangrydev.sandbox.zdd;

import org.assertj.core.api.WithAssertions;
import org.junit.Test;

import static io.github.theangrydev.sandbox.zdd.OneZDD.ONE_ZDD;

public class OneZDDTest implements WithAssertions {

    @Test
    public void oneZDD() {
        assertThat(ONE_ZDD).isNotNull();
    }

    @Test
    public void oneHasNoDirectAssignment() {
        assertThat(ONE_ZDD.directAssignment()).isEmpty();
    }
}