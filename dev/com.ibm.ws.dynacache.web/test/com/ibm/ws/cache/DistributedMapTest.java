// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import test.common.SharedOutputManager;

import com.ibm.websphere.cache.DistributedMap;

public class DistributedMapTest {

    String className = "TestDistributedMap";

    SharedOutputManager outputMgr = SharedOutputManager.getInstance().trace(StandaloneCache.TRACE_STRING);

    @Rule
    public TestRule testOutput = outputMgr;

    @Before
    public void setUp() throws Exception {
        StandaloneCache.initialize("DistributedMapTest");
    }

    @After
    public void tearDown() {
        ((CacheServiceImpl) ServerCache.getCacheService()).stop();
    }

    public DistributedMap getMap() {
        return StandaloneCache.getMap();
    }

    @Test
    public void testSize() {
        final String methodName = className + ".testSize()";
        System.out.println("\n" + methodName + " begin");
        DistributedMap map = getMap();
        map.clear();
        Assert.assertTrue("size not zero after clear", map.size() == 0);
        map.put("one", "one");
        map.put("two", "two");
        map.put("three", "three");
        assertTrue("size does not match entries in cache", map.size() == 3);
        System.out.println(methodName + " end");
    }

    @Test
    public void testClear() {
        final String methodName = className + ".testClear()";
        System.out.println("\n" + methodName + " begin");
        DistributedMap map = getMap();
        map.clear();
        //set some entries
        for (int i = 0; i < 500; i++) {
            String id = "test:" + i;
            String data = "this is a test value:" + i;
            map.put(id, data,
                    3, //priority
                    10, //timeout = 10 seconds
                    EntryInfo.NOT_SHARED,
                    new String[] { "dependency id" });
            String newData = (String) map.get(id);
            assertEquals("cache entry not equal after set", data, newData);
        }
        //clear the cache
        map.clear();
        //make sure entries are null
        for (int i = 0; i < 500; i++) {
            String id = "test:" + i;
            String newData = (String) map.get(id);
            assertNull("cache entry not null after clear", newData);
        }
        System.out.println(methodName + " end");
    }

    @Test
    public void testDRSBootstrap() {
        final String methodName = className + ".testDRSBootstrap()";
        System.out.println("\n" + methodName + " begin");
        DistributedMap map = getMap();
        map.setDRSBootstrap(true);
        boolean enable = map.isDRSBootstrapEnabled();
        assertTrue("setDRSBootStrap to true", enable == true);
        map.setDRSBootstrap(false);
        enable = map.isDRSBootstrapEnabled();
        assertTrue("setDRSBootStrap to false", enable == false);
        System.out.println(methodName + " end");
    }

    @Test
    public void testPutGet() throws Exception {
        final String methodName = className + ".testPutGet()";
        System.out.println("\n" + methodName + " begin");
        DistributedMap map = getMap();
        map.clear();
        for (int i = 0; i < 10000; i++) {
            String id = "test:" + i;
            Object oldValue = map.get(id);
            assertNull("cache entry not null before set", oldValue);
            String data = "this is a test value:" + i;
            map.put(id, data);
            String newData = (String) map.get(id);
            assertEquals("cache entry not equal after set", data, newData);
        }
        System.out.println(methodName + " end");
    }

