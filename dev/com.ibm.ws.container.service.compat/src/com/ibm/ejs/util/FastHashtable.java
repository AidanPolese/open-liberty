// /I/ /W/ /G/ /U/   <-- CMVC Keywords, replace / with %
// 1.13 SERV1/ws/code/utils/src/com/ibm/ejs/util/FastHashtable.java, WAS.utils, WASX.SERV1, aa1225.01 10/11/10 10:25:00
//
// IBM Confidential OCO Source Material
//
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002, 2010
//
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Module  :  FastHashtable.java
//
// Source File Description:
//
//     Provides an optimized Hashtable implementation.
//
// Change Activity:
//
// Reason    Version   Date     Userid    Change Description
// --------- --------- -------- --------- -----------------------------------------
// d173022.12 ASV51X   20020814 leealber : Close The Gap - add new get/put with no
//                                         bucket synchronization
// LIDB2775-23.3 ASV60 20031215 pdykes   : Unity - integrate zOS specific changes
// d218838   WAS60     20040818 tkb      : PERF: redesigned for performance/size
// d366845.3 EJB3      20060615 kjlaw    : add generics for EJB3 usage.
// d477704   WAS70     20071107 mcasile  : Switch to new FFDC Facade API
// F743-33394
//           WAS80     20101011 bkail    : Don't precreate buckets
// --------- --------- -------- --------- -----------------------------------------

package com.ibm.ejs.util;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Provides an optimized Hashtable implementation. <p>
 **/
//d366845.3 add generic types support
public class FastHashtable<K, V> extends Dictionary<K, V> {
    /////////////////////////////////////////////////////////////////////////
    //
    // Construction
    //

    public FastHashtable(int expectedEntries) {
        @SuppressWarnings("unchecked")
        Bucket<K, V>[] uncheckedBuckets = new Bucket[expectedEntries];
        buckets = uncheckedBuckets;
    }

    /////////////////////////////////////////////////////////////////////////
    //
    // Attributes
    //

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /////////////////////////////////////////////////////////////////////////
    //
    // Operations
    //

    public Object getLock(K key) {
        return getBucketForKey(key);
    }

    public boolean contains(K key) {
        final Bucket<K, V> bucket = getBucketForKey(key);

        synchronized (bucket) {

            Element<K, V> element = bucket.findByKey(key);

            return element != null;

        }
    }

    public V get(Object key) {
        @SuppressWarnings("unchecked")
        K k = (K) key;
        final Bucket<K, V> bucket = getBucketForKey(k);

        synchronized (bucket) {

            final Element<K, V> element = bucket.findByKey(k);
            return (element != null ? element.ivObject : null);
        }
    }

    public V remove(Object key) {
        @SuppressWarnings("unchecked")
        K k = (K) key;
        final Bucket<K, V> bucket = getBucketForKey(k);
        Element<K, V> element = null;

        synchronized (bucket) {
            element = bucket.removeByKey(k);
        }

        if (element != null) {
            synchronized (this) {
                --size;
            }
            return element.ivObject;
        }

        return null;

    }

    public V put(K key, V object) {
        synchronized (this) {
            ++size;
        }

        final Bucket<K, V> bucket = getBucketForKey(key);

        synchronized (bucket) {
            final Element<K, V> e = bucket.replaceByKey(key, object);
            return (V) (e != null ? e.ivObject : null);
        }
    }

    public final Enumeration<V> elements() {
        return new ObjectEnumerator();
    }

    public final Enumeration<K> keys() {
        return new KeyEnumerator();
    }

    public synchronized void clear() {
        size = 0;
        for (Bucket<K, V> bucket : buckets) {
            if (bucket != null) {
                synchronized (bucket) {
                    bucket.clear();
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////
    //
    // Implementation
    //

    /**
     * Returns the bucket which the specified key hashes to
     **/
    protected final Bucket<K, V> getBucketForKey(K key) {
        int bucket_index = (key.hashCode() & 0x7FFFFFFF) % buckets.length;
        Bucket<K, V> thebucket = buckets[bucket_index];
        if (thebucket == null) {
            synchronized (this) {
                thebucket = buckets[bucket_index];
                if (thebucket == null) {
                    thebucket = new Bucket<K, V>();
                    buckets[bucket_index] = thebucket;
                } // if
            } // sync
        } // if
        return thebucket;
    }

    //////////////////////////////////////////////////////////////////////////
    //
    // Data
    //

    /**
     * The array of buckets. Buckets are lazily created using double-checked
     * locking. This is safe because Bucket objects have no initialization.
     */
    protected final Bucket<K, V>[] buckets;

    protected int size = 0;

    //////////////////////////////////////////////////////////////////////////

    abstract class ElementEnumerator<T> implements Enumeration<T> {

        ElementEnumerator() {
            // F743-33394 - Lock on the hashtable to check if the size is zero
            // and to ensure this thread sees a consistent view of the buckets
            // array at some point in time.
            synchronized (FastHashtable.this) {
                if (size == 0) {
                    // If size is empty, make findNextBucket be a no-op.
                    bucketIndex = buckets.length;
                }
            }
        }

        //
        // Enumeration interface
        //

        /**
         * Determine if there are more elements remaining in the enumeration;
         * returns true if more elements remain, false otherwise
         **/
        public boolean hasMoreElements() {
            // If there are more elements in the bucket we're currently
            // enumerating or if we can find another non-empty bucket, the
            // the enumeration has more elements

            if (ivCurrentBucketElement != null) {
                return true;
            } else {
                return findNextBucket();
            }
        }

        /**
         * Get the next element in the enumeration.
         **/
        public Element<K, V> nextBucketElement() {
            // If we don't have a current bucket, or we've exhausted the
            // elements in the current bucket, try to find another non-empty
            // bucket

            if (ivCurrentBucketElement == null) {
                if (!findNextBucket()) {
                    // No more non-empty buckets, no more elements
                    throw new NoSuchElementException();
                }
            }

            Element<K, V> nextElement = ivCurrentBucketElement;
            ivCurrentBucketElement = ivCurrentBucketElement.ivNext;
            return nextElement;
        }

        /**
         * Finds the next non-empty bucket of the cache's hash table; returns
         * true if such a bucket it found, false otherwise
         **/
        private boolean findNextBucket() {
            ivCurrentBucketElement = null;

            // F743-33394 - Note, we do not take a lock here to check for lazily
            // created buckets.  The constructor took a lock to ensure we had a
            // consistent view of memory at some point in time.  So, in the worst
            // case, this enumeration will fail to observe some newly created
            // buckets, but that's ok since the next enumeration will.
            while (bucketIndex < buckets.length) {
                Bucket<K, V> bucket = buckets[bucketIndex++];
                if (bucket != null) { // @WS14334.30A & LIDB2775-23.3
                    synchronized (bucket) {
                        if (bucket.size() > 0) {
                            ivCurrentBucketElement = bucket.ivHead;
                            return true;
                        }
                    }
                } // @WS14334.30A && // LIDB2775-23.3
            }

            return false;
        }

        /**
         * The array index of the hash table bucket we're currenly
         * enumerating
         **/
        private int bucketIndex;

        /** The next element of the current bucket **/
        private Element<K, V> ivCurrentBucketElement; // d218838

    }

    class ObjectEnumerator extends ElementEnumerator<V> {
        public V nextElement() {
            return nextBucketElement().ivObject;
        }
    }

    class KeyEnumerator extends ElementEnumerator<K> {
        public K nextElement() {
            return nextBucketElement().ivKey;
        }
    }
}
