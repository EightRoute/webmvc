package com.webmvc.util.list;

import java.io.Serializable;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * @author sgz
 * @date   2018年2月12日 下午1:13:40
 */
public class LinkedList<E> implements List<E>, Deque<E>, Serializable {

	private static final long serialVersionUID = 1057766311597491141L;

	transient int size = 0;
	
	transient int modCount = 0;
	
	transient Node<E> first;
	
	transient Node<E> last;
	
	public LinkedList() {
		
	}
	
	public LinkedList(Collection<? extends E> c) {
		this();
		addAll(c);
	}
	
	
	/*LinkedList是由一个一个Node连接起来的链表*/
	private static class Node<E> {
		E item; //当前的Node的element
		Node<E> prev; //上一个Node的引用
		Node<E> next; //下一个Node的引用
		
		Node(Node<E> prev, E element, Node<E> next) {
			this.item = element;
			this.next = next;
			this.prev = prev;
		}
	}
	
	/*将e作为首节点*/
	private void linkFirst(E e) {
		final Node<E> f = first;
		final Node<E> newFirst = new Node<>(null, e, f);
		first = newFirst;
		if (f == null) {
			last = newFirst;
		} else {
			f.prev = newFirst;
		}
		modCount++;
		size++;
	}
	
	/*将e作为末节点*/
	private void linkLasted(E e) {
		final Node<E> l = last;
		final Node<E> newLast = new Node<>(l, e, null);
		last = newLast;
		if (l == null) {
			first = newLast;
		} else {
			l.next = newLast;
		}
		modCount++;
		size++;
	}
	
	/*将e插入到succ前面,且succ不为null*/
	private void linkBefore(E e, Node<E> succ) {
		final Node<E> prev = succ.prev;//获取succ的前一个Node
		final Node<E> newNode = new Node<>(prev, e, succ);
		succ.prev = newNode;
		if (prev == null) {
			first = prev;
		} else {
			prev.next = newNode;
		}
		modCount++;
		size++;
	}
	
	
	/*去掉首节点f,且假设f不为空*/
	private E unlinkFirst(Node<E> f) {
		final E element = f.item;
		final Node<E> next = f.next;
		f.item = null;
		f.next = null; //有利于gc
		if (next == null) {
			last = null;
		} else {
			next.prev = null;
		}		
		size--;
		modCount++;
		return element;
	}
	
	/*去掉末节点l,且假设l不为空*/
	private E unlinkLast(Node<E> l) {
		final Node<E> prev = l.prev;
		final E element = l.item;
		l.prev = null;
		l.item = null;
		if (prev == null) {
			first = null;
		} else {
			prev.next = null;
		}
		size--;
		modCount++;
		return element;
	}
	
	
	/*去掉节点x,且假设x不为空*/
	private E unlink(Node<E> x) {
		final E element = x.item;
		final Node<E> prev = x.prev;
		final Node<E> next = x.next;
		
		if (prev == null) {
			first = next;
		} else {
			prev.next = next; //断开中间的连接
			x.prev = null; //gc
		}
		
		if (next == null) {
			last = prev;
		} else {
			next.prev = prev;
			x.next = null; //gc
		}
		x.item = null; //gc
		size--;
		modCount++;
		return element;
	}
	
	
	/**
	 * 有多少个元素
	 */
	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 是否包含对象o
	 */
	@Override
	public boolean contains(Object o) {
		return indexOf(o) != -1;
	}

	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 在末尾添加一个element
	 */
	@Override
	public boolean add(E e) {
		linkLasted(e);
		return true;
	}

