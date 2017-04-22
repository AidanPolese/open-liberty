// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import test.common.SharedOutputManager;

import com.ibm.websphere.cache.CacheEntry;
import com.ibm.websphere.cache.DistributedNioMap;

public class ApiDistributedNioMapTest {

    CacheConfig cacheConfig = null;

    boolean runNow = true;
    int syncPoint = 0;
    float batchInvalidateErrors = 0;
    float batchInvalidateCounter = 0;
    int setGetFailures = 0;

    SharedOutputManager outputMgr = SharedOutputManager.getInstance().trace(StandaloneCache.TRACE_STRING_FULL);

    @Rule
    public TestRule testOutput = outputMgr;

    @Before
    public void setUp() throws Exception {
        System.out.println("ApiTestDistributedNioMap > Entry");
        cacheConfig = new CacheConfig();
        cacheConfig.cacheName = DCacheBase.DEFAULT_DISTRIBUTED_MAP_NAME;
        cacheConfig.setMaxCacheSize(10);
        cacheConfig.setEnableNioSupport(true);
        StandaloneCache.initialize(cacheConfig, "ApiDistributedNioMapTest");
        System.out.println("ApiTestDistributedNioMap > Exit");
    }

    @After
    public void tearDown() {
        ((CacheServiceImpl) ServerCache.getCacheService()).stop();
    }

    //------------------------------------------------------------
    @Test
    public void testApiDistributedNioMap() throws Exception {
        final String methodName = "testApiDistributedNioMap()";
        System.out.println(methodName + " - Start");
        DistributedNioMap nioMap = StandaloneCache.getNioMap(cacheConfig.cacheName);
        DistributedNioMap nioMap1 = nioMap;

        Object key = new Object();
        Object value = new Object();
        Object userMetaData = new Object();
        int priority = 1;
        int timeToLive = -1;
        int sharingPolicy = EntryInfo.NOT_SHARED;
        Object dependencyIds[] = new Object[3];
        dependencyIds[0] = new Object();
        dependencyIds[1] = new Object();
        dependencyIds[2] = new Object();
        Object aliasArray[] = new Object[3];
        aliasArray[0] = new Object();
        aliasArray[1] = new Object();
        aliasArray[2] = new Object();

        Object alias = aliasArray[1];

        nioMap.clear();
        nioMap.equals(nioMap);
        nioMap.getCacheEntry(key);
        nioMap.getClass();
        nioMap.hashCode();
        nioMap.invalidate(key);
        nioMap.invalidate(key, true);
        nioMap.invalidate(key, false);

        sleep(2000);

        nioMap.put(key, value, userMetaData, priority, timeToLive, sharingPolicy, dependencyIds, aliasArray);
        nioMap.addAlias(key, aliasArray);
        nioMap.removeAlias(alias);
        nioMap.releaseLruEntries(10);

        nioMap1.clear();
        nioMap1.equals(nioMap);
        nioMap1.getCacheEntry(key);
        nioMap1.getClass();
        nioMap1.hashCode();
        nioMap1.invalidate(key);
        nioMap1.invalidate(key, true);
        nioMap1.invalidate(key, false);

        sleep(2000);

        nioMap1.put(key, value, userMetaData, priority, timeToLive, sharingPolicy, dependencyIds, aliasArray);
        nioMap1.putAndGet(key, value, userMetaData, priority, timeToLive, sharingPolicy, dependencyIds, aliasArray);
        nioMap1.addAlias(key, aliasArray);
        nioMap1.releaseLruEntries(10);
        nioMap1.removeAlias(alias);

        sleep(2000);

        System.out.println(methodName + " - Finish");
    }

