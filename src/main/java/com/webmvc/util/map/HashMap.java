package com.webmvc.util.map;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class HashMap<K, V> extends AbstractMap<K, V> 
	implements Map<K, V>, Serializable{
	
	private static final long serialVersionUID = 2772224917432192756L;

	/*初始化容量,1 << 4,必须为2的倍数*/
	static final int DEFAULT_INITIAL_CAPACITY = 16;
	/*最大容量, 1 << 30*/
	static final int MAXIMUM_CAPACITY = 1073741824;
	/*默认扩容参数,代表了table的填充度有多少*/
	static final float DEFAULT_LOAD_FACTOR = 0.75F;
	
	static final int TREEIFY_THRESHOLD = 8;
	
	static final int UNTREEIFY_THRESHOLD = 6;
	
	static final int MIN_TREEIFY_CAPACITY = 64;
	
	static class Node<K, V> implements Map.Entry<K, V> {
		final int hash;
		final K key;
		V value;
		Node<K, V> next;
		
		Node(int hash, K key, V value, Node<K,V> next) {
			this.hash = hash;
			this.key = key;
			this.value = value;
			this.next = next;
		}
		
		@Override
		public final K getKey() {
			return key;
		}

		@Override
		public final V getValue() {
			return value;
		}
		
		@Override
		public final String toString() {
			return key + " = " + value;
		}
		
		@Override
		public final int hashCode() {
			/* ^运算  01 -> 1, 00 -> 0, 11 -> 0 */
			return Objects.hashCode(key) ^ Objects.hashCode(value);
		}

		@Override
		public final V setValue(V newValue) {
			V oldValue = value;
			value = newValue;
			return oldValue;
		}
		
		@Override
		public final boolean equals(Object o) {
			if (o == this)
                return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                if (Objects.equals(key, e.getKey()) &&
                    Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
		}
		
	}
	
	static final int hash(Object key) {
		int h;
		/*无符号的右移>>>,按照二进制把数字右移指定数位，高位直接补零，低位移除*/
		return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
	}
	
	static final int tableSizeFor (int cap) {
		int n = cap - 1;
		n |= n >>> 1;
		n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
	}
	
	transient Node<K,V>[] table;
	
	transient Set<Map.Entry<K, V>> entrySet;
	
	transient int size;
	
	transient int modCount;
	/*阈值，等于加载因子*容量，当实际大小超过阈值则进行扩容*/
	int threshold;
	/*扩容参数*/
	final float loadFactor;
	
	public HashMap(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException("初始化容量不能小于0,当前容量为:" + initialCapacity);
		}
		
		if (initialCapacity > MAXIMUM_CAPACITY) {
			initialCapacity = MAXIMUM_CAPACITY;
		}
		
		if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
			throw new IllegalArgumentException("扩容参数错误,当前为:" + loadFactor);
		}
		
		this.loadFactor = loadFactor;
		this.threshold = tableSizeFor(initialCapacity);
	}
	
	public HashMap(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}
	
	public HashMap() {
		this.loadFactor = DEFAULT_LOAD_FACTOR;
	}
	
	public HashMap(Map<? extends K, ? extends V> m) {
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		putMapEntries(m, false);
	}
	
	final void putMapEntries(Map<? extends K, ? extends V> m, boolean evict) {
		int s = m.size();
		if (s > 0) {
			if (table == null) {
				float ft = ((float)s / loadFactor) + 1.0F;
				int t = ((ft < (float)MAXIMUM_CAPACITY)) ? (int)ft : MAXIMUM_CAPACITY;
				if (t > threshold) {
					threshold = tableSizeFor(t);
				}
			} else if (s > threshold) {
				//大于阈值 扩容
				resize();
			}
			for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
				K key = e.getKey();
				V value = e.getValue();
				putVal(hash(key), key, value, false, evict);
			}
		}
	}
	
	final Node<K, V>[] resize() {
		return null;
	}
	
	final V putVal(int hash, K key, V value,
			boolean onlyIfAbsent, boolean evict) {
		return null;
	}
	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public V get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V put(K key, V value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<K> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<V> values() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}


}