    @Test
    public void testObjectKey() throws Exception {
        final String methodName = className + ".testObjectKey";
        System.out.println("\n" + methodName + " begin");
        DistributedMap map = getMap();
        map.clear();
        // test putGet
        for (int i = 0; i < 2000; i++) {
            String id = "test:" + i;
            Object oid = new Thread(id);
            Object oldValue = map.get(oid);
            assertNull("cache entry not null before set", oldValue);
            String data = "this is a test value:" + i;
            map.put(oid, data);
            String newData = (String) map.get(oid);
            if (!data.equals(newData)) {
                Thread.sleep(1000);
                newData = (String) map.get(oid);
            }
            assertEquals("cache entry not equal after set", data, newData);
        }
        // test dependency id
        for (int i = 0; i < 2000; i++) {
            //Object iodd = new Thread("objectDepIdTest"+i);
            Object iodd = new MySerializableObjectKey("objectDepIdTest" + i, true);
            String data2 = "this is a test value for ObjectDepIdTest";
            map.put(iodd, data2,
                    3, //priority
                    10, //timeout = 10 seconds
                    EntryInfo.NOT_SHARED,
                    new Object[] { "dependency id" });
            String newData2 = (String) map.get(iodd);
            assertEquals("cache entry not equal after set", data2, newData2);
            map.invalidate(iodd);
            String newData21 = (String) map.get(iodd);
            // The update daemon may not have fired instantaneously
            if (newData21 != null) {
                Thread.sleep(1000);
                newData21 = (String) map.get(iodd);
            }
            assertNull("cache entry not null after invalidation no. " + i, newData21);
        }
        // test invalidateByTemplate ( or clear)
        for (int i = 0; i < 2000; i++) {
            String id3 = "test:" + i;
            Object oid3 = new Thread(id3);
            Object oldValue3 = map.get(oid3);
            assertNull("cache entry not null before set", oldValue3);
            String data3 = "this is a test value:" + i;
            map.put(oid3, data3);
            String newData3 = (String) map.get(oid3);
            assertEquals("cache entry not equal after set", data3, newData3);
            map.clear();
            String newData31 = (String) map.get(oid3);
            // The update daemon may not have fired instantaneously
            if (newData31 != null) {
                Thread.sleep(1000);
                newData31 = (String) map.get(oid3);
            }
            assertNull("cache entry not null after clear no. " + i, newData31);
        }
        System.out.println(methodName + " end");
    }

    @Test
    public void testPutReplace() throws Exception {
        final String methodName = className + ".testPutReplace";
        System.out.println("\n" + methodName + " begin");
        DistributedMap map = getMap();
        map.clear();
        for (int i = 0; i < 100; i++) {
            String id = "test:" + i;
            Object oldValue = map.get(id);
            assertNull("cache entry not null before set", oldValue);
            String data = "this is a test value:" + i;
            map.put(id, data);
            String newData = (String) map.get(id);
            assertEquals("cache entry not equal after set", data, newData);
        }
        for (int i = 0; i < 100; i++) {
            String id = "test:" + i;
            String data = "This is a different value to replace previous:" + i;
            map.put(id, data);
            String newData = (String) map.get(id);
            assertEquals("cache entry not equal after replace", data, newData);
        }
        System.out.println(methodName + " end");
    }

    @Test
    public void testTimeout() throws Exception {
        final String methodName = className + ".testTimeout()";
        System.out.println("\n" + methodName + " begin");
        DistributedMap map = getMap();
        map.clear();
        //set some entries
        for (int i = 0; i < 500; i++) {
            String id = "test:" + i;
            String data = "this is a test value:" + i;
            map.put(id, data,
                    3, //priority
                    10, //timeout = 10 seconds
                    EntryInfo.NOT_SHARED,
                    new String[] { "dependency id" });
            String newData = (String) map.get(id);
            assertEquals("testTimeout.1: cache entry not equal after set", data, newData);
        }
        //wait for timeout
        try {
            Thread.sleep(22000);
        } catch (Exception ex) {
        }
        //make sure entries are null
        for (int i = 0; i < 500; i++) {
            String id = "test:" + i;
            String newData = (String) map.get(id);
            assertNull("testTimeout.2: cache entry " + id + " not null after timeout", newData);
        }
        map.setTimeToLive(3); // set globalTimeToLive to 3 sec timeout
        map.setSharingPolicy(EntryInfo.NOT_SHARED);
        //set some entries
        for (int i = 0; i < 100; i++) {
            String id = "test:" + i;
            String data = "this is a test value:" + i;
            map.put(id, data);
            String newData = (String) map.get(id);
            assertEquals("testTimeout.3: cache entry not equal after set", data, newData);
        }
        //wait for timeout
        try {
            Thread.sleep(20000);
        } catch (Exception ex) {
        }
        //make sure entries are null
        for (int i = 0; i < 100; i++) {
            String id = "test:" + i;
            String newData = (String) map.get(id);
            assertNull("testTimeout.4: cache entry " + id + " not null after timeout", newData);
        }
        map.setTimeToLive(-1); // set globalTimeToLive to no timeout
        //set some entries
        for (int i = 0; i < 100; i++) {
            String id = "test:" + i;
            String data = "this is a test value:" + i;
            map.put(id, data);
            String newData = (String) map.get(id);
            assertEquals("testTimeout.5: cache entry not equal after set", data, newData);
        }
        //wait for timeout
        try {
            Thread.sleep(20000);
        } catch (Exception ex) {
        }
        //make sure entries are not null
        for (int i = 0; i < 100; i++) {
            String id = "test:" + i;
            String data = "this is a test value:" + i;
            String newData = (String) map.get(id);
            assertEquals("testTimeout.6: cache entry not equal after waiting 20 sec", data, newData);
        }
        System.out.println(methodName + " end");
    }

