package ixs171130;

import java.util.NavigableMap;
import java.util.TreeMap;

class IdIndex<K, V> {
    private TreeMap<K, V> index;

    public IdIndex() {
        index = new TreeMap<>();
    }

    public boolean containsKey(K id) {
        return this.index.containsKey(id);
    }

    public V get(K id) {
        return this.index.get(id);
    }

    public V put(K id, V p) {
        return this.index.put(id, p);
    }

    public V remove(K id)
    {
        return this.index.remove(id);
    }

    public NavigableMap<K, V> subMap(K l, boolean inclusiveLow, K h, boolean inclusiveHigh) {
        return this.index.subMap(l, inclusiveLow, h, inclusiveHigh);
    }
}
