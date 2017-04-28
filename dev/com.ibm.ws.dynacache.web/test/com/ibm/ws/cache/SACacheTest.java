package com.ibm.ws.cache;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import test.common.SharedOutputManager;

public class SACacheTest {

    boolean runNow = true;
    int syncPoint = 0;
    float batchInvalidateErrors = 0;
    float batchInvalidateCounter = 0;
    float setGetFailures = 0;
    float setGets = 0;

    SharedOutputManager outputMgr = SharedOutputManager.getInstance().trace(StandaloneCache.TRACE_STRING);

    @Rule
    public TestRule testOutput = outputMgr;

    @Before
    public void setUp() throws Exception {
        StandaloneCache.initialize("SACacheTest");
    }

    @After
    public void tearDown() {
        ((CacheServiceImpl) ServerCache.getCacheService()).stop();
    }

    @Test
    public void testPutGet() throws Exception {

        ServerCache.cache.clear();
        for (int i = 0; i < 10000; i++) {
            String id = "test:" + i;
            CacheEntry ce = (CacheEntry) ServerCache.cache.getEntry(id);
            assertNull("cache entry not null before set", ce);
            ce = new CacheEntry();
            EntryInfo ei = new EntryInfo();
            ei.setId(id);
            ei.setTimeLimit(600);
            ei.addDataId("my data id");
            ce.setValue("hello world:" + i);
            ce.copyMetaData(ei);
            ServerCache.cache.setEntry(ce);
            ce = (CacheEntry) ServerCache.cache.getEntry(id);
            assertNotNull("cache entry was null after set", ce);
        }
    }

