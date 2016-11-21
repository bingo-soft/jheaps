/*
 * (C) Copyright 2014-2016, by Dimitrios Michail
 *
 * JHeaps Library
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jheaps;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Comparator;

import org.jheaps.annotations.LinearTime;

/**
 * An array based d-ary addressable heap. The heap is sorted according to the
 * {@linkplain Comparable natural ordering} of its keys, or by a
 * {@link Comparator} provided at heap creation time, depending on which
 * constructor is used.
 *
 * <p>
 * The implementation uses an array in order to store the elements. and
 * automatically maintains the size of the array much like a
 * {@link java.util.Vector} does, providing amortized O(log_d(n)) time cost for
 * the {@code insert} and amortized O(d log_d(n)) for the {@code deleteMin}
 * operation. Operation {@code findMin}, is a worst-case O(1) operation.
 * Operations {@code delete} and {@code decreaseKey} take worst-case O(log(n))
 * time.
 * 
 * <p>
 * Constructing such a heap from an array of elements can be performed using the
 * method {@link #heapify(int, Object[], Object[])} or
 * {@link #heapify(int, Object[], Object[], Comparator)} in linear time.
 *
 * <p>
 * Note that the ordering maintained by a d-ary heap, like any heap, and whether
 * or not an explicit comparator is provided, must be <em>consistent with
 * {@code equals}</em> if this heap is to correctly implement the {@code Heap}
 * interface. (See {@code Comparable} or {@code Comparator} for a precise
 * definition of <em>consistent with equals</em>.) This is so because the
 * {@code Heap} interface is defined in terms of the {@code equals} operation,
 * but a binary heap performs all key comparisons using its {@code compareTo}
 * (or {@code compare}) method, so two keys that are deemed equal by this method
 * are, from the standpoint of the d-ary heap, equal. The behavior of a heap
 * <em>is</em> well-defined even if its ordering is inconsistent with
 * {@code equals}; it just fails to obey the general contract of the
 * {@code Heap} interface.
 *
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access a heap concurrently, and at least one of the threads
 * modifies the heap structurally, it <em>must</em> be synchronized externally.
 * (A structural modification is any operation that adds or deletes one or more
 * elements or changing the key of some element.) This is typically accomplished
 * by synchronizing on some object that naturally encapsulates the heap.
 *
 * @param <K>
 *            the type of keys maintained by this heap
 * @param <V>
 *            the type of values maintained by this heap
 *
 * @author Dimitrios Michail
 */
public class DaryArrayAddressableHeap<K, V> extends AbstractDaryArrayAddressableHeap<K, V> implements Serializable {

	private final static long serialVersionUID = 1;

	/**
	 * Default initial capacity of the binary heap.
	 */
	public static final int DEFAULT_HEAP_CAPACITY = 128;

	/**
	 * Constructs a new, empty heap, using the natural ordering of its keys.
	 *
	 * <p>
	 * All keys inserted into the heap must implement the {@link Comparable}
	 * interface. Furthermore, all such keys must be <em>mutually
	 * comparable</em>: {@code k1.compareTo(k2)} must not throw a
	 * {@code ClassCastException} for any keys {@code k1} and {@code k2} in the
	 * heap. If the user attempts to put a key into the heap that violates this
	 * constraint (for example, the user attempts to put a string key into a
	 * heap whose keys are integers), the {@code insert(Object key)} call will
	 * throw a {@code ClassCastException}.
	 *
	 * <p>
	 * The initial capacity of the heap is
	 * {@link DaryArrayHeap#DEFAULT_HEAP_CAPACITY} and adjusts automatically
	 * based on the sequence of insertions and deletions.
	 * 
	 * @param d
	 *            the number of children of each node in the d-ary heap
	 * @throws IllegalArgumentException
	 *             in case the number of children per node are less than 2
	 */
	public DaryArrayAddressableHeap(int d) {
		super(d, null, DEFAULT_HEAP_CAPACITY);
	}

