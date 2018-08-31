/*
 * (C) Copyright 2014-2018, by Dimitrios Michail
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
package org.jheaps.monotone;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jheaps.Heap;
import org.jheaps.annotations.ConstantTime;
import org.jheaps.annotations.LogarithmicTime;

/**
 * Base abstract implementation of a radix heap.
 * 
 * @author Dimitrios Michail
 *
 * @param <K>
 *            the key type
 */
abstract class AbstractRadixHeap<K> implements Heap<K>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Denotes that a key does not belong to a bucket
     */
    protected static final int NO_BUCKET = -1;
    
    /**
     * The buckets as lists. We use array-lists instead of linked-lists, to be
     * cache friendly.
     */
    protected List<K>[] buckets;

    /**
     * Number of elements
     */
    protected long size;
    
    /**
     * Last deleted key. This value is used to distribute elements in the
     * buckets. Should be initialized with the {@link #minKey} value.
     */
    protected K lastDeletedKey;

    /**
     * The current minimum value (cached)
     */
    protected K currentMin;

    /**
     * The current minimum value bucket (cached)
     */
    protected int currentMinBucket;   
    
    /**
     * The current minimum value position in bucket (cached)
     */
    protected int currentMinPos;

    /**
     * Minimum key allowed
     */
    protected K minKey;

    /**
     * Maximum key allowed
     */
    protected K maxKey;

    /**
     * Constructor
     */
    AbstractRadixHeap() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @LogarithmicTime(amortized = true)
    public K findMin() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return findAndCacheMinimum();
    }

    /**
     * {@inheritDoc}
     * 
     * @throws IllegalArgumentException
     *             if the key is null
     * @throws IllegalArgumentException
     *             if the key is less than the minimum allowed key
     * @throws IllegalArgumentException
     *             if the key is more than the maximum allowed key
     * @throws IllegalArgumentException
     *             if the key is less than the current minimum
     */
    @Override
    @LogarithmicTime(amortized = true)
    public void insert(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Null keys not permitted");
        }
        if (compare(key, maxKey) > 0) {
            throw new IllegalArgumentException("Key is more than the maximum allowed key");
        }

        if (compare(key, lastDeletedKey) < 0) {
            throw new IllegalArgumentException("Invalid key. Monotone heap.");
        }
        int b = computeBucket(key, lastDeletedKey);
        buckets[b].add(key);
        
        // clear minimum key cache if needed
        if (currentMin != null && compare(key, currentMin) < 0) {
            currentMin = null;
            currentMinBucket = NO_BUCKET;
            currentMinPos = -1;
        }
        
        size++;
    }

    /**
     * {@inheritDoc}
     * 
     * The cost of this operation is amortized O(logC) assuming the heap
     * contains keys in the range [0, C] or equivalently [a, a+C].
     */
    @Override
    @LogarithmicTime(amortized = true)
    public K deleteMin() {
        if (size == 0) {
            throw new NoSuchElementException();
        }

        K result = findAndCacheMinimum();

        // updated last deleted key
        lastDeletedKey = currentMin;
        
        if (currentMinBucket == 0) {
            // just remove since all values in first bucket are the same as the
            // previous lastDeletedKey
            buckets[currentMinBucket].remove(currentMinPos);
        } else {
            // redistribute all elements based on new lastDeletedKey
            int pos = 0;
            for (K val : buckets[currentMinBucket]) {
                if (pos != currentMinPos) { 
                    int b = computeBucket(val, lastDeletedKey);
                    assert b < currentMinBucket;
                    buckets[b].add(val);     
                }
                ++pos;
            }
            buckets[currentMinBucket].clear();
        }

        // clear current minimum cache
        currentMin = null;
        currentMinBucket = NO_BUCKET;
        currentMinPos = -1;

        size--;
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ConstantTime
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @ConstantTime
    public long size() {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        for (List<K> bucket : buckets) {
            bucket.clear();
        }
        size = 0;
        lastDeletedKey = minKey;
        currentMin = null;
        currentMinBucket = NO_BUCKET;
        currentMinPos = -1;
    }

    /**
     * Always returns {@code null} since this heap uses the
     * {@linkplain Comparable natural ordering} of its keys.
     * 
     * @return {@code null} since this heap uses the natural ordering of its
     *         keys
     */
    @Override
    public Comparator<? super K> comparator() {
        return null;
    }

    /**
     * Compares its two arguments for order. Returns a negative integer, zero,
     * or a positive integer as the first argument is less than, equal to, or
     * greater than the second.
     *
     * @param o1
     *            the first object to be compared.
     * @param o2
     *            the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     *         argument is less than, equal to, or greater than the second.
     */
    protected abstract int compare(K o1, K o2);

    /**
     * Compute the bucket of a key based on a minimum key.
     * 
     * @param key
     *            the key
     * @param minKey
     *            the minimum key
     * @return the bucket where the key should go
     */
    protected int computeBucket(K key, K minKey) {
        return 1 + Math.min(msd(key, minKey), buckets.length - 2);
    }

    /**
     * Compute the most significant digit which is different in the binary
     * representation of two values, or -1 if numbers are equal.
     * 
     * @param a
     *            the first value
     * @param b
     *            the second value
     * @return the most significant digit which is different or -1 if numbers
     *         are equal
     */
    protected abstract int msd(K a, K b);

    /**
     * Helper method for finding and caching the minimum. Assumes that the
     * heap contains at least one element.
     */
    private K findAndCacheMinimum() {
        if (currentMin == null) {
            // find first non-empty bucket
            currentMinBucket = NO_BUCKET;
            for (int i = 0; i < this.buckets.length; i++) {
                if (!buckets[i].isEmpty()) {
                    currentMinBucket = i;
                    break;
                }
            }
            assert currentMinBucket >= 0;
            
            // find new minimum and its position (beware of cached values)
            currentMinPos = -1;
            int pos = 0;
            for (K val : buckets[currentMinBucket]) {
                if (currentMin == null || compare(val, currentMin) < 0) { 
                    currentMin = val;
                    currentMinPos = pos;
                }
                ++pos;
            }
            assert currentMin != null && currentMinPos >= 0;
        }
        return currentMin;
    }
    
}
