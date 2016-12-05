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
package org.jheaps.tree;

import java.util.Comparator;

import org.jheaps.MergeableAddressableHeap;

public class SkewHeapMergeableAddressableHeapTest extends AbstractMergeableAddressableHeapTest {

    @Override
    protected MergeableAddressableHeap<Integer, String> createHeap() {
        return new SkewHeap<Integer, String>();
    }

    @Override
    protected MergeableAddressableHeap<Integer, String> createHeap(Comparator<Integer> comparator) {
        return new SkewHeap<Integer, String>(comparator);
    }

}
