/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ejs.util.cache;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class SweepLruEvictionStrategyTest
{
    @Test
    @Ignore
    public void testSweepLruEvictionStrategy() throws Exception
    {
        int size = 15;

        int bigSize = 30;

        int softLimit = 2;

        Object[] objectList = new Object[bigSize];

        Cache testCache = new Cache("TestCache", 9, false);

        final ScheduledExecutorService scheduledExecutorService =
                        Executors.newScheduledThreadPool(1); // F73234

        SweepLruEvictionStrategy ev =
                        new SweepLruEvictionStrategy(testCache, 9, 10000, scheduledExecutorService, scheduledExecutorService); // F73234
        testCache.setEvictionStrategy(ev);
        testCache.setCachePreferredMaxSize(softLimit);

        java.text.SimpleDateFormat format =
                        new java.text.SimpleDateFormat("HH:mm:ss.SSSS");

        //
        // Fill the cache to the soft limit with some stuff
        //
        for (int i = 0; i < size; ++i) {
            String key = Integer.toString(i);
            String object = format.format(new Date());
            System.out.println("Inserting (" + key + ", " + object + ")");
            testCache.insert(key, object);
            objectList[i] = object;
            testCache.unpin(key);
        }

        System.out.println("---- There are " + testCache.getSize() +
                           " elements in the cache");

        Assert.assertEquals("The size of cache is wrong. ", size, testCache.getSize());

        //
        // Check to see if we can find everything we inserted
        //

        for (int i = size - 1; i >= 0; --i) {
            String key = Integer.toString(i);
            String object = (String) testCache.find(key);
            System.out.println("Found (" + key + ", " + object + ")");
            Assert.assertSame("The object found isn't correct. ", objectList[i], object);
            testCache.unpin(key);
        }

        System.out.println("---- There are still " + testCache.getSize() +
                           " elements in the cache");

        Assert.assertEquals("The size of cache is wrong. ", size, testCache.getSize());

        //
        // Look for stuff we haven't inserted yet
        //

        for (int i = bigSize - 1; i >= size; --i) {
            String key = Integer.toString(i);
            String object = (String) testCache.find(key);
            System.out.println("Found (" + key + ", " + object + ")");
            Assert.assertNull("Found an object that shouldn't be inserted yet. ", object);
        }

        System.out.println("---- There are still " + testCache.getSize() +
                           " elements in the cache");

        Assert.assertEquals("The size of cache is wrong. ", size, testCache.getSize());

        //
        // Stuff more into the cache to exceed the hard limit
        //

        for (int i = size; i < bigSize; ++i) {
            String key = Integer.toString(i);
            String object = format.format(new Date());
            System.out.println("Inserting (" + key + ", " + object + ")");
            testCache.insert(key, object);
            testCache.unpin(key);
        }

        System.out.println("---- There are now " + testCache.getSize() +
                           " elements in the cache");

        Assert.assertEquals("The size of cache is wrong. ", bigSize, testCache.getSize());

        //
        // Wait and see if LRU evicts any objects
        //

        ev.start();

        int iterations = 0;
        do {

            // We earlier specified the cache's soft limit (which is the only limit we use) to be 2 objects. 
            // We filled the cache with 30 objects. The SweepLru strategy of evicting gets rid of all the objects
            // it can evict once it's threshold is reached. Since we specified the discard threshold of an object to be 20 with
            // sweeps happening every 10 seconds, the threshold gets adjusted to 6 (for performance reasons). 
            // This means that it will take 7 sweeps to get to a point where we can get rid of all the objects. Since each iteration
            // takes a second and a sweep takes 10 seconds, we end up spending 70 seconds + 1 second for the final iteration. 
            // To accommodate for hardware problems, we'll double that amount and make sure the loop won't get stuck beyond that.  
            Assert.assertTrue("Maximum iterations exceeded.", ++iterations < 142);
            System.out.println("Sleeping...");
            Thread.sleep(1000);
            System.out.println(">>>> There are now " + testCache.getSize() +
                               " elements in the cache");
        } while (testCache.getSize() > softLimit);

        //
        // Let's see what we've got left...
        //

        int emptySpaces = 0;
        for (int i = 0; i < bigSize; ++i) {
            String key = Integer.toString(i);
            String object = (String) testCache.find(key);
            System.out.println("Found (" + key + ", " + object + ")");
            if (object == null) {
                emptySpaces++;
            }
        }

        Assert.assertEquals("The cache size is inconsistent. ", testCache.getSize(), bigSize - emptySpaces);

        System.out.println("---- There are " + testCache.getSize() +
                           " elements in the cache");
    }

    @Test
    @Ignore
    public void testSetPreferredMaxSize() throws Exception
    {
        int bigSize = 30;

        int smallSize = 15;

        Cache testCache = new Cache("TestCache", bigSize, false);

        final ScheduledExecutorService scheduledExecutorService =
                        Executors.newScheduledThreadPool(1);

        SweepLruEvictionStrategy ev =
                        new SweepLruEvictionStrategy(testCache, bigSize, 3000, scheduledExecutorService, scheduledExecutorService);
        testCache.setEvictionStrategy(ev);

        // Initially the preferred max size is the size specified at the creation of the cache
        Assert.assertEquals(bigSize, ev.getPreferredMaxSize());

        // Set the preferred size to be something else
        testCache.setCachePreferredMaxSize(smallSize);

        // Preferred size is still original size - this gets updated every time the eviction 
        // strategy wakes up.
        Assert.assertEquals(bigSize, ev.getPreferredMaxSize());

        ev.start();
        Thread.sleep(3500);

        // We slept past the sweep interval time, and now see the update
        Assert.assertEquals(smallSize, ev.getPreferredMaxSize());
    }

    @Test
    @Ignore
    public void testSetSweepInterval() throws Exception
    {
        int bigSize = 30;

        int smallSize = 15;

        long initSweepInterval = 3000;
        long longSweepInterval = 15000;

        Cache testCache = new Cache("TestCache", bigSize, false);

        final ScheduledExecutorService scheduledExecutorService =
                        Executors.newScheduledThreadPool(1);

        // Set the initial sweep interval to be small
        SweepLruEvictionStrategy ev =
                        new SweepLruEvictionStrategy(testCache, bigSize, initSweepInterval, scheduledExecutorService, scheduledExecutorService);
        testCache.setEvictionStrategy(ev);

        // Change the preferred cache size 
        testCache.setCachePreferredMaxSize(smallSize);

        ev.start();
        Thread.sleep(3500);

        // After a sleep of 3.5 seconds, the eviction strategy should have woken up
        // and thus re-set the preferred cache size
        Assert.assertEquals(smallSize, ev.getPreferredMaxSize());

        // Set new values to be updated next time around
        testCache.setSweepInterval(longSweepInterval);
        testCache.setCachePreferredMaxSize(bigSize);

        Thread.sleep(3500);

        // Old sweep interval was still in effect, so observe that the cache size
        // was indeed modified
        Assert.assertEquals(bigSize, ev.getPreferredMaxSize());

        //Change the cache size again and see when it is updated
        testCache.setCachePreferredMaxSize(smallSize);

        Thread.sleep(5000);

        // After a sleep of 5s, the eviction strategy has not woken up yet,
        // so the cache size should still be at the bigSize
        Assert.assertEquals(bigSize, ev.getPreferredMaxSize());

        Thread.sleep(12000);

        // Sleep past the sweep point, and observe that the cache size did indeed
        // update after this prolonged sweep interval
        Assert.assertEquals(smallSize, ev.getPreferredMaxSize());
    }
}