	/**
	 * Constructs a new, empty heap, with a provided initial capacity using the
	 * natural ordering of its keys.
	 *
	 * <p>
	 * All keys inserted into the heap must implement the {@link Comparable}
	 * interface. Furthermore, all such keys must be <em>mutually
	 * comparable</em>: {@code k1.compareTo(k2)} must not throw a
	 * {@code ClassCastException} for any keys {@code k1} and {@code k2} in the
	 * heap. If the user attempts to put a key into the heap that violates this
	 * constraint (for example, the user attempts to put a string key into a
	 * heap whose keys are integers), the {@code insert(Object key)} call will
	 * throw a {@code ClassCastException}.
	 *
	 * <p>
	 * The initial capacity of the heap is provided by the user and is adjusted
	 * automatically based on the sequence of insertions and deletions.
	 *
	 * @param d
	 *            the number of children of each node in the d-ary heap
	 * @param capacity
	 *            the initial heap capacity
	 * @throws IllegalArgumentException
	 *             in case the number of children per node are less than 2
	 */
	public DaryArrayAddressableHeap(int d, int capacity) {
		super(d, null, capacity);
	}

	/**
	 * Constructs a new, empty heap, ordered according to the given comparator.
	 *
	 * <p>
	 * All keys inserted into the heap must be <em>mutually comparable</em> by
	 * the given comparator: {@code comparator.compare(k1,
	 * k2)} must not throw a {@code ClassCastException} for any keys {@code k1}
	 * and {@code k2} in the heap. If the user attempts to put a key into the
	 * heap that violates this constraint, the {@code insert(Object key)} call
	 * will throw a {@code ClassCastException}.
	 *
	 * <p>
	 * The initial capacity of the heap is
	 * {@link DaryArrayAddressableHeap#DEFAULT_HEAP_CAPACITY} and adjusts
	 * automatically based on the sequence of insertions and deletions.
	 *
	 * @param d
	 *            the number of children of each node in the d-ary heap
	 * @param comparator
	 *            the comparator that will be used to order this heap. If
	 *            {@code null}, the {@linkplain Comparable natural ordering} of
	 *            the keys will be used.
	 * @throws IllegalArgumentException
	 *             in case the number of children per node are less than 2 *
	 */
	public DaryArrayAddressableHeap(int d, Comparator<? super K> comparator) {
		super(d, comparator, DEFAULT_HEAP_CAPACITY);
	}

	/**
	 * Constructs a new, empty heap, with a provided initial capacity ordered
	 * according to the given comparator.
	 *
	 * <p>
	 * All keys inserted into the heap must be <em>mutually comparable</em> by
	 * the given comparator: {@code comparator.compare(k1,
	 * k2)} must not throw a {@code ClassCastException} for any keys {@code k1}
	 * and {@code k2} in the heap. If the user attempts to put a key into the
	 * heap that violates this constraint, the {@code insert(Object key)} call
	 * will throw a {@code ClassCastException}.
	 *
	 * <p>
	 * The initial capacity of the heap is provided by the user and is adjusted
	 * automatically based on the sequence of insertions and deletions.
	 *
	 * @param d
	 *            the number of children of each node in the d-ary heap
	 * @param comparator
	 *            the comparator that will be used to order this heap. If
	 *            {@code null}, the {@linkplain Comparable natural ordering} of
	 *            the keys will be used.
	 * @param capacity
	 *            the initial heap capacity
	 * @throws IllegalArgumentException
	 *             in case the number of children per node are less than 2
	 */
	public DaryArrayAddressableHeap(int d, Comparator<? super K> comparator, int capacity) {
		super(d, comparator, capacity);
	}

