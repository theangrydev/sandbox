package io.github.theangrydev.sandbox.zdd;


import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class WeakSet<Key> {

    private final ReferenceQueue<Key> referenceQueue = new ReferenceQueue<>();
    private final ConcurrentHashMap<WeakKey<Key>, WeakKey<Key>> map = new ConcurrentHashMap<>();

    public Key putIfAbsent(Key key) {
        purgeStaleKeys();
        WeakKey<Key> weakKey = new WeakKey<>(key, referenceQueue);
        WeakKey<Key> previous = map.putIfAbsent(weakKey, weakKey);
        if (previous == null) {
            return key;
        }
        Key previousKey = previous.get();
        if (previousKey != null) {
            return previousKey;
        }
        // the key must have been garbage collected since the putIfAbsent
        // this is a VERY unlucky case and so we just retry
        return putIfAbsent(key);
    }

    private void purgeStaleKeys() {
        Reference<? extends Key> reference;
        while ((reference = referenceQueue.poll()) != null) {
            map.remove(reference);
        }
    }

    static class WeakKey<Key> extends WeakReference<Key> {

        private final int hashCode;

        WeakKey(Key key, ReferenceQueue<Key> referenceQueue) {
            super(key, referenceQueue);
            hashCode = key.hashCode();
        }

        @Override
        public int hashCode() {
            return hashCode;
        }

        @Override
        public boolean equals(Object other) {
            return Objects.equals(get(), ((WeakKey) other).get());
        }
    }
}
