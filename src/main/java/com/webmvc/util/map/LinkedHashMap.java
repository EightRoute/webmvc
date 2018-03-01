package com.webmvc.util.map;

import java.util.Map;

/**
 * LinkedHashMap
 */
public class LinkedHashMap<K, V> extends HashMap<K, V> 
		implements Map<K, V>{

	private static final long serialVersionUID = 8363940143484343740L;

	static class Entry<K, V> extends HashMap.Node<K, V> {
		Entry<K, V> before, after;
		Entry(int hash, K key, V value,
				com.webmvc.util.map.HashMap.Node<K, V> next) {
			super(hash, key, value, next);
		}
		
	}
}