    @Test
    public void testEntryInvalidation() throws Exception {
        final String methodName = className + ".testEntryInvalidation()";
        System.out.println("\n" + methodName + " begin");
        DistributedMap map = getMap();
        map.clear();
        //set some entries
        for (int i = 0; i < 500; i++) {
            String id = "test:" + i;
            String data = "this is a test value:" + i;
            map.put(id, data,
                    3, //priority
                    600, //timeout = 600 seconds
                    EntryInfo.NOT_SHARED,
                    new String[] { "dependency id" });
            String newData = (String) map.get(id);
            assertEquals("cache entry not equal after set", data, newData);
        }
        //invalidate each id
        for (int i = 0; i < 500; i++) {
            String id = "test:" + i;
            map.invalidate(id);
        }
        //make sure entries are null
        for (int i = 0; i < 500; i++) {
            String id = "test:" + i;
            String newData = (String) map.get(id);
            assertNull("cache entry not null after invalidation", newData);
        }
        System.out.println(methodName + " end");
    }

    @Test
    public void testGroupInvalidation() throws Exception {
        final String methodName = className + ".testGroupInvalidation()";
        System.out.println("\n" + methodName + " begin");
        DistributedMap map = getMap();
        map.clear();
        //set some entries
        for (int i = 0; i < 500; i++) {
            String id = "test:" + i;
            String data = "this is a test value:" + i;
            map.put(id, data,
                    3, //priority
                    600, //timeout = 600 seconds
                    EntryInfo.NOT_SHARED,
                    new String[] { "dependency id" });
            String newData = (String) map.get(id);
            assertEquals("cache entry not equal after set", data, newData);
        }
        //invalidate the group id
        map.invalidate("dependency id");
        //make sure entries are null
        for (int i = 0; i < 500; i++) {
            String id = "test:" + i;
            String newData = (String) map.get(id);
            assertNull("cache entry not null after invalidation", newData);
        }
        System.out.println(methodName + " end");
    }

    @Test
    public void testKeySet() throws Exception {
        final String methodName = className + ".testKeySet()";
        System.out.println("\n" + methodName + " begin");
        DistributedMap map = getMap();
        map.clear();
        //set some entries
        for (int i = 0; i < 500; i++) {
            String id = "test:" + i;
            String data = "this is a test value:" + i;
            map.put(id, data);
            String newData = (String) map.get(id);
            assertEquals("cache entry not equal after set", data, newData);
        }
        Set keySet = map.keySet();
        //make sure entries are in key set
        for (int i = 0; i < 500; i++) {
            String id = "test:" + i;
            assertTrue("key not found in key set", keySet.contains(id));
        }
        System.out.println(methodName + " end");
    }