    @Test
    public void testInvalidationListeners() throws Exception {

        final String methodName = "com.ibm.ws.cache.ApiTestDistributedNioMap.testInvalidationListeners()";
        System.out.println("\n" + methodName + " begin");
        DistributedNioMap map = StandaloneCache.getNioMap(cacheConfig.cacheName);

        map.clear();
        map.enableListener(true);
        MyEventListenerImpl listener1 = new MyEventListenerImpl("Listener1", 5);
        map.addInvalidationListener(listener1);
        listener1.setTypeOfEvent(1);
        MyEventListenerImpl listener2 = new MyEventListenerImpl("Listener2", 5);
        map.addInvalidationListener(listener2);
        listener2.setTypeOfEvent(1);

        //set some entries
        for (int i = 0; i < 10; i++) {
            String id = "c_id_" + i;
            String data = "this is a test value:" + i;
            if (i == 1 || i == 3) {
                map.put(id,
                        data,
                        null,
                        3, //priority
                        0, //no timeout
                        EntryInfo.NOT_SHARED,
                        new String[] { "dep_id_1", "dep_id_2" },
                        null);
            } else if (i == 2) {
                map.put(id, data, null,
                        3, //priority
                        0, //no timeout
                        EntryInfo.NOT_SHARED,
                        new String[] { "dep_id_1" },
                        null);
            } else if (i == 4 || i == 5 || i == 6) {
                map.put(id, data, null,
                        3, //priority
                        0, //no timeout
                        EntryInfo.NOT_SHARED,
                        new String[] { "dep_id_2" },
                        null);
            } else if (i == 8 || i == 9) {
                map.put(id, data, null,
                        3, //priority
                        0, //timeout = 0
                        EntryInfo.NOT_SHARED,
                        new String[] { "dep_id_3" },
                        null);
            } else // i= 0, 7, 10, 2000
            {
                map.put(id, data, null,
                        3, //priority
                        0, //no timeout
                        EntryInfo.NOT_SHARED,
                        new String[] { "dep_id_4" },
                        null);
            }

            CacheEntry ce = map.getCacheEntry(id);
            String newData = (String) ce.getValue();
            ce.finish();
            assertEquals("cache entry not equal after set", data, newData);
        }
        //invalidate the dep id
        System.out.println("*** Invalidate dep_id_1");
        map.invalidate("dep_id_1");
        listener1.waitOnCompletion();
        listener2.waitOnCompletion();

        for (int i = 0; i < 10; i++) {
            String id = "c_id_" + i;
            if (i == 1 || i == 2 || i == 3) {
                String newData = null;
                CacheEntry ce = map.getCacheEntry(id);
                if (null != ce) {
                    newData = (String) ce.getValue();
                    ce.finish();
                }
                assertNull("cache entry not null after invalidation - " + id, newData);
            }
        }
        String rs = listener1.compare(new InvalidationListenerInfo("c_id_1", "this is a test value:1", com.ibm.websphere.cache.InvalidationEvent.EXPLICIT,
                        com.ibm.websphere.cache.InvalidationEvent.LOCAL, Cache.DEFAULT_DISTRIBUTED_MAP_NAME), 3);
        assertEquals("cache invalidation events not equal", "", rs);
        rs = listener1.compare(new InvalidationListenerInfo("c_id_2", "this is a test value:2", com.ibm.websphere.cache.InvalidationEvent.EXPLICIT,
                        com.ibm.websphere.cache.InvalidationEvent.LOCAL, Cache.DEFAULT_DISTRIBUTED_MAP_NAME), 3);
        assertEquals("cache invalidation events not equal", "", rs);
        rs = listener1.compare(new InvalidationListenerInfo("c_id_3", "this is a test value:3", com.ibm.websphere.cache.InvalidationEvent.EXPLICIT,
                        com.ibm.websphere.cache.InvalidationEvent.LOCAL, Cache.DEFAULT_DISTRIBUTED_MAP_NAME), 3);
        assertEquals("cache invalidation events not equal", "", rs);
        rs = listener2.compare(new InvalidationListenerInfo("c_id_1", "this is a test value:1", com.ibm.websphere.cache.InvalidationEvent.EXPLICIT,
                        com.ibm.websphere.cache.InvalidationEvent.LOCAL, Cache.DEFAULT_DISTRIBUTED_MAP_NAME), 3);
        assertEquals("cache invalidation events not equal", "", rs);
        rs = listener2.compare(new InvalidationListenerInfo("c_id_2", "this is a test value:2", com.ibm.websphere.cache.InvalidationEvent.EXPLICIT,
                        com.ibm.websphere.cache.InvalidationEvent.LOCAL, Cache.DEFAULT_DISTRIBUTED_MAP_NAME), 3);
        assertEquals("cache invalidation events not equal", "", rs);
        rs = listener2.compare(new InvalidationListenerInfo("c_id_3", "this is a test value:3", com.ibm.websphere.cache.InvalidationEvent.EXPLICIT,
                        com.ibm.websphere.cache.InvalidationEvent.LOCAL, Cache.DEFAULT_DISTRIBUTED_MAP_NAME), 3);
        assertEquals("cache invalidation events not equal", "", rs);

        listener1.restart(4);
        listener2.restart(4);
        System.out.println("*** Invalidate c_id_4, c_id_5, c_id_6, c_id_7");
        map.invalidate("c_id_4");
        map.invalidate("c_id_5");
        map.invalidate("c_id_6");
        map.invalidate("c_id_7");
        listener1.waitOnCompletion();
        listener2.waitOnCompletion();
        for (int i = 4; i < 8; i++) {
            String id = "c_id_" + i;
            String newData = null;
            CacheEntry ce = map.getCacheEntry(id);
            if (null != ce) {
                newData = (String) ce.getValue();
                ce.finish();
            }
            assertNull("cache entry not null after invalidation - " + id, newData);
        }
        rs = listener1.compare(new InvalidationListenerInfo("c_id_4", "this is a test value:4", com.ibm.websphere.cache.InvalidationEvent.EXPLICIT,
                        com.ibm.websphere.cache.InvalidationEvent.LOCAL, Cache.DEFAULT_DISTRIBUTED_MAP_NAME), 4);
        assertEquals("cache invalidation events not equal", "", rs);
        rs = listener1.compare(new InvalidationListenerInfo("c_id_5", "this is a test value:5", com.ibm.websphere.cache.InvalidationEvent.EXPLICIT,
                        com.ibm.websphere.cache.InvalidationEvent.LOCAL, Cache.DEFAULT_DISTRIBUTED_MAP_NAME), 4);
        assertEquals("cache invalidation events not equal", "", rs);
        rs = listener2.compare(new InvalidationListenerInfo("c_id_4", "this is a test value:4", com.ibm.websphere.cache.InvalidationEvent.EXPLICIT,
                        com.ibm.websphere.cache.InvalidationEvent.LOCAL, Cache.DEFAULT_DISTRIBUTED_MAP_NAME), 4);
        assertEquals("cache invalidation events not equal", "", rs);
        rs = listener2.compare(new InvalidationListenerInfo("c_id_5", "this is a test value:5", com.ibm.websphere.cache.InvalidationEvent.EXPLICIT,
                        com.ibm.websphere.cache.InvalidationEvent.LOCAL, Cache.DEFAULT_DISTRIBUTED_MAP_NAME), 4);
        assertEquals("cache invalidation events not equal", "", rs);
        rs = listener1.compare(new InvalidationListenerInfo("c_id_6", "this is a test value:6", com.ibm.websphere.cache.InvalidationEvent.EXPLICIT,
                        com.ibm.websphere.cache.InvalidationEvent.LOCAL, Cache.DEFAULT_DISTRIBUTED_MAP_NAME), 4);
        assertEquals("cache invalidation events not equal", "", rs);
        rs = listener1.compare(new InvalidationListenerInfo("c_id_7", "this is a test value:7", com.ibm.websphere.cache.InvalidationEvent.EXPLICIT,
                        com.ibm.websphere.cache.InvalidationEvent.LOCAL, Cache.DEFAULT_DISTRIBUTED_MAP_NAME), 4);
        assertEquals("cache invalidation events not equal", "", rs);
        rs = listener2.compare(new InvalidationListenerInfo("c_id_6", "this is a test value:6", com.ibm.websphere.cache.InvalidationEvent.EXPLICIT,
                        com.ibm.websphere.cache.InvalidationEvent.LOCAL, Cache.DEFAULT_DISTRIBUTED_MAP_NAME), 4);
        assertEquals("cache invalidation events not equal", "", rs);
        rs = listener2.compare(new InvalidationListenerInfo("c_id_7", "this is a test value:7", com.ibm.websphere.cache.InvalidationEvent.EXPLICIT,
                        com.ibm.websphere.cache.InvalidationEvent.LOCAL, Cache.DEFAULT_DISTRIBUTED_MAP_NAME), 4);
        assertEquals("cache invalidation events not equal", "", rs);

        System.out.println(" Overflow cacheSize .... current size " + map.size(false));
        listener1.restart(2);
        listener1.setTypeOfEvent(2);
        listener2.restart(2);
        listener2.setTypeOfEvent(2);

        for (int i = 2000; i < 2011; i++) {
            String id = "c_id_" + i;
            String data = "this is a test value:" + i;
            map.put(id, data,
                    null,
                    3, //priority
                    0, //no timeout
                    EntryInfo.NOT_SHARED,
                    new String[] { "dep_id_4" },
                    null);
        }
        listener1.waitOnCompletion();
        listener2.waitOnCompletion();
        rs = listener1.compare(new InvalidationListenerInfo("c_id_0", "this is a test value:0", com.ibm.websphere.cache.InvalidationEvent.LRU,
                        com.ibm.websphere.cache.InvalidationEvent.LOCAL, Cache.DEFAULT_DISTRIBUTED_MAP_NAME), 4);
        assertEquals("cache invalidation events not equal", "", rs);
        rs = listener1.compare(new InvalidationListenerInfo("c_id_8", "this is a test value:8", com.ibm.websphere.cache.InvalidationEvent.LRU,
                        com.ibm.websphere.cache.InvalidationEvent.LOCAL, Cache.DEFAULT_DISTRIBUTED_MAP_NAME), 4);
        assertEquals("cache invalidation events not equal", "", rs);
        rs = listener1.compare(new InvalidationListenerInfo("c_id_9", "this is a test value:9", com.ibm.websphere.cache.InvalidationEvent.LRU,
                        com.ibm.websphere.cache.InvalidationEvent.LOCAL, Cache.DEFAULT_DISTRIBUTED_MAP_NAME), 4);
        assertEquals("cache invalidation events not equal", "", rs);
        rs = listener1.compare(new InvalidationListenerInfo("c_id_2000", "this is a test value:2000", com.ibm.websphere.cache.InvalidationEvent.LRU,
                        com.ibm.websphere.cache.InvalidationEvent.LOCAL, Cache.DEFAULT_DISTRIBUTED_MAP_NAME), 4);
        assertEquals("cache invalidation events not equal", "", rs);

        System.out.println("*** clear");
        listener1.restart(1);
        listener2.restart(1);
        listener1.setTypeOfEvent(5);
        listener2.setTypeOfEvent(5);

        map.clear();
        listener1.waitOnCompletion();
        listener2.waitOnCompletion();

        rs = listener1.compare(new InvalidationListenerInfo("*", null, com.ibm.websphere.cache.InvalidationEvent.CLEAR_ALL, com.ibm.websphere.cache.InvalidationEvent.LOCAL,
                        Cache.DEFAULT_DISTRIBUTED_MAP_NAME), 1);
        assertEquals("cache invalidation events not equal", "", rs);
        rs = listener2.compare(new InvalidationListenerInfo("*", null, com.ibm.websphere.cache.InvalidationEvent.CLEAR_ALL, com.ibm.websphere.cache.InvalidationEvent.LOCAL,
                        Cache.DEFAULT_DISTRIBUTED_MAP_NAME), 1);
        assertEquals("cache invalidation events not equal", "", rs);
        map.removeInvalidationListener(listener1);
        map.removeInvalidationListener(listener2);
        map.enableListener(false);
        System.out.println(methodName + " end");
    }

    private void sleep(long delay) {
        try {
            Thread.sleep(delay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