	/**
	 * Create a heap from an array of elements. The elements of the array are
	 * not destroyed. The method has linear time complexity.
	 *
	 * @param <K>
	 *            the type of keys maintained by the heap
	 * @param <V>
	 *            the type of values maintained by the heap
	 * @param d
	 *            the number of children of the d-ary heap
	 * @param keys
	 *            an array of keys
	 * @param values
	 *            an array of values, can be null
	 * @return a d-ary heap
	 * @throws IllegalArgumentException
	 *             in case the number of children per node are less than 2
	 * @throws IllegalArgumentException
	 *             in case the keys array is null
	 * @throws IllegalArgumentException
	 *             in case the values array has different length than the keys
	 *             array
	 */
	@LinearTime
	public static <K, V> DaryArrayAddressableHeap<K, V> heapify(int d, K[] keys, V[] values) {
		if (d < 2) {
			throw new IllegalArgumentException("D-ary heaps must have at least 2 children per node");
		}
		if (keys == null) {
			throw new IllegalArgumentException("Key array cannot be null");
		}
		if (values != null && keys.length != values.length) {
			throw new IllegalArgumentException("Values array must have the same length as the keys array");
		}
		if (keys.length == 0) {
			return new DaryArrayAddressableHeap<K, V>(d);
		}

		DaryArrayAddressableHeap<K, V> h = new DaryArrayAddressableHeap<K, V>(d, keys.length);

		for (int i = 0; i < keys.length; i++) {
			K key = keys[i];
			V value = (values == null) ? null : values[i];
			AbstractArrayAddressableHeap<K, V>.ArrayHandle ah = h.new ArrayHandle(key, value);
			ah.index = i + 1;
			h.array[i + 1] = ah;
		}
		h.size = keys.length;

		for (int i = keys.length / d; i > 0; i--) {
			h.fixdown(i);
		}

		return h;
	}

	/**
	 * Create a heap from an array of elements. The elements of the array are
	 * not destroyed. The method has linear time complexity.
	 *
	 * @param <K>
	 *            the type of keys maintained by the heap
	 * @param <V>
	 *            the type of values maintained by the heap
	 * @param d
	 *            the number of children of the d-ary heap
	 * @param keys
	 *            an array of keys
	 * @param values
	 *            an array of values, can be null
	 * @param comparator
	 *            the comparator to use
	 * @return a d-ary heap
	 * @throws IllegalArgumentException
	 *             in case the number of children per node are less than 2
	 * @throws IllegalArgumentException
	 *             in case the keys array is null
	 * @throws IllegalArgumentException
	 *             in case the values array has different length than the keys
	 *             array
	 */
	@LinearTime
	public static <K, V> DaryArrayAddressableHeap<K, V> heapify(int d, K[] keys, V[] values,
			Comparator<? super K> comparator) {
		if (d < 2) {
			throw new IllegalArgumentException("D-ary heaps must have at least 2 children per node");
		}
		if (keys == null) {
			throw new IllegalArgumentException("Keys array cannot be null");
		}
		if (values != null && keys.length != values.length) {
			throw new IllegalArgumentException("Values array must have the same length as the keys array");
		}
		if (keys.length == 0) {
			return new DaryArrayAddressableHeap<K, V>(d, comparator);
		}

		DaryArrayAddressableHeap<K, V> h = new DaryArrayAddressableHeap<K, V>(d, comparator, keys.length);

		for (int i = 0; i < keys.length; i++) {
			K key = keys[i];
			V value = (values == null) ? null : values[i];
			AbstractArrayAddressableHeap<K, V>.ArrayHandle ah = h.new ArrayHandle(key, value);
			ah.index = i + 1;
			h.array[i + 1] = ah;
		}
		h.size = keys.length;

		for (int i = keys.length / d; i > 0; i--) {
			h.fixdownWithComparator(i);
		}

		return h;
	}

	/**
	 * Ensure that the array representation has the necessary capacity.
	 * 
	 * @param capacity
	 *            the requested capacity
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void ensureCapacity(int capacity) {
		checkCapacity(capacity);
		ArrayHandle[] newArray = (ArrayHandle[]) Array.newInstance(ArrayHandle.class, capacity + 1);
		System.arraycopy(array, 1, newArray, 1, size);
		array = newArray;
	}

}
