package com.slimgears.slimbus.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Denis on 24/09/2015.
 *
 */
public class ListMap<K, V> extends HashMap<K, List<V>> {
    public List<V> getOrPut(K key) {
        if (!containsKey(key)) {
            List<V> list = new ArrayList<>();
            put(key, list);
            return list;
        }
        return get(key);
    }
}