    @Test
    public void testValuesCollection() throws Exception {
        final String methodName = className + ".testValuesCollection()";
        System.out.println("\n" + methodName + " begin");
        DistributedMap map = getMap();
        map.clear();
        //set some entries
        for (int i = 0; i < 500; i++) {
            String id = "test:" + i;
            String data = "this is a test value:" + i;
            map.put(id, data);
            String newData = (String) map.get(id);
            assertEquals("cache entry not equal after set", data, newData);
        }
        Collection values = map.values();
        //make sure values are in the collection
        for (int i = 0; i < 500; i++) {
            String data = "this is a test value:" + i;
            assertTrue("value not found in value set", values.contains(data));
        }
        System.out.println(methodName + " end");
    }

    @Test
    public void testContainsKey() throws Exception {
        final String methodName = className + ".testContainsKey()";
        System.out.println("\n" + methodName + " begin");
        DistributedMap map = getMap();
        map.clear();
        //set some entries
        for (int i = 0; i < 500; i++) {
            String id = "test:" + i;
            String data = "this is a test value:" + i;
            map.put(id, data);
            String newData = (String) map.get(id);
            assertEquals("cache entry not equal after set", data, newData);
        }
        //make sure entries are in the cache
        for (int i = 0; i < 500; i++) {
            String id = "test:" + i;
            assertTrue("key not found in map", map.containsKey(id));
        }
        System.out.println(methodName + " end");
    }

    @Test
    public void testContainsValue() throws Exception {
        final String methodName = className + ".testContainsValue()";
        System.out.println("\n" + methodName + " begin");
        DistributedMap map = getMap();
        map.clear();
        //set some entries
        for (int i = 0; i < 500; i++) {
            String id = "test:" + i;
            String data = "this is a test value:" + i;
            map.put(id, data);
            String newData = (String) map.get(id);
            assertEquals("cache entry not equal after set", data, newData);
        }
        //make sure values are in the map
        for (int i = 0; i < 500; i++) {
            String data = "this is a test value:" + i;
            assertTrue("value not found in map:" + data, map.containsValue(data));
        }
        System.out.println(methodName + " end");
    }

    @Test
    public void testPutAll() throws Exception {
        final String methodName = className + ".testPutAll()";
        System.out.println("\n" + methodName + " begin");
        DistributedMap map = getMap();
        map.clear();
        HashMap sampleMap = new HashMap();
        sampleMap.put("one", "val one");
        sampleMap.put("two", "val two");
        sampleMap.put("three", "val three");
        map.putAll(sampleMap);
        assertTrue("entry one not found", map.get("one").equals("val one"));
        assertTrue("entry two not found", map.get("two").equals("val two"));
        assertTrue("entry three not found", map.get("three").equals("val three"));
        System.out.println(methodName + " end");
    }

    @Test
    public void testIsEmpty() throws Exception {
        final String methodName = className + ".testIsEmpty()";
        System.out.println("\n" + methodName + " begin");
        DistributedMap map = getMap();
        map.clear();
        assertTrue("map was not empty", map.isEmpty());
        System.out.println(methodName + " end");
    }

    @Test
    public void testPutInvalidationInfo() throws Exception {
        final String methodName = className + ".testPutInvalidationInfo()";
        System.out.println("\n" + methodName + " begin");
        DistributedMap map = getMap();
        map.clear();
        map.put("one", "value1",
                1, //priority
                0, //timeout
                EntryInfo.NOT_SHARED,
                new String[] { "depId1", "depId2", "depId3" });
        map.put("one", "value2",
                1, //priority
                0, //timeout
                EntryInfo.NOT_SHARED,
                new String[] { "depId1", "depId2" });
        map.invalidate("depId3");
        String newData = (String) map.get("one");
        assertEquals("cache entry remove after inavalidation by depId", "value2", newData);
        System.out.println(methodName + " end");
    }

