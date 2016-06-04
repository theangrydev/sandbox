package io.github.theangrydev.sandbox.zdd;

import com.google.code.tempusfugit.concurrency.RepeatingRule;
import com.google.code.tempusfugit.concurrency.annotations.Repeating;
import org.assertj.core.api.WithAssertions;
import org.junit.Rule;
import org.junit.Test;

public class WeakSetTest implements WithAssertions {

    @Rule
    public RepeatingRule repeating = new RepeatingRule();

    @Test
    public void notPresentKeyIsReturned() {
        WeakSet<Key> weakSet = new WeakSet<>();

        Key key = new Key(9999);
        Key keyReturned = weakSet.putIfAbsent(key);

        assertThat(keyReturned).isSameAs(key);
    }

    @Repeating(repetition = 1000)
    @Test
    public void presentKeyIsReturned() {
        WeakSet<Key> weakSet = new WeakSet<>();

        Key originalKey = new Key(9999);
        weakSet.putIfAbsent(originalKey);
        Key newKey = new Key(9999);
        Key keyReturned = weakSet.putIfAbsent(newKey);

        assertThat(keyReturned).isSameAs(originalKey);
    }

    @Repeating(repetition = 1000)
    @Test
    public void newKeyIsReturnedIfPresentKeyIsGarbageCollected() {
        WeakSet<Key> weakSet = new WeakSet<>();

        Key originalKey = new Key(9999);
        weakSet.putIfAbsent(originalKey);
        originalKey = null;
        System.gc();

        Key newKey = new Key(9999);
        Key keyReturned = weakSet.putIfAbsent(newKey);

        assertThat(keyReturned).isSameAs(newKey);
    }

    private class Key {
        private final int value;

        Key(int value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Key key = (Key) o;
            return value == key.value;
        }

        @Override
        public int hashCode() {
            return value;
        }
    }
}