package com.eveningoutpost.dexdrip.adapters;

import androidx.collection.ArrayMap;
import androidx.databinding.MapChangeRegistry;
import androidx.databinding.ObservableMap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ObservableArrayMapNoNotify<K, V> implements ObservableMap<K, V> {

    private final ArrayMap<K, V> backing = new ArrayMap<>();
    private transient MapChangeRegistry mListeners;

    @Override
    public void addOnMapChangedCallback(OnMapChangedCallback<? extends ObservableMap<K, V>, K, V> listener) {
        if (mListeners == null) mListeners = new MapChangeRegistry();
        mListeners.add(listener);
    }

    @Override
    public void removeOnMapChangedCallback(OnMapChangedCallback<? extends ObservableMap<K, V>, K, V> listener) {
        if (mListeners != null) mListeners.remove(listener);
    }

    private void notifyChange(Object key) {
        if (mListeners != null) mListeners.notifyCallbacks(this, 0, key);
    }

    // ---- Map / ObservableMap ----
    @Override public int size() { return backing.size(); }
    @Override public boolean isEmpty() { return backing.isEmpty(); }
    @Override public boolean containsKey(Object key) { return backing.containsKey(key); }
    @Override public boolean containsValue(Object value) { return backing.containsValue(value); }
    @Override public V get(Object key) { return backing.get(key); }

    @Override
    public V put(K key, V value) {
        V old = backing.put(key, value);
        notifyChange(key);
        return old;
    }

    public V putNoNotify(K key, V value) {
        return backing.put(key, value);
    }

    @Override
    public V remove(Object key) {
        V old = backing.remove(key);
        if (old != null) notifyChange(key);
        return old;
    }

    @Override
    public boolean remove(Object key, Object value) {
        boolean removed = backing.remove(key, value);
        if (removed) notifyChange(key);
        return removed;
    }

    @Override
    public void clear() {
        if (!backing.isEmpty()) {
            backing.clear();
            notifyChange(null);
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override public Set<K> keySet() { return backing.keySet(); }
    @Override public Collection<V> values() { return backing.values(); }
    @Override public Set<Entry<K, V>> entrySet() { return backing.entrySet(); }

    // Optional, falls du diese API im Projekt nutzt:
    public K keyAt(int index) { return backing.keyAt(index); }
    public V valueAt(int index) { return backing.valueAt(index); }
    public V removeAt(int index) { K k = backing.keyAt(index); V v = backing.removeAt(index); if (v != null) notifyChange(k); return v; }
    public V setValueAt(int index, V value) { K k = backing.keyAt(index); V old = backing.setValueAt(index, value); notifyChange(k); return old; }
}
