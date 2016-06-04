package io.github.theangrydev.sandbox.zdd;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Objects;

public class WeakSet<Key> {

    private final ReferenceQueue<Key> referenceQueue = new ReferenceQueue<>();
    private LoadingCache<WeakKey<Key>, WeakKey<Key>> cache;

    public WeakSet() {
        cache = Caffeine.newBuilder().build(key -> key);
    }

    public Key putIfAbsent(Key key) {
        purgeStaleKeys(); //TODO: could do this periodically
        WeakKey<Key> unique = cache.get(new WeakKey<>(key, referenceQueue));
        Key uniqueKey = unique.get();
        if (uniqueKey != null) {
            return uniqueKey;
        }
        return putIfAbsent(key);
    }

    private void purgeStaleKeys() {
        Reference<? extends Key> reference;
        while ((reference = referenceQueue.poll()) != null) {
            cache.invalidate(reference);
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