	/**
	 * 删除首次出现且为参数o的element
	 */
	@Override
	public boolean remove(Object o) {
		if (o == null) {
			for (Node<E> x = first; x != null; x = x.next) {
				if (x.item == null) {
					unlink(x);
					return true;
				}
			}
		} else {
			for (Node<E> x = first; x != null; x = x.next) {
				if (x.item.equals(o)) {
					unlink(x);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * List中是否包含一个集合
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	
	/**
	 * 在List后面添加一个集合
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		return addAll(size, c);
	}

	
	/**
	 * 在指定的地方加入一个新的集合
	 * @param index 插入的地址
	 * @param c 要被插入的集合
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		checkPositionIndex(index);
		
		Object[] a = c.toArray();
		int numNew = a.length;
		if (numNew == 0) {
			return false; //需要添加的集合为空
		}
		
		/*新链表和老链表要对接的两个Node*/
		Node<E> pred, succ;
		if (index == size) {
			succ = null;
			pred = last;
		} else {
			succ = node(index);
			pred = succ.prev;
		}
		/*创建新链表*/
		for (Object o : c) {
			E e = (E) o;
			Node<E> newNode = new Node<>(pred, e, null);
			if (pred == null) {
				first = newNode;
			} else {
				pred.next = newNode;
			}
			pred = newNode;
		}
		
		if (succ == null) {
			last = pred;
		} else {
			/*拼接两个链表*/
			succ.prev = last;
			pred.next = succ;
		}
		
		size += numNew;
		modCount++;
		return true;
	}
	
	/*根据位置寻找Node*/
	private Node<E> node(int index) {
		/*链表不支持随机取值,所以需要找一个近的头开始遍历*/
		if (index < (size >> 1)) {
			Node<E> x = first;
			for (int i = 0; i < index; i++) {
				x = x.next;
			}
			return x;
		} else {
			Node<E> x = last;
			for (int i = size - 1; i > index; i--) {
				x = x.prev;
			}
			return x;
		}
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	private void checkPositionIndex(int index) {
        if (!isPositionIndex(index)) {
            throw new IndexOutOfBoundsException("坐标超出范围");
        }
    }
	
	private boolean isPositionIndex(int index) {
        return index >= 0 && index <= size;
    }
	
	
	/**
	 * 清空List
	 */
	@Override
	public void clear() {
		/*为了gc*/
		for (Node<E> x = first; x != null; ) {
			Node<E> next = x.next;
			x.item = null;
			x.prev = null;
			x.next = null;
			x = next;
		}
		first = last = null;
		size = 0; 
		modCount++;
	}

	
	/**
	 * 根据位置获取元素
	 */
	@Override
	public E get(int index) {
		checkPositionIndex(index);
		return node(index).item;
	}

	
	/**
	 * 更具位置修改元素
	 */
	@Override
	public E set(int index, E element) {
		checkPositionIndex(index);
		Node<E> node = node(index);
		E oldValue = node.item;
		node.item = element;
		return oldValue;
	}

	/**
	 * 将元素放到指定位置
	 */
	@Override
	public void add(int index, E element) {
		checkPositionIndex(index);
		if (index == size) {
			linkLasted(element);
		} else {
			/*linkBefore不能插入到最后*/
			linkBefore(element, node(index));
		}
		
	}

	/**
	 * 删除指定位置的元素
	 */
	@Override
	public E remove(int index) {
		checkPositionIndex(index);
		return unlink(node(index));
	}

	/**
	 * 返回链表中第一个出现o的位置,如不存在则为-1
	 */
	@Override
	public int indexOf(Object o) {
		int index = 0;
		if (o == null) {
			for (Node<E> x = first; x != null; x = x.next) {
				if (x.item == null) {
					return index;
				}
				index++;
			}
		} else {
			for (Node<E> x = first; x != null; x = x.next) {
				if (o.equals(x.item)) {
					return index;
				}
				index++;
			}
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<E> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 添加一个element在链表头
	 */
	@Override
	public void addFirst(E e) {
		linkFirst(e);
	}

	/**
	 * 添加一个element在链表尾
	 */
	@Override
	public void addLast(E e) {
		linkLasted(e);
	}

	@Override
	public boolean offerFirst(E e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean offerLast(E e) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 删除第一个element
	 */
	@Override
	public E removeFirst() {
		final Node<E> f = first;
		if (f == null){
			 throw new NoSuchElementException("没有这个element");
		}
		return unlinkFirst(f);
	}

	
	/**
	 * 删除最后一个element
	 */
	@Override
	public E removeLast() {
		final Node<E> l =last;
		if (l == null) {
			throw new NoSuchElementException("没有这个element");
		}
		return unlinkLast(l);
	}

	@Override
	public E pollFirst() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E pollLast() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 获取第一个element
	 */
	@Override
	public E getFirst() {
		final Node<E> f = first;
		if (f == null){
			 throw new NoSuchElementException("没有这个element");
		}
		return f.item;
	}

	/**
	 * 获取最后一个element
	 */
	@Override
	public E getLast() {
		final Node<E> l = last;
		if (l == null) {
			throw new NoSuchElementException("没有这个element");
		}
		return l.item;
	}

	@Override
	public E peekFirst() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E peekLast() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean offer(E e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E remove() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E poll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E element() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E peek() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void push(E e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public E pop() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<E> descendingIterator() {
		// TODO Auto-generated method stub
		return null;
	}

}
