package com.webmvc.util.map;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;




public class HashMap<K, V> extends AbstractMap<K, V> 
	implements Map<K, V>, Serializable{
	
	private static final long serialVersionUID = 2772224917432192756L;

	/*初始化容量,1 << 4,必须为2的倍数
	 * 如果length为2的次幂  则length-1 转化为二进制必定是11111……的形式，
	 * 在于h的二进制与操作效率会非常的快，而且空间不浪费
	 */
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
		// 移位为了分布的更均匀
		//需要重写hashcode原因
		//(h >>> 16)扰动函数 混合原始哈希码的高位和低位，以此来加大低位的随机性
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
						//如果为红黑树
						((TreeNode<K, V>)e).split(this, newTab, j, oldCap);
					} else {
						Node<K, V> loHead = null, loTail = null;
						Node<K, V> hiHead = null, hiTail = null;
						Node<K, V> next;
						do {
							//遍历链表
							next = e.next;
							//当(e.hash & oldCap) == 0 痛的位置不需要改变
							// oldcap 00010000 oldcap-1 00001111  newcap-1 00011111  
							// hash   11101111          11101111           11101111
							// 假设e.hash = 32 oldcap = 15 -> 32/15 = 2 32/16 = 0 32/30 = 2
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
						if (loTail != null) {
							loTail.next = null;
							newTab[j] = loHead;
						}
						if (hiTail != null) {
							hiTail.next = null;
							newTab[j + oldCap] = hiHead;
						}
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
		// i = (n - 1) & hash  简化了以前的indexFor方法
		//为什么长度要2次幂的原因 2次幂时 length - 1的二进制为1111 &运算时不会浪费数组空间
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
	
	/*当有一条链条的长度大于TREEIFY_THRESHOLD 
	 * 且桶的长度大于MIN_TREEIFY_CAPACITY时
	 * 将桶内链表节点 替换成 红黑树节点*/
	final void treeifyBin(Node<K, V>[] tab, int hash) {
		int n, index;
		Node<K, V> e;
		//如果桶的长度小于MIN_TREEIFY_CAPACITY则扩容，而不进行树形化
		if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY) {
			resize();
		} else if ((e = tab[index = (n - 1) & hash]) != null) {
			//红黑树链表头尾节点
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
				//将红黑树链表转化为红黑树
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
	
	static Class<?> comparableClassFor(Object x) {
		return null;
	}
	
	static int compareComparables(Class<?> kc, Object k, Object x) {
		return 0;
	}
	
	Node<K,V> replacementNode(Node<K,V> p, Node<K,V> next) {
        return new Node<>(p.hash, p.key, p.value, next);
    }
	
	TreeNode<K,V> newTreeNode(int hash, K key, V value, Node<K,V> next) {
        return new TreeNode<>(hash, key, value, next);
    }
	
	/*
	 * 红黑树特性
	 * 1根节点为黑色
	 * 2节点要么是红色 要么是黑色
	 * 3对于每个节点，从该点至null（树尾端）的任何路径，都含有相同个数的黑色节点
	 * 4红色节点不能连续
	 * 5新增的节点都是红色
	 */
	static final class TreeNode<K, V> extends LinkedHashMap.Entry<K, V> {
		TreeNode<K, V> parent;
		TreeNode<K, V> left;
		TreeNode<K, V> right;
		TreeNode<K, V> prev;
		boolean red;
		
		TreeNode(int hash, K key, V value, Node<K, V> next) {
			super(hash, key, value, next);
		}
		
		//返回树的根节点
		final TreeNode<K, V> root() {
			//是否存在父节点
			for (TreeNode<K, V> r = this,p ;;) {
				if ((p = r.parent) == null) {
					return r;
				}
				r = p;
			}
		}
		
		//将树的根节点放进桶中
		static <K, V> void moveRootToFront(Node<K, V>[] tab, TreeNode<K, V> root){
			int n;
			if (root != null && tab != null && (n = tab.length) > 0) {
				int index = (n - 1) & root.hash;
				TreeNode<K, V> first = (TreeNode<K, V>) tab[index];
				if (first != root) {
					Node<K, V> rn;
					tab[index] = root;
					TreeNode<K, V> rp = root.prev;
					if ((rn = root.next) != null) {
						((TreeNode<K, V>)rn).prev = rp;
					}
					if (rp != null) {
						rp.next = rn;
					}
					if (first != null) {
						first.prev = root;
					}
					root.next = first;
					root.prev = null;
				}
				//如果要开启断言检查，则需要用开关-enableassertions或-ea来开启
				assert checkInvariants(root);
			}
		}
		
		final TreeNode<K, V> find(int h, Object k, Class<?> kc) {
			TreeNode<K, V> p = this;
			do {
				int ph, dir;
				K pk;
				TreeNode<K, V> pl = p.left, pr = p.right, q;
				if ((ph = p.hash) > h) {
					p = pl;
				} else if (ph < h) {
					p = pr;
				} else if ((pk = p.key) == k || (k != null && k.equals(pk))) {
					return p;
				} else if (pl == null) {
					p = pr;
				} else if (pr == null) {
					p = pl;
				} else if ((kc != null ||
                          (kc = comparableClassFor(k)) != null) &&
                         (dir = compareComparables(kc, k, pk)) != 0) {
					p = (dir < 0) ? pl : pr;
				} else if ((q = pr.find(h, k, kc)) != null) {
					return q;
				} else {
					p = pl;
				}
				
			} while (p != null);
			return null;
		}
		
		final TreeNode<K, V> getTreeNode(int h, Object k) {
			return ((parent != null) ? root() : this).find(h, k, null);
		}
		
		static int tieBreakOrder(Object a, Object b) {
			int d;
            if (a == null || b == null ||
                (d = a.getClass().getName().
                 compareTo(b.getClass().getName())) == 0)
                d = (System.identityHashCode(a) <= System.identityHashCode(b) ?
                     -1 : 1);
            return d;
		}
 		
		
		final TreeNode<K,V> putTreeVal(HashMap<K,V> map, Node<K,V>[] tab,
	            int h, K k, V v) { 
			Class<?> kc = null;
            boolean searched = false;
            TreeNode<K,V> root = (parent != null) ? root() : this;
            for (TreeNode<K,V> p = root;;) {
                int dir, ph; K pk;
                if ((ph = p.hash) > h)
                    dir = -1;
                else if (ph < h)
                    dir = 1;
                else if ((pk = p.key) == k || (k != null && k.equals(pk)))
                    return p;
                else if ((kc == null &&
                          (kc = comparableClassFor(k)) == null) ||
                         (dir = compareComparables(kc, k, pk)) == 0) {
                    if (!searched) {
                        TreeNode<K,V> q, ch;
                        searched = true;
                        if (((ch = p.left) != null &&
                             (q = ch.find(h, k, kc)) != null) ||
                            ((ch = p.right) != null &&
                             (q = ch.find(h, k, kc)) != null))
                            return q;
                    }
                    dir = tieBreakOrder(k, pk);
                }

                TreeNode<K,V> xp = p;
                if ((p = (dir <= 0) ? p.left : p.right) == null) {
                    Node<K,V> xpn = xp.next;
                    TreeNode<K,V> x = map.newTreeNode(h, k, v, xpn);
                    if (dir <= 0)
                        xp.left = x;
                    else
                        xp.right = x;
                    xp.next = x;
                    x.parent = x.prev = xp;
                    if (xpn != null)
                        ((TreeNode<K,V>)xpn).prev = x;
                    moveRootToFront(tab, balanceInsertion(root, x));
                    return null;
                }
            }
		}
		
		//将红黑树节点链表转换成红黑树
		final void treeify(Node<K, V>[] tab) {
			TreeNode<K,V> root = null;
			//遍历链表
            for (TreeNode<K,V> x = this, next; x != null; x = next) {
                next = (TreeNode<K,V>)x.next;
                x.left = x.right = null;
                //将头结点设为红黑树的根节点
                if (root == null) {
                    x.parent = null;
                    x.red = false;
                    root = x;
                } else {
                    K k = x.key;
                    int h = x.hash;
                    Class<?> kc = null;
                    //判断是左孩子还是右孩子
                    for (TreeNode<K,V> p = root;;) {
                        int dir, ph;
                        K pk = p.key;
                        if ((ph = p.hash) > h) {
                            dir = -1;
                        } else if (ph < h) {
                            dir = 1;
                        } else if ((kc == null &&
                                  (kc = comparableClassFor(k)) == null) ||
                                 (dir = compareComparables(kc, k, pk)) == 0) {
                            dir = tieBreakOrder(k, pk);
                        }
                        TreeNode<K,V> xp = p;
                        if ((p = (dir <= 0) ? p.left : p.right) == null) {
                            x.parent = xp;
                            if (dir <= 0) {
                                xp.left = x;
                            } else {
                                xp.right = x;
                            }
                            //恢复红黑树特性
                            root = balanceInsertion(root, x);
                            break;
                        }
                    }
                }
            }
            moveRootToFront(tab, root);
		}
		
		final Node<K, V> untreeify(HashMap<K, V> map) {
			Node<K,V> hd = null, tl = null;
            for (Node<K,V> q = this; q != null; q = q.next) {
                Node<K,V> p = map.replacementNode(q, null);
                if (tl == null)
                    hd = p;
                else
                    tl.next = p;
                tl = p;
            }
            return hd;
		}
		
		final void removeTreeNode(HashMap<K, V> map, Node<K, V>[] tab, boolean movable) {
			int n;
            if (tab == null || (n = tab.length) == 0)
                return;
            int index = (n - 1) & hash;
            TreeNode<K,V> first = (TreeNode<K,V>)tab[index], root = first, rl;
            TreeNode<K,V> succ = (TreeNode<K,V>)next, pred = prev;
            if (pred == null)
                tab[index] = first = succ;
            else
                pred.next = succ;
            if (succ != null)
                succ.prev = pred;
            if (first == null)
                return;
            if (root.parent != null)
                root = root.root();
            if (root == null || root.right == null ||
                (rl = root.left) == null || rl.left == null) {
                tab[index] = first.untreeify(map);  // too small
                return;
            }
            TreeNode<K,V> p = this, pl = left, pr = right, replacement;
            if (pl != null && pr != null) {
                TreeNode<K,V> s = pr, sl;
                while ((sl = s.left) != null) // find successor
                    s = sl;
                boolean c = s.red; s.red = p.red; p.red = c; // swap colors
                TreeNode<K,V> sr = s.right;
                TreeNode<K,V> pp = p.parent;
                if (s == pr) { // p was s's direct parent
                    p.parent = s;
                    s.right = p;
                }
                else {
                    TreeNode<K,V> sp = s.parent;
                    if ((p.parent = sp) != null) {
                        if (s == sp.left)
                            sp.left = p;
                        else
                            sp.right = p;
                    }
                    if ((s.right = pr) != null)
                        pr.parent = s;
                }
                p.left = null;
                if ((p.right = sr) != null)
                    sr.parent = p;
                if ((s.left = pl) != null)
                    pl.parent = s;
                if ((s.parent = pp) == null)
                    root = s;
                else if (p == pp.left)
                    pp.left = s;
                else
                    pp.right = s;
                if (sr != null)
                    replacement = sr;
                else
                    replacement = p;
            }
            else if (pl != null)
                replacement = pl;
            else if (pr != null)
                replacement = pr;
            else
                replacement = p;
            if (replacement != p) {
                TreeNode<K,V> pp = replacement.parent = p.parent;
                if (pp == null)
                    root = replacement;
                else if (p == pp.left)
                    pp.left = replacement;
                else
                    pp.right = replacement;
                p.left = p.right = p.parent = null;
            }

            TreeNode<K,V> r = p.red ? root : balanceDeletion(root, replacement);

            if (replacement == p) {  // detach
                TreeNode<K,V> pp = p.parent;
                p.parent = null;
                if (pp != null) {
                    if (p == pp.left)
                        pp.left = null;
                    else if (p == pp.right)
                        pp.right = null;
                }
            }
            if (movable)
                moveRootToFront(tab, r);
		}
		
		final void split(HashMap<K, V> map, Node<K, V>[] tab, int index, int bit) {
			TreeNode<K,V> b = this;
            // Relink into lo and hi lists, preserving order
            TreeNode<K,V> loHead = null, loTail = null;
            TreeNode<K,V> hiHead = null, hiTail = null;
            int lc = 0, hc = 0;
            for (TreeNode<K,V> e = b, next; e != null; e = next) {
                next = (TreeNode<K,V>)e.next;
                e.next = null;
                if ((e.hash & bit) == 0) {
                    if ((e.prev = loTail) == null)
                        loHead = e;
                    else
                        loTail.next = e;
                    loTail = e;
                    ++lc;
                }
                else {
                    if ((e.prev = hiTail) == null)
                        hiHead = e;
                    else
                        hiTail.next = e;
                    hiTail = e;
                    ++hc;
                }
            }

            if (loHead != null) {
                if (lc <= UNTREEIFY_THRESHOLD)
                    tab[index] = loHead.untreeify(map);
                else {
                    tab[index] = loHead;
                    if (hiHead != null) // (else is already treeified)
                        loHead.treeify(tab);
                }
            }
            if (hiHead != null) {
                if (hc <= UNTREEIFY_THRESHOLD)
                    tab[index + bit] = hiHead.untreeify(map);
                else {
                    tab[index + bit] = hiHead;
                    if (loHead != null)
                        hiHead.treeify(tab);
                }
            }
		}
		
		static <K, V> TreeNode<K, V> rotateLeft(TreeNode<K, V> root, TreeNode<K, V> p) {
			TreeNode<K,V> r, pp, rl;
            if (p != null && (r = p.right) != null) {
                if ((rl = p.right = r.left) != null)
                    rl.parent = p;
                if ((pp = r.parent = p.parent) == null)
                    (root = r).red = false;
                else if (pp.left == p)
                    pp.left = r;
                else
                    pp.right = r;
                r.left = p;
                p.parent = r;
            }
            return root;
		}
		
		static <K, V> TreeNode<K, V> rotateRight(TreeNode<K, V> root, TreeNode<K, V> p) {
			TreeNode<K,V> l, pp, lr;
            if (p != null && (l = p.left) != null) {
                if ((lr = p.left = l.right) != null)
                    lr.parent = p;
                if ((pp = l.parent = p.parent) == null)
                    (root = l).red = false;
                else if (pp.right == p)
                    pp.right = l;
                else
                    pp.left = l;
                l.right = p;
                p.parent = l;
            }
            return root;
		}
		
		static <K, V> TreeNode<K, V> balanceInsertion(TreeNode<K, V> root, TreeNode<K, V> x) {
			 x.red = true;
	            for (TreeNode<K,V> xp, xpp, xppl, xppr;;) {
	                if ((xp = x.parent) == null) {
	                    x.red = false;
	                    return x;
	                }
	                else if (!xp.red || (xpp = xp.parent) == null)
	                    return root;
	                if (xp == (xppl = xpp.left)) {
	                    if ((xppr = xpp.right) != null && xppr.red) {
	                        xppr.red = false;
	                        xp.red = false;
	                        xpp.red = true;
	                        x = xpp;
	                    }
	                    else {
	                        if (x == xp.right) {
	                            root = rotateLeft(root, x = xp);
	                            xpp = (xp = x.parent) == null ? null : xp.parent;
	                        }
	                        if (xp != null) {
	                            xp.red = false;
	                            if (xpp != null) {
	                                xpp.red = true;
	                                root = rotateRight(root, xpp);
	                            }
	                        }
	                    }
	                }
	                else {
	                    if (xppl != null && xppl.red) {
	                        xppl.red = false;
	                        xp.red = false;
	                        xpp.red = true;
	                        x = xpp;
	                    }
	                    else {
	                        if (x == xp.left) {
	                            root = rotateRight(root, x = xp);
	                            xpp = (xp = x.parent) == null ? null : xp.parent;
	                        }
	                        if (xp != null) {
	                            xp.red = false;
	                            if (xpp != null) {
	                                xpp.red = true;
	                                root = rotateLeft(root, xpp);
	                            }
	                        }
	                    }
	                }
	            }
		}
		
		static <K, V> TreeNode<K, V> balanceDeletion(TreeNode<K, V> root, TreeNode<K, V> x) {
			for (TreeNode<K,V> xp, xpl, xpr;;)  {
                if (x == null || x == root)
                    return root;
                else if ((xp = x.parent) == null) {
                    x.red = false;
                    return x;
                }
                else if (x.red) {
                    x.red = false;
                    return root;
                }
                else if ((xpl = xp.left) == x) {
                    if ((xpr = xp.right) != null && xpr.red) {
                        xpr.red = false;
                        xp.red = true;
                        root = rotateLeft(root, xp);
                        xpr = (xp = x.parent) == null ? null : xp.right;
                    }
                    if (xpr == null)
                        x = xp;
                    else {
                        TreeNode<K,V> sl = xpr.left, sr = xpr.right;
                        if ((sr == null || !sr.red) &&
                            (sl == null || !sl.red)) {
                            xpr.red = true;
                            x = xp;
                        }
                        else {
                            if (sr == null || !sr.red) {
                                if (sl != null)
                                    sl.red = false;
                                xpr.red = true;
                                root = rotateRight(root, xpr);
                                xpr = (xp = x.parent) == null ?
                                    null : xp.right;
                            }
                            if (xpr != null) {
                                xpr.red = (xp == null) ? false : xp.red;
                                if ((sr = xpr.right) != null)
                                    sr.red = false;
                            }
                            if (xp != null) {
                                xp.red = false;
                                root = rotateLeft(root, xp);
                            }
                            x = root;
                        }
                    }
                }
                else { // symmetric
                    if (xpl != null && xpl.red) {
                        xpl.red = false;
                        xp.red = true;
                        root = rotateRight(root, xp);
                        xpl = (xp = x.parent) == null ? null : xp.left;
                    }
                    if (xpl == null)
                        x = xp;
                    else {
                        TreeNode<K,V> sl = xpl.left, sr = xpl.right;
                        if ((sl == null || !sl.red) &&
                            (sr == null || !sr.red)) {
                            xpl.red = true;
                            x = xp;
                        }
                        else {
                            if (sl == null || !sl.red) {
                                if (sr != null)
                                    sr.red = false;
                                xpl.red = true;
                                root = rotateLeft(root, xpl);
                                xpl = (xp = x.parent) == null ?
                                    null : xp.left;
                            }
                            if (xpl != null) {
                                xpl.red = (xp == null) ? false : xp.red;
                                if ((sl = xpl.left) != null)
                                    sl.red = false;
                            }
                            if (xp != null) {
                                xp.red = false;
                                root = rotateRight(root, xp);
                            }
                            x = root;
                        }
                    }
                }
            }
		}
		
		static <K, V> boolean checkInvariants(TreeNode<K, V> t) {
			TreeNode<K,V> tp = t.parent, tl = t.left, tr = t.right,
	                tb = t.prev, tn = (TreeNode<K,V>)t.next;
	            if (tb != null && tb.next != t)
	                return false;
	            if (tn != null && tn.prev != t)
	                return false;
	            if (tp != null && t != tp.left && t != tp.right)
	                return false;
	            if (tl != null && (tl.parent != t || tl.hash > t.hash))
	                return false;
	            if (tr != null && (tr.parent != t || tr.hash < t.hash))
	                return false;
	            if (t.red && tl != null && tl.red && tr != null && tr.red)
	                return false;
	            if (tl != null && !checkInvariants(tl))
	                return false;
	            if (tr != null && !checkInvariants(tr))
	                return false;
	            return true;
	        }
		}
				
	
	

	

}
