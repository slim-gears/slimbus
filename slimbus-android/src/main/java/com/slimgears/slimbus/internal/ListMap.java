package com.slimgears.slimbus.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Denis on 24/09/2015.
 *
 */
public class ListMap<K, V> extends HashMap<K, List<V>> {
    @Override
    public List<V> get(Object key) {
        List<V> list = super.get(key);
        return list != null ? list : Collections.emptyList();
    }

    public List<V> getOrPut(K key) {
        List<V> list = super.get(key);
        if (list == null) {
            list = new ArrayList<>();
            put(key, list);
        }
        return list;
    }
}
