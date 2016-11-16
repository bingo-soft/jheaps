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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Random;

import org.jheaps.MapHeap.Entry;
import org.junit.Test;

public class RadixHeapTest {

	private static final int SIZE = 100000;

	@Test
	public void test() {
		Heap<Long> h = new RadixHeap<Long>(0, SIZE).asHeap();

		for (long i = 0; i < SIZE; i++) {
			h.insert(i);
			assertEquals(Long.valueOf(0), h.findMin());
			assertFalse(h.isEmpty());
			assertEquals(h.size(), i + 1);
		}

		for (int i = SIZE - 1; i >= 0; i--) {
			assertEquals(h.findMin().longValue(), Long.valueOf(SIZE - i - 1).longValue());
			h.deleteMin();
		}
	}

	@Test
	public void testVerySmall() {
		Heap<Long> h = new RadixHeap<Long>(29, 36).asHeap();

		h.insert(29l);
		h.insert(30l);
		h.insert(31l);
		h.insert(30l);
		h.insert(33l);
		h.insert(36l);
		h.insert(35l);

		assertEquals(h.size(), 7);
		assertEquals(h.findMin().longValue(), 29l);
		assertEquals(h.size(), 7);
		assertEquals(h.deleteMin().longValue(), 29l);
		assertEquals(h.size(), 6);
		assertEquals(h.findMin().longValue(), 30l);
		assertEquals(h.deleteMin().longValue(), 30l);
		assertEquals(h.findMin().longValue(), 30l);
		assertEquals(h.deleteMin().longValue(), 30l);
		assertEquals(h.findMin().longValue(), 31l);
		assertEquals(h.deleteMin().longValue(), 31l);
		assertEquals(h.findMin().longValue(), 33l);
		assertEquals(h.deleteMin().longValue(), 33l);
		assertEquals(h.findMin().longValue(), 35l);
		assertEquals(h.deleteMin().longValue(), 35l);
		assertEquals(h.findMin().longValue(), 36l);
		assertEquals(h.deleteMin().longValue(), 36l);
		assertEquals(h.size(), 0);
		assertTrue(h.isEmpty());
	}

	@Test
	public void testSortRandomSeed1() {
		Heap<Long> h = new RadixHeap<Long>(0, SIZE + 1).asHeap();

		Random generator = new Random(1);

		long[] a = new long[SIZE];
		for (int i = 0; i < SIZE; i++) {
			a[i] = (long) (SIZE * generator.nextDouble());
		}
		Arrays.sort(a);
		for (int i = 0; i < SIZE; i++) {
			h.insert(a[i]);
		}

		Long prev = null, cur;
		while (!h.isEmpty()) {
			cur = h.findMin();
			h.deleteMin();
			if (prev != null) {
				assertTrue(prev.compareTo(cur) <= 0);
			}
			prev = cur;
		}
	}

	@Test
	public void testSort2RandomSeed1() {
		Heap<Long> h = new RadixHeap<Boolean>(0, SIZE + 1).asHeap();

		Random generator = new Random(1);

		long[] a = new long[SIZE];
		for (int i = 0; i < SIZE; i++) {
			a[i] = (long) (SIZE * generator.nextDouble());
		}
		Arrays.sort(a);
		for (int i = 0; i < SIZE; i++) {
			h.insert(a[i]);
		}

		Long prev = null, cur;
		while (!h.isEmpty()) {
			cur = h.deleteMin();
			if (prev != null) {
				assertTrue(prev.compareTo(cur) <= 0);
			}
			prev = cur;
		}
	}

	@Test
	public void testSortRandomSeed2() {
		Heap<Long> h = new RadixHeap<Boolean>(0, SIZE + 1).asHeap();

		Random generator = new Random(2);

		long[] a = new long[SIZE];
		for (int i = 0; i < SIZE; i++) {
			a[i] = (long) (SIZE * generator.nextDouble());
		}
		Arrays.sort(a);
		for (int i = 0; i < SIZE; i++) {
			h.insert(a[i]);
		}

		Long prev = null, cur;
		while (!h.isEmpty()) {
			cur = h.findMin();
			h.deleteMin();
			if (prev != null) {
				assertTrue(prev.compareTo(cur) <= 0);
			}
			prev = cur;
		}
	}

	@Test
	public void testSort2RandomSeed2() {
		Heap<Long> h = new RadixHeap<Long>(0, SIZE + 1).asHeap();

		Random generator = new Random(2);

		long[] a = new long[SIZE];
		for (int i = 0; i < SIZE; i++) {
			a[i] = (long) (SIZE * generator.nextDouble());
		}
		Arrays.sort(a);
		for (int i = 0; i < SIZE; i++) {
			h.insert(a[i]);
		}

		Long prev = null, cur;
		while (!h.isEmpty()) {
			cur = h.deleteMin();
			if (prev != null) {
				assertTrue(prev.compareTo(cur) <= 0);
			}
			prev = cur;
		}
	}

	@Test
	public void testSort3RandomSeed1() {
		MapHeap<Long, String> h = new RadixHeap<String>(0, SIZE + 1);

		Random generator = new Random(1);

		long[] a = new long[SIZE];
		for (int i = 0; i < SIZE; i++) {
			a[i] = (long) (SIZE * generator.nextDouble());
		}
		Arrays.sort(a);
		for (int i = 0; i < SIZE; i++) {
			h.insert(a[i], String.valueOf(a[i]));
		}

		Entry<Long, String> prev = null, cur;
		while (!h.isEmpty()) {
			cur = h.deleteMin();
			assertEquals(String.valueOf(cur.getKey()), cur.getValue());
			if (prev != null) {
				assertTrue(prev.getKey().compareTo(cur.getKey()) <= 0);
			}
			prev = cur;
		}
	}

	@Test
	public void testClear() {
		Heap<Long> h = new RadixHeap<Boolean>(0, 15).asHeap();

		for (long i = 0; i < 15; i++) {
			h.insert(i);
		}

		h.clear();
		assertEquals(0L, h.size());
		assertTrue(h.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMonotone() {
		Heap<Long> h = new RadixHeap<Boolean>(0, 1000).asHeap();
		h.insert(100l);
		h.insert(99l);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalConstruction() {
		new RadixHeap<Long>(-1, 100);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalConstruction1() {
		new RadixHeap<Boolean>(100, 99);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSerializable() throws IOException, ClassNotFoundException {
		Heap<Long> h = new RadixHeap<Long>(0, 15).asHeap();

		for (long i = 0; i < 15; i++) {
			h.insert(i);
		}

		// write
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(h);
		oos.close();
		byte[] data = baos.toByteArray();

		// read
		h = null;

		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		Object o = ois.readObject();
		ois.close();
		h = (Heap<Long>) o;

		for (int i = 0; i < 15; i++) {
			assertEquals(15 - i, h.size());
			assertEquals(Long.valueOf(i), h.findMin());
			h.deleteMin();
		}
		assertTrue(h.isEmpty());

	}

}
