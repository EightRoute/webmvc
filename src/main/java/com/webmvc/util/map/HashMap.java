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
	
	//当桶中元素个数超过这个值时，需要使用红黑树节点替换链表节点
	//这个值必须为 8，要不然频繁转换效率也不高
	static final int TREEIFY_THRESHOLD = 8;
	
	//一个树的链表还原阈值
	//当扩容时，桶中元素个数小于这个值，就会把树形的桶元素 还原（切分）为链表结构
	//这个值应该比上面那个小，至少为 6，避免频繁转换
	static final int UNTREEIFY_THRESHOLD = 6;
	
	//哈希表的最小树形化容量
	//当哈希表中的容量大于这个值时，表中的桶才能进行树形化
	//否则桶内元素太多时会扩容，而不是树形化
	//为了避免进行扩容、树形化选择的冲突，这个值不能小于 4 * TREEIFY_THRESHOLD
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
		//移位为了分布的更均匀
		//需要重写hashcode原因
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
	
	/**
	 * 初始分配或者容量翻倍
	 * @return 新的table
	 */
	final Node<K, V>[] resize() {
		Node<K, V>[] oldTab = table;
		//老的容量
		int oldCap = (oldTab == null) ? 0 : oldTab.length; 
		//老阈值
		int oldThr = threshold;
		int newCap, newThr = 0;
		if (oldCap > 0) {
			//如果已经达到最大容量，则不扩容
			if (oldCap >= MAXIMUM_CAPACITY) {
				threshold = Integer.MAX_VALUE;
				return oldTab;
			} else if ((newCap = oldCap >> 1) < MAXIMUM_CAPACITY &&
					oldCap >= DEFAULT_INITIAL_CAPACITY) {
				//容量翻倍 ↑, 阈值翻倍↓
				newThr = oldThr << 1;
			}
		} else if (oldThr > 0) {
			newCap = oldThr;
		} else {
			//初始分配
			newCap = DEFAULT_INITIAL_CAPACITY;
			//阈值 = 容量 * 负载因子
			newThr = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
		}
		
		if (newThr == 0) {
			float ft = (float)newCap * loadFactor;
			newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ? 
					(int)ft : Integer.MAX_VALUE);
		}
		threshold = newThr;
		
		@SuppressWarnings("unchecked")
		//创建新的表
		Node<K, V>[] newTab = (Node<K,V>[])new Node[newCap]; 
		table = newTab;
		if (oldTab != null) {
			for (int j = 0; j < oldCap; ++j) {
				//遍历老的表,将元素放到新的表中
				Node<K, V> e;
				if ((e = oldTab[j]) != null) {
					oldTab[j] = null; //gc
					if (e.next == null) {
						newTab[e.hash & (newCap - 1)] = e;
					} else if (e instanceof TreeNode) {
						//TODO
					} else {
						Node<K, V> loHead = null, loTail = null;
						Node<K, V> hiHead = null, hiTail = null;
						Node<K, V> next;
						do {
							next = e.next;
							if ((e.hash & oldCap) == 0) {
								if (loTail == null) {
									loHead = e;
								} else {
									loTail.next = e;
								}
								loTail = e;
							} else {
								if (hiTail == null) {
									hiHead = e;
								} else {
									hiTail.next = e;
								}
								hiTail = e;
							}
						} while ((e = next) != null);
						//TODO
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param hash 用来确定数组下标
	 * @param key 键
	 * @param value 值
	 * @param onlyIfAbsent 如果存在值则不改变
	 * @param evict
	 * @return 原来的值或null
	 */
	final V putVal(int hash, K key, V value,
			boolean onlyIfAbsent, boolean evict) {
		Node<K, V>[] tab;
		Node<K, V> p;
		int n, i;
		/*如果为初始状态*/
		if ((tab = table) == null || (n = tab.length) == 0) {
			n = (tab = resize()).length;
		}
		/*如果链表为空,元素将插入到数组的(n - 1) & hash的位置,*/
		if ((p = tab[i = (n - 1) & hash]) == null) {
			tab[i] = newNode(hash, key, value, null);
		} else {
			Node<K, V> e; K k;
			//如果和第一个元素的key就相等
			if (p.hash == hash 
				&& ((k = p.key) == key || (key != null && key.equals(k)))) {
				e = p;
			} else if (p instanceof TreeNode) {
				//TODO
				e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
			} else {
				/*遍历链表,如果没有找到相同的key则在最后增加一个Node*/
				for (int binCount = 0; ;++binCount) {
					if ((e = p.next) == null) {
						p.next = newNode(hash, key, value, null);
						//遍历table[i]，判断链表长度是否大于TREEIFY_THRESHOLD(默认值为8)，大于8的话把链表转换为红黑树，在红黑树中执行插入操作，否则进行链表的插入操作
						if (binCount >= TREEIFY_THRESHOLD - 1) {
							treeifyBin(tab, hash);
							break;
						}
					}
					//存在相同的key时
					if (e.hash == hash 
							&& ((k = e.key) == key || (key != null && key.equals(k)))) {
						break;
					}
					p = e;
				}		
			}
			if (e != null) {
				V oldValue = e.value;
				//是否覆盖默认值
				if (!onlyIfAbsent || oldValue == null) {
					e.value = value;
				}
				afterNodeAccess(e);
				return oldValue;
			}
		}
		++modCount;
		if (++size > threshold) {
			resize();
		}
		afterNodeInsertion(evict);
		return null;
	}
	
	void afterNodeAccess(Node<K, V> p) {}
	void afterNodeInsertion(boolean evict) { }
	
	
	Node<K, V> newNode(int hash, K key, V value, Node<K, V> next) {
		return new Node<>(hash, key, value, next);
	}
	
	/*将桶内所有的 链表节点 替换成 红黑树节点*/
	final void treeifyBin(Node<K, V>[] tab, int hash) {
		int n, index;
		Node<K, V> e;
		//如果桶的长度小于MIN_TREEIFY_CAPACITY则扩容，而不进行树形化
		if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY) {
			resize();
		} else if ((e = tab[index = (n - 1) & hash]) != null) {
			//红黑树头尾节点
			TreeNode<K, V> hd = null, tl = null;
			do {
				//新建一个树节点,内容和链表节点e相同
				TreeNode<K, V> p = replacementTreeNode(e, null);
				if (tl == null) {
					hd = p;
				} else {
					p.prev = tl;
					tl.next = p;
				}
			} while ((e = e.next) != null);
			//让桶的第一个元素指向新建的红黑树头结点，以后这个桶里的元素就是红黑树而不是链表
			if ((tab[index] = hd) != null) {
				hd.treeify(tab);
			}
		}
	}
	
	/**
	 * 将链表节点转换为红黑树节点 
	 */
	TreeNode<K, V> replacementTreeNode(Node<K, V> p, Node<K, V> next) {
		return new TreeNode<K, V>(p.hash, p.key, p.value, next);
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
	
	static final class TreeNode<K, V> extends LinkedHashMap.Entry<K, V> {
		TreeNode<K, V> parent;
		TreeNode<K, V> left;
		TreeNode<K, V> right;
		TreeNode<K, V> prev;
		boolean red;
		
		TreeNode(int hash, K key, V value, Node<K, V> next) {
			super(hash, key, value, next);
		}
		
		
		
		final TreeNode<K,V> putTreeVal(HashMap<K,V> map, Node<K,V>[] tab,
	            int h, K k, V v) { 
			return null;
		}
		
		final void treeify(Node<K, V>[] tab) {
			
		}
		
	}
	

	

}