    @Test
    public void testAlias() throws Exception {
        final String methodName = className + ".testAlias()";
        System.out.println("\n" + methodName + " begin");
        DistributedMap map = getMap();
        map.clear();
        map.put("one", "value1",
                1, //priority
                0, //timeout
                EntryInfo.NOT_SHARED,
                new String[] { "depId1", "depId2" });
        map.addAlias("one", new String[] { "aliasOne", "aliasTwo" });
        String value = (String) map.get("aliasOne");
        assertEquals("get aliasOne", "value1", value);
        value = (String) map.get("aliasTwo");
        assertEquals("get aliasTwo", "value1", value);
        map.removeAlias("aliasOne");
        value = (String) map.get("aliasOne");
        assertNull("value not null after remove aliasOne", value);
        value = (String) map.get("aliasTwo");
        assertEquals("get aliasTwo", "value1", value);
        map.removeAlias("aliasTwo");
        value = (String) map.get("aliasOne");
        assertNull("value not null after remove aliasTwo", value);
        value = (String) map.get("one");
        assertEquals("get id - one", "value1", value);
        System.out.println(methodName + " end");
    }

    @Test
    public void testMultithreadedBig() throws Exception {
        final String methodName = className + ".testMultithreadedBig()";
        System.out.println("\n" + methodName + " begin");
        DistributedMap map = getMap();
        map.clear();
        TesterThread threads[] = new TesterThread[5];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new TesterThread(map, "thread:" + i, 1, 10000, 0);
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        System.out.println(methodName + " end");
    }

    @Test
    public void testMultithreadedVerify() throws Exception {
        final String methodName = className + ".testMultithreadedVerify()";
        System.out.println("\n" + methodName + " begin");
        _multithreadVerify(getMap());
        System.out.println(methodName + " end");
    }

    // current performance target is 1.9.  we should decrease this
    // as much as possible.  this must be updated as we make
    // performance improvements.

    static final double targetRatio = 1.9;

    @Test
    public void testMutltiThreadPerformance() throws Exception {

        final String methodName = className + ".testMutltiThreadPerformance()";
        System.out.println("\n" + methodName + " begin");

        // WARM up for the test... let the JIT do its job...
        _multithreadVerify(Collections.synchronizedMap(new HashMap()));
        _multithreadVerify(getMap());

        // test a standard synchronized map
        long start1 = System.currentTimeMillis();
        _multithreadVerify(Collections.synchronizedMap(new HashMap()));
        long end1 = System.currentTimeMillis();

        // test our map
        long start2 = System.currentTimeMillis();
        _multithreadVerify(getMap());
        long end2 = System.currentTimeMillis();

        //compare
        System.err.println("\n-----------------------");
        System.err.println("performance validation");
        System.err.println("-----------------------");

        //display
        System.err.println("standard synchronized map : " + (end1 - start1) + " ms");
        System.err.println("distributedmap            : " + (end2 - start2) + " ms");
        double ratio = ((double) (end2 - start2)) / ((double) (end1 - start1));
        System.err.println("performance ratio: " + ratio);
        System.err.println("target ratio: " + targetRatio);
        System.out.println(methodName + " end");
    }

    // utility function used to stress a Map
    private void _multithreadVerify(Map map) throws Exception {
        map.clear();
        TesterThread threads[] = new TesterThread[5];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new TesterThread(map, "thread:" + i, 25, 100, 10);
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
    }

    class TesterThread extends Thread {
        String name;
        int items;
        Map map;
        int getIterations;
        int loops;

        TesterThread(Map map, String name, int loops, int items, int getIterations) {
            this.map = map;
            this.name = name;
            this.items = items;
            this.getIterations = getIterations;
            this.loops = loops;
        }

        @Override
        public void run() {
            for (int k = 0; k < loops; k++) {
                for (int i = 0; i < items; i++) {
                    String id = name + ":" + i;
                    String data = name + ": this is a test value:" + i;
                    map.put(id, data);
                }
                for (int j = 0; j < getIterations; j++) {
                    for (int i = 0; i < items; i++) {
                        String id = name + ":" + i;
                        String data = name + ": this is a test value:" + i;
                        String newData = (String) map.get(id);
                        assertEquals("cache entry not equal after set", data, newData);
                    }
                }
            }
        }
    }

}
