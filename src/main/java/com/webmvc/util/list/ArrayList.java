package com.webmvc.util.list;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * 用来学习java.util.ArrayList<E>
 * Created by sgz
 * 2018/2/10 19:02
 */
public class  ArrayList<E> implements List<E>, Serializable{

    private static final long serialVersionUID = -8938796252048773961L;

    /*
     * 初始化时数组中默认元素的数量
     */
    private static final int DEFAULT_CAPACITY = 10;
    
    protected transient int modCount = 0;

    private static final Object[] EMPTY_ELEMENTDATA = {};
    private static final Object[] DEFAULTCAPACITY_EMPTY_CAPACITY = {};

    /*
     * 用来存放集合元素的数组
     */
    Object[] elementData;

    /*
     *  集合中元素的数量
     */
    private int size;
    
    /*
     * 如果太大有的虚拟机可能会抛出OutOfMemoryError
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * 带有默认初始数量的构造器
     * 初始化数量必须不小于0
     * @param initialCapacity
     */
    public ArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("初始化数量必须不小于0,当前值为: " + initialCapacity);
        }
    }

    /**
     * 不带初始化数量,数量将为默认的10
     */
    public ArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_CAPACITY;
    }

    /**
     * 使用一个集合初始化,集合中必须为泛型E或E的子类
     * @param c 元素要被复制到List中的集合
     */
    public ArrayList(Collection<? extends E> c) {
        elementData = c.toArray();
        if ((size = elementData.length) != 0) {
        	/*数组能够记住它的元素类型,如果存入一个错误类型会抛出ArrayStoreException*/
        	if (elementData.getClass() != Object[].class) {
        		elementData = Arrays.copyOf(elementData, size, Object[].class);
        	}
        } else {
        	elementData = EMPTY_ELEMENTDATA;
        }
    }


    /**
     * 压缩数组的容量为实际存放元素的数量
     */
    public void trimToSize() {
    	if (size < elementData.length) {
    		elementData = (size == 0) ? EMPTY_ELEMENTDATA : Arrays.copyOf(elementData, size);
    	}
    }
    
    public void ensureCapacity(int minCapacity) {
    	int minExpend = (elementData != DEFAULTCAPACITY_EMPTY_CAPACITY) ? 0 : DEFAULT_CAPACITY;
    	if (minCapacity > minExpend) {
    		ensureExplicitCapacity(minCapacity);
    	}
    }
    
    private void ensureCapacityInternal(int minCapacity) {
    	if (elementData == DEFAULTCAPACITY_EMPTY_CAPACITY) {
    		minCapacity = Math.max(minCapacity, DEFAULT_CAPACITY);
    	}
    	ensureExplicitCapacity(minCapacity);
    }
    
    private void ensureExplicitCapacity(int minCapacity) {
    	modCount++;
    	/*如果数组的容量小于设置的最小的容量,则扩容*/
    	if (minCapacity - elementData.length > 0) {
    		grow(minCapacity);
    	}
    }
    /*
     * 数组扩容
     */
    private void grow(int minCapacity) {
    	int oldCapacity = elementData.length;
    	/*每次大约扩容1/2*/
    	int newCapacity = oldCapacity + (oldCapacity >> 1);
    	if (newCapacity < minCapacity) {
    		newCapacity = minCapacity;
    	}
    	if (newCapacity > MAX_ARRAY_SIZE) {
    		newCapacity = hugeCapacity(minCapacity);
    	}
    	elementData = Arrays.copyOf(elementData, newCapacity);
    }
    
    private static int hugeCapacity(int minCapacity) {
    	if (minCapacity < 0) {
    		/*超出Integer最大的值*/
    		throw new OutOfMemoryError("超出Integer最大的值");
    	}
    	return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }

    /**
     * 获取数组中元素的数量
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * 判断List是否为空
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * 判断List是否包含某个元素
     */
    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

   

    /**
     * 将List转为数组
     */
    @Override
    public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }

    /**
     * 将List转换为T[]的数组
     * 如果传入的数组的尺寸小于List的数量将会新建一个新的数组对象
     * 如果传入的数组的length大于size将不会创建新的数组
     */
    @SuppressWarnings("unchecked")
	@Override
    public <T> T[] toArray(T[] a) {
    	/*如果传入的数组的尺寸小于List的数量将会新建一个新的数组对象*/
    	if (a.length < size) {
    		return (T[]) Arrays.copyOf(elementData, size, a.getClass());
    	} 
    	System.arraycopy(elementData, 0, a, 0, size);
    	if (a.length > size) {
    		/*个人感觉只能做个标识作用 ,而且还不保险*/
    		a[size] = null;
    	}
        return a;
    }
    
    
    @SuppressWarnings("unchecked")
	E elementData(int index){
    	return (E) elementData[index];
    }
    
    
    /*
     * 判断数组是否越界
     */
    private void rangeCheck(int index) {
    	if (index > size) {
    		throw new IndexOutOfBoundsException("数组越界,数组最大下标为:" + size + ",当前取值为:" + index);
    	}
    }

    /**
     * 往集合中添加元素
     */
    @Override
    public boolean add(E e) {
    	ensureCapacityInternal(size++);
    	elementData[size++] = e;
        return true;
    }

    /**
     * 删除第一个出现的对象 
     */
    @Override
    public boolean remove(Object o) {
    	if (o == null) {
    		for (int i = 0; i < size; i++) {
    			if (elementData[i] == null) {
    				fastRemove(i);
    				return true;
    			}
    		}
    	} else {
    		for (int i = 0; i < size; i++) {
    			if (elementData[i] == o) {
    				fastRemove(i);
    				return true;
    			}
    		}
    	}
        return false;
    }
    
    private void fastRemove(int index) {
    	modCount++;
    	int numMoved = size - index -1;
    	if (numMoved > 0) {
    		System.arraycopy(elementData, index + 1, elementData,
    				index, numMoved);
    		elementData[--size] = null;
    	}
    }

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
     * 往集合中添加集合
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
    	Object[] a = c.toArray();
    	int numNew = a.length;
    	ensureCapacityInternal(size + numNew);
    	/*将数组拼接在后面*/
    	System.arraycopy(a, 0, elementData, size, numNew);
    	size += numNew;
        return numNew != 0;
    }

    /**
     * 添加一个集合到List中，并指定开始的下标index
     * @param index 指定开始的下标
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
    	rangeCheckForAdd(index);
    	
    	Object[] a = c.toArray();
    	int numNew = a.length;
    	ensureCapacityInternal(numNew + size);
    	
    	int numMoved = size - index;
    	/*如果在中间需要让出位置*/
    	if (numMoved > 0) {
    		System.arraycopy(elementData, index, elementData,
    				index + numNew, numMoved);
    	}
    	
    	System.arraycopy(a, 0, elementData, index, numNew);
    	size += numNew;
        return numNew != 0;
    }

    /**
     * 删除参数中所有的元素
     */
    @Override
    public boolean removeAll(Collection<?> c) {
    	Objects.requireNonNull(c);
        return batchRemove(c, false);
    }
    
    /**
     * 批量删除
     * @param c 
     * @param complement 如果为true则删除不包含在c中的元素,
     * 如果为false则删除包含在c中的元素
     */
    private boolean batchRemove(Collection<?> c, boolean complement) {
    	final Object[] elementData = this.elementData;
    	int r = 0;
    	int w = 0;
    	boolean modified = false;
    	try {
    		for ( ; r < size; r++) {
    			if (c.contains(elementData[r]) == complement) {
    				elementData[w++] = elementData[r];
    			}
    		}
    	} finally {
    		if (r != size) {
    			System.arraycopy(elementData, r, elementData,
    					w, size - r);
    			w += size - r; 
    		}
    		if (w != size) {
    			for (int i = w; i < size; i++) {
    				elementData[i] = null;
    			}
    			modCount += size - w;
    			size = w;
    			modified = true;
    		}
    	}
    	return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
    	Objects.requireNonNull(c);
        return batchRemove(c, true);
    }

    /**
     * 清空集合
     */
    @Override
    public void clear() {
    	for (int i = 0; i < size; i++) {
    		elementData[i] = null;
    	}
    	size = 0;
    }

    /**
     * 根据下标获取集合中的元素
     */
    @Override
    public E get(int index) {
    	rangeCheck(index);
        return elementData(index);
    }

    /**
     * 根据下标设置集合中的元素,并且返回原值
     */
    @SuppressWarnings("unchecked")
	@Override
    public E set(int index, E element) {
    	rangeCheck(index);
    	E oldValue = (E) elementData[index];
    	elementData[index] = element;
        return oldValue;
    }

    private void rangeCheckForAdd(int index) {
    	if (index > size || index < 0) {
    		throw new IndexOutOfBoundsException("数组下标越界,错误的下标为:" + index + ",最小下标应为0,最大为:" +size);
    	}
    }
    
    /**
     * 将元素添加到一个指定的位置
     */
    @Override
    public void add(int index, E element) {
    	rangeCheckForAdd(index);
    	ensureCapacityInternal(size++);
    	/*将数组index下标后面的元素全部后移一位*/
    	System.arraycopy(elementData, index, elementData, index + 1, size - index);
    	elementData[index] = element;
    	size++;
    }

    /**
     * 根据位置删除元素
     */
    @Override
    public E remove(int index) {
    	rangeCheck(index);
    	modCount++;
    	E oldValue = elementData(index);
    	
    	int numMoved = size - index -1;
    	if (numMoved > 0) {
    		System.arraycopy(elementData, index + 1,
    				elementData, index, numMoved);
    	}
    	/*去掉引用,让jvm可以回收*/
    	elementData[--size] = null;
        return oldValue;
    }

    /**
     * 返回元素首次出现的下标,如果不包含则返回-1
     */
    @Override
    public int indexOf(Object o) {
    	if (o == null) {
    		for (int i = 0; i < size; i++) {
    			if (elementData[i] == null) {
    				return i;
    			}
    		}
    	} else {
    		for (int i = 0; i < size; i++) {
    			/*为啥需要重写equals方法*/
    			if (o.equals(elementData[i])) {
    				return i;
    			}
    		}
    	}
        return -1;
    }

    /**
     * 返回元素最后一次出现的下标,如果不包含则返回-1
     */
    @Override
    public int lastIndexOf(Object o) {
    	if (o == null) {
    		for (int i = size -1; i >= 0; i--) {
    			if (elementData[i] == null) {
    				return i;
    			}
    		}
    	} else {
    		for (int i = size -1; i >= 0; i--) {
    			/*为啥需要重写equals方法*/
    			if (o.equals(elementData[i])) {
    				return i;
    			}
    		}
    	}
        return -1;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }
    
    @Override
    public ListIterator<E> listIterator() {
        return null;
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return null;
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return null;
    }
}
