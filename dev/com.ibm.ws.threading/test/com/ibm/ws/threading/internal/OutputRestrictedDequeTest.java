/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.threading.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests that can be used to help catch errors in experimental implementations of a thread-safe output restricted deque.
 */
public class OutputRestrictedDequeTest {
    // TODO toggle between a valid implementation that is not optimized to our needs (we don't need
    //      poll/remove from tail or reverse iteration) and various experimental implementations
    class OutputRestrictedDeque<T> extends LinkedBlockingDeque<T> {}
    //class OutputRestrictedDeque<T> extends BoundedBuffer<T> {}
    //class OutputRestrictedDeque<T> extends ORDeque<T> {}
    //...others

    private static ExecutorService testThreads;

    private static final long TIMEOUT_NS = TimeUnit.SECONDS.toNanos(10);

    @AfterClass
    public static void afterClass() {
        if (testThreads != null)
            testThreads.shutdownNow();
    }

    @BeforeClass
    public static void beforeClass() {
        testThreads = Executors.newFixedThreadPool(20);
    }

    // With 1 item in the queue, concurrently offer another, and then poll 2 items, where the second poll waits if necessary.
    // At the end of the test, both items should be polled and there should be nothing left in the queue.
    @Test
    public void testConcurrentOfferPoll() throws Exception {
        final OutputRestrictedDeque<String> q = new OutputRestrictedDeque<String>();
        assertTrue(q.offer("A"));
        Future<?> future = testThreads.submit(new Runnable() {
            @Override
            public void run() {
                q.offer("B");
            }
        });
        assertEquals("A", q.poll());
        assertEquals("B", q.poll(TIMEOUT_NS, TimeUnit.NANOSECONDS));
        future.get(TIMEOUT_NS, TimeUnit.NANOSECONDS);
        assertEquals(0, q.size());
    }

    // Concurrently poll the queue while on another thread offering 2 items and polling once.
    // The second poll which follows the 2 offers must return an item, whereas the first poll might or might not.
    @Test
    public void testConcurrentPollOffer() throws Exception {
        final OutputRestrictedDeque<String> q = new OutputRestrictedDeque<String>();
        Future<String> future = testThreads.submit(new Callable<String>() {
            @Override
            public String call() {
                return q.poll();
            }
        });
        assertTrue(q.offer("C"));
        assertTrue(q.offer("D"));

        String item2 = q.poll();
        String item1 = future.get(10, TimeUnit.SECONDS);

        // Poll on same thread as offers must return something because there has been at most 1 other poll
        assertNotNull(item2);
        assertNotSame(item1, item2);
        assertTrue("C".equals(item2) || "D".equals(item2));

        if (item1 == null)
            assertEquals(1, q.size());
        else {
            assertEquals(0, q.size());
            assertTrue("C".equals(item1) || "D".equals(item1));
        }
    }

    // General test of iterator, including concurrent removal
    @Test
    public void testIterator() throws Exception {
        assertFalse(new OutputRestrictedDeque<String>().iterator().hasNext());

        OutputRestrictedDeque<String> q = new OutputRestrictedDeque<String>();
        assertTrue(q.add("A"));
        assertTrue(q.add("B"));
        assertTrue(q.add("C"));
        assertTrue(q.add("C"));
        assertTrue(q.add("D"));
        assertTrue(q.add("C"));

        assertEquals(6, q.size());

        for (Iterator<String> it = q.iterator(); it.hasNext();)
            if (it.next().equals("C"))
                it.remove();

        assertEquals(3, q.size());

        Iterator<String> it = q.iterator();
        assertEquals("A", it.next());
        assertTrue(it.hasNext());
        it.remove(); // A

        assertEquals(2, q.size());
        assertFalse(q.contains("A"));
        assertTrue(q.contains("B"));

        assertTrue(q.add("E"));

        assertTrue(it.hasNext());
        assertEquals("B", it.next());
        assertEquals("D", it.next());
        assertTrue(q.remove("D"));
        try {
            it.remove();
            //fail("should not be able to remove what was already removed"); // TODO expectation for LinkedBlockingQueue differs here
        } catch (NoSuchElementException x) {
        }
        assertTrue(it.hasNext());
        try {
            it.remove();
            fail("remove should be disallowed before next invoked");
        } catch (IllegalStateException x) {
        }
        assertEquals("E", it.next());
        assertFalse(it.hasNext());
        assertEquals(2, q.size());
    }