    public void testBatchInvalidation() throws Exception {
        System.out.println("");
        System.out.println("running: testBatchInvalidation()");

        //-----------------------------------------
        // Add entries into Cache
        //-----------------------------------------
        ServerCache.cache.clear();
        for (int i = 0; i < 100; i++) {
            String id = "test:" + i;
            CacheEntry ce = new CacheEntry();
            EntryInfo ei = new EntryInfo();
            ei.setId(id);
            ei.addDataId("my data id");
            ce.setValue("hello world:" + i);
            ce.copyMetaData(ei);
            ServerCache.cache.setEntry(ce);
        }
        //-----------------------------------------

        //-----------------------------------------
        // Start setEntry thread
        //-----------------------------------------
        Thread t = new Thread() {
            @Override
            public void run()
            {
                System.out.println("running: testBatchInvalidation() - Thread 1 is running");

                while (runNow)
                {

                    try
                    {
                        if (syncPoint != 1)
                        {
                            Thread.sleep(1);
                            continue;
                        }

                        for (int i = 0; i < 100; i++) {
                            String id = "test:" + i;
                            CacheEntry ce = new CacheEntry();
                            EntryInfo ei = new EntryInfo();
                            ei.setId(id);
                            ei.addDataId("my data id");
                            ce.setValue("hello world:" + i);
                            ce.copyMetaData(ei);
                            ServerCache.cache.setEntry(ce);
                            ce = (CacheEntry) ServerCache.cache.getEntry(id);
                            if (null == ce)
                            {
                                setGetFailures++;
                            }
                            setGets++;
                            //assertNotNull("running: testBatchInvalidation() - cache entry was null after get",ce);
                        }

                        Thread.sleep(10);

                        syncPoint = 2;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }

                System.out.println("running: testBatchInvalidation() - Thread 1 is stopped");
            }
        };
        t.setDaemon(true);
        t.start();

        try {
            Thread.sleep(500);
        } catch (Exception ex) {
        }

        //-----------------------------------------
        // Start invalidation thread
        //-----------------------------------------
        Thread t1 = new Thread() {
            @Override
            public void run()
            {
                System.out.println("running: testBatchInvalidation() - Thread 2 is running");

                while (runNow)
                {

                    try
                    {
                        if (syncPoint != 0)
                        {
                            Thread.sleep(1);
                            continue;
                        }

                        for (int i = 0; i < 100; i++) {
                            String id = "test:" + i;

                            //ServerCache.cache.invalidateById(id, true);
                            ServerCache.cache.invalidateById(id, false);
                        }

                        Thread.sleep(10);

                        syncPoint = 1;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }

                System.out.println("running: testBatchInvalidation() - Thread 2 is stopped");
            }
        };
        t1.setDaemon(true);
        t1.start();

        //-----------------------------------------
        // Start verification thread
        //-----------------------------------------
        Thread t2 = new Thread() {
            @Override
            public void run()
            {
                System.out.println("running: testBatchInvalidation() - Thread 3 is running");

                while (runNow)
                {

                    try
                    {
                        if (syncPoint != 2)
                        {
                            Thread.sleep(1);
                            continue;
                        }

                        for (int i = 0; i < 100; i++) {
                            String id = "test:" + i;

                            CacheEntry ce = (CacheEntry) ServerCache.cache.getEntry(id);
                            if (ce == null)
                                batchInvalidateErrors++;
                            batchInvalidateCounter++;
                        }

                        Thread.sleep(1);

                        syncPoint = 0;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }

                System.out.println("running: testBatchInvalidation() - Thread 3 is stopped");
            }
        };
        t2.setDaemon(true);
        t2.start();

        try {
            Thread.sleep(30000);
        } catch (Exception ex) {
        }

        runNow = false;

        try {
            Thread.sleep(1000);
        } catch (Exception ex) {
        }

        ServerCache.cache.clear();

        float batchInvalidateRate = (batchInvalidateErrors / batchInvalidateCounter) * 100;
        if (batchInvalidateRate < .001)
            batchInvalidateRate = 0.0f;
        float setGetRate = (setGetFailures / setGets) * 100;
        if (setGetRate < .001)
            setGetRate = 0.0f;
        String s = ((new Float(batchInvalidateRate)).toString() + "0000").substring(0, 4);
        String s1 = ((new Float(setGetRate)).toString() + "0000").substring(0, 4);

        System.out.println("Live entry invalidation %: " + s + "%  " + batchInvalidateErrors + " out of " + batchInvalidateCounter);
        System.out.println("      Cache put failure %: " + s1 + "%  " + setGetFailures + " out of " + setGets);

        if (setGetRate > .75f) // .75%
        {
            assertNotNull("error - " + setGetFailures + " cache entries out of " + setGets + " were null after get (" + setGetFailures + ")", null);
        }

        if (batchInvalidateRate > .75f) // .75%
        {
            assertNotNull("error - live cache entries were invalidated (" + batchInvalidateErrors + ")  " + s + "%", null);
        }
    }

//------------------------------------------------------------

    //------------------------------------------------------------
    // @A1A - Added this test
    //------------------------------------------------------------
    @Test
    public void testSetEntryCopy() throws Exception
    //------------------------------------------------------------
    {
        System.out.println("");
        System.out.println("running: testSetEntryCopy()");

        int oldTimeLimit = 50000;
        int newTimeLimit = 60000;

        ServerCache.cache.clear();
        for (int i = 0; i < 100; i++) {
            String id = "test:" + i;

            //----------------------------------
            // Create a CacheEntry
            //----------------------------------
            CacheEntry ceOld = new CacheEntry();
            EntryInfo eiOld = new EntryInfo();
            eiOld.setId(id);
            eiOld.addDataId("my data id");
            eiOld.setTimeLimit(oldTimeLimit);
            ceOld.copyMetaData(eiOld);
            ceOld.setValue("hello world:" + i);
            //----------------------------------

            //----------------------------------
            // Put it, Get it
            //----------------------------------
            ServerCache.cache.setEntry(ceOld);
            ceOld = (CacheEntry) ServerCache.cache.getEntry(id);
            assertNotNull("cache entry was null after set", ceOld);
            //----------------------------------

            //----------------------------------
            // Create a new CacheEntry - same id, different timeLimit
            //----------------------------------
            CacheEntry ceNew = new CacheEntry();
            EntryInfo eiNew = new EntryInfo();
            eiNew.setId(id);
            eiNew.addDataId("my data id");
            eiNew.setTimeLimit(newTimeLimit);
            ceNew.copyMetaData(eiNew);
            ceNew.setValue("new hello world:" + i);
            //----------------------------------

            //----------------------------------
            // Put it, Get it.
            //----------------------------------
            ServerCache.cache.setEntry(ceNew);
            ceNew = (CacheEntry) ServerCache.cache.getEntry(id);
            assertNotNull("cache entryNew  was null after set", ceNew);
            //----------------------------------

            //----------------------------------
            // Verify
            //----------------------------------
            if (ceNew.getTimeLimit() != newTimeLimit) {
                assertNotNull("TimeLimit copy error", null);
            }
            //----------------------------------

        }

        ServerCache.cache.clear();
    }

    //------------------------------------------------------------

    @Test
    public void testTimeout() throws Exception {

        //set some entries
        ServerCache.cache.clear();
        for (int i = 0; i < 100; i++) {
            String id = "test:" + i;
            CacheEntry ce = new CacheEntry();
            EntryInfo ei = new EntryInfo();
            ei.setId(id);
            ei.setTimeLimit(10);
            ei.addDataId("my data id");
            ce.setValue("hello world:" + i);
            ce.copyMetaData(ei);
            ServerCache.cache.setEntry(ce);
            ce = (CacheEntry) ServerCache.cache.getEntry(id);
            assertNotNull("cache entry was null after set", ce);
        }
        //wait for timeout
        try {
            Thread.sleep(20000);
        } catch (Exception ex) {
        }
        //make sure entries are null
        for (int i = 0; i < 10000; i++) {
            String id = "test:" + i;
            CacheEntry ce = (CacheEntry) ServerCache.cache.getEntry(id);
            assertNull("cache not null after timeout", ce);
        }
    }

    @Test
    public void testEntryInvalidation() throws Exception {
        ServerCache.cache.clear();
        //set some entries
        for (int i = 0; i < 100; i++) {
            String id = "test:" + i;
            CacheEntry ce = new CacheEntry();
            EntryInfo ei = new EntryInfo();
            ei.setId(id);
            ei.setTimeLimit(700);
            ei.addDataId("my data id");
            ce.setValue("hello world:" + i);
            ce.copyMetaData(ei);
            ServerCache.cache.setEntry(ce);
            ce = (CacheEntry) ServerCache.cache.getEntry(id);
            assertNotNull("cache entry was null after set", ce);
        }
        //invalidate each id
        for (int i = 0; i < 100; i++) {
            String id = "test:" + i;
            ServerCache.cache.invalidateById(id, false);
        }
        ServerCache.cache.invalidateById("force a flush of batchupdatedaemon", true);
        //make sure entries are null
        for (int i = 0; i < 100; i++) {
            String id = "test:" + i;
            CacheEntry ce = (CacheEntry) ServerCache.cache.getEntry(id);
            assertNull("cache not null after timeout", ce);
        }
    }

    @Test
    public void testGroupInvalidation() throws Exception {
        ServerCache.cache.clear();
        //set some entries
        for (int i = 0; i < 100; i++) {
            String id = "test:" + i;
            CacheEntry ce = new CacheEntry();
            EntryInfo ei = new EntryInfo();
            ei.setId(id);
            ei.setTimeLimit(700);
            ei.addDataId("my data id");
            ce.setValue("hello world:" + i);
            ce.copyMetaData(ei);
            ServerCache.cache.setEntry(ce);
            ce = (CacheEntry) ServerCache.cache.getEntry(id);
            assertNotNull("cache entry was null after set", ce);
        }
        //invalidate group id
        ServerCache.cache.invalidateById("my data id", true);
        try {
            Thread.sleep(2000);
        } catch (Exception ex) {
        }

        //make sure entries are null
        for (int i = 0; i < 100; i++) {
            String id = "test:" + i;
            CacheEntry ce = (CacheEntry) ServerCache.cache.getEntry(id);
            assertNull("cache not null after timeout", ce);
        }
    }
}