    // Repeatedly have 2 threads performing offer, 2 performing push, 2 performing poll, 1 performing timed poll,
    // and one performing removal of specific items. Let the test run for a fixed duration and then compare the
    // reported size against what we think the size should be based on whether or not offers/pushes/polls/removes
    // were successful.
    @Test
    public void testMultipleOfferPushPollConcurrently() throws Exception {
        final long durationOfTestNS = TimeUnit.SECONDS.toNanos(2);

        final AtomicBoolean done = new AtomicBoolean();
        final OutputRestrictedDeque<Integer> q = new OutputRestrictedDeque<Integer>();
        final AtomicInteger size = new AtomicInteger();
        Callable<Void> offerTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                while (!done.get()) {
                    if (q.offer((int) (Math.random() * 5.0)))
                        size.incrementAndGet();
                }
                return null;
            }
        };
        Callable<Void> pushTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                while (!done.get()) {
                    q.push((int) (Math.random() * 5.0));
                    size.incrementAndGet();
                }
                return null;
            }
        };
        Callable<Void> pollTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                while (!done.get()) {
                    if (q.poll() != null)
                        size.decrementAndGet();
                }
                return null;
            }
        };
        Callable<Void> pollTimedTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                while (!done.get()) {
                    if (q.poll(500, TimeUnit.MILLISECONDS) != null)
                        size.decrementAndGet();
                }
                return null;
            }
        };
        Callable<Void> removeTask = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                while (!done.get()) {
                    if (q.remove((int) (Math.random() * 5.0)))
                        size.decrementAndGet();
                }
                return null;
            }
        };
        Future<?>[] f = new Future<?>[10];
        f[0] = testThreads.submit(offerTask);
        f[1] = testThreads.submit(pushTask);
        f[2] = testThreads.submit(pollTask);
        f[3] = testThreads.submit(pollTimedTask);
        f[4] = testThreads.submit(removeTask);
        f[5] = testThreads.submit(offerTask);
        f[6] = testThreads.submit(pushTask);
        f[7] = testThreads.submit(pollTask);
        f[8] = testThreads.submit(pollTimedTask);
        f[9] = testThreads.submit(removeTask);
        TimeUnit.NANOSECONDS.sleep(durationOfTestNS);
        done.set(true);
        for (Future<?> future : f)
            future.get();

        assertEquals(size.get(), q.size());

        int count = 0;
        for (Iterator<Integer> it = q.iterator(); it.hasNext();) {
            assertTrue(it.hasNext());
            assertNotNull(it.next());
            count++;
        }

        assertEquals(size.get(), count);
    }

    // Sequential test of: offer, remove specific items, poll, offer, and poll.
    @Test
    public void testOfferRemovePollOfferPoll() {
        OutputRestrictedDeque<String> q = new OutputRestrictedDeque<String>();
        assertTrue(q.offer("A"));
        assertTrue(q.offer("B"));
        assertTrue(q.offer("C"));
        assertTrue(q.offer("D"));
        assertTrue(q.offer("E"));
        assertTrue(q.offer("F"));
        assertEquals(6, q.size());
        assertTrue(q.remove("F"));
        assertTrue(q.remove("E"));
        assertTrue(q.remove("D"));
        assertEquals(3, q.size());
        assertTrue(q.remove("C"));
        assertTrue(q.remove("B"));
        assertTrue(q.remove("A"));
        assertEquals(0, q.size());
        assertNull(q.poll());
        assertEquals(0, q.size());
        assertTrue(q.offer("G"));
        assertEquals(1, q.size());
        assertEquals("G", q.poll());
        assertEquals(0, q.size());
    }

    // Sequential test of: push, poll, offer, poll.
    @Test
    public void testPushPollOfferPoll() {
        OutputRestrictedDeque<Integer> q = new OutputRestrictedDeque<Integer>();
        q.push(20);
        q.push(15);
        q.push(10);
        q.push(5);
        assertEquals(4, q.size());
        assertEquals(Integer.valueOf(5), q.peek());
        assertEquals(Integer.valueOf(5), q.poll());
        assertEquals(Integer.valueOf(10), q.peek());
        assertEquals(Integer.valueOf(10), q.poll());
        assertEquals(2, q.size());
        assertTrue(q.offer(25));
        assertEquals(Integer.valueOf(15), q.peek());
        assertEquals(Integer.valueOf(15), q.poll());
        assertEquals(Integer.valueOf(20), q.peek());
        assertEquals(Integer.valueOf(20), q.poll());
        assertEquals(1, q.size());
        assertEquals(Integer.valueOf(25), q.peek());
        assertEquals(Integer.valueOf(25), q.poll());
        assertEquals(0, q.size());
        assertNull(q.poll());
        assertEquals(0, q.size());
    }
}
