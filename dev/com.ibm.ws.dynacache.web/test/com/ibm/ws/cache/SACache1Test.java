// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.io.File;
import java.io.Serializable;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import test.common.SharedOutputManager;

import com.ibm.ws.cache.spi.DistributedMapFactory;
import com.ibm.wsspi.cache.DistributedObjectCacheFactory;

public class SACache1Test {
    String className = null;

    CacheConfig cacheConfig = null;

    boolean runNow = true;
    int syncPoint = 0;
    float batchInvalidateErrors = 0;
    float batchInvalidateCounter = 0;
    int setGetFailures = 0;

    SharedOutputManager outputMgr = SharedOutputManager.getInstance().trace(StandaloneCache.TRACE_STRING);

    @Rule
    public TestRule testOutput = outputMgr;

    @Before
    public void setUp() throws Exception {
        cacheConfig = new CacheConfig();
        cacheConfig.cacheName = DCacheBase.DEFAULT_CACHE_NAME;
        cacheConfig.setEnableDiskOffload(true);
        cacheConfig.setDiskOffloadLocation("." + File.separator + "dynacache");
        cacheConfig.setMaxCacheSize(500);
        StandaloneCache.initialize(cacheConfig, "SACache1Test");
    }

    @After
    public void tearDown() {
        ((CacheServiceImpl) ServerCache.getCacheService()).stop();
    }

    @Test
    public void testSerialNonSerialObject() throws Exception {
        final String methodName = className + ".testSerialNonSerialObject()";
        System.out.println("\n" + methodName + " begin");

        if (!cacheConfig.isEnableDiskOffload()) {
            System.out
                            .println("running: testSerialNonSerial() - Warning: Disk offload is not enabled");
        }

        Object objectIn = new Object();

        int entryCount = cacheConfig.getCacheSize() + 100; //force LRU

        //-----------------------------------------
        // Add entries into Cache
        //-----------------------------------------
        ServerCache.cache.clear();
        for (int i = 0; i != entryCount; i++) {
            String id = "test:" + i;
            CacheEntry ce = new CacheEntry();
            EntryInfo ei = new EntryInfo();
            ei.setId(id);
            ei.addDataId("my data id");
            ce.setValue(objectIn);
            ce.copyMetaData(ei);
            ServerCache.cache.setEntry(ce);
        }
        //-----------------------------------------

        //-----------------------------------------
        //
        //-----------------------------------------
        for (int i = 0; i != entryCount; i++) {
            String id = "test:" + i;

            CacheEntry ce = (CacheEntry) ServerCache.cache.getEntry(id);
            if (null != ce) {
                Object object = ce.getValue();
                if (object instanceof Serializable) {
                    assertNotNull("testSerialNonSerial() - error - serializable object was returned", null);
                }
            }
        }
        //-----------------------------------------

        //-----------------------------------------
        // Add entries into Cache
        //-----------------------------------------
        objectIn = "mySerializableObject";
        ServerCache.cache.clear();
        for (int i = 0; i != entryCount; i++) {
            String id = "test:" + i;
            CacheEntry ce = new CacheEntry();
            EntryInfo ei = new EntryInfo();
            ei.setId(id);
            ei.addDataId("my data id");
            ce.setValue(objectIn);
            ce.copyMetaData(ei);
            ServerCache.cache.setEntry(ce);
        }
        //-----------------------------------------

        //-----------------------------------------
        //
        //-----------------------------------------
        for (int i = 0; i != entryCount; i++) {
            String id = "test:" + i;

            CacheEntry ce = (CacheEntry) ServerCache.cache.getEntry(id);
            assertNotNull("testSerialNonSerial - error - entry not found", ce);
            Object object = ce.getValue();
            if (!(object instanceof Serializable)) {
                assertNotNull("testSerialNonSerial() - error - serializable object was not returned", null);
            }
        }
        //-----------------------------------------

        ServerCache.cache.clear();

        System.out.println(methodName + " end");
    }

    @Test
    public void testFeatureDisableDependencyId() throws Exception {
        final String methodName = className + ".testFeatureDisableDependencyId()";
        System.out.println("\n" + methodName + " begin");

        DistributedMapImpl map_1 = null;
        DistributedMapImpl map_2 = null;
        String name_1 = "featueTestDisableDependencyId_enabled";
        String name_2 = "featueTestDisableDependencyId_disabled";

        // Configure normal DMap ( with dependencyIds enabled )
        map_1 = (DistributedMapImpl) DistributedMapFactory.getMap(name_1);

        String id = "id";
        String data = "data";
        String[] depIds = new String[] { "dependency id" };
        String result = null;

        map_1.put(id,
                  data,
                  3, //priority
                  10, //timeout = 10 seconds
                  EntryInfo.NOT_SHARED,
                  depIds);
        result = (String) map_1.get(id);
        assertNotNull(methodName + " - entry not found", result);

        map_1.invalidate(depIds[0]);

        result = (String) map_1.get(id);
        assertNull(methodName + " - entry still alive", result);

        // Configure DMap with dependencyIds disabled
        Properties props = new Properties();
        props.put(CacheConfig.DISABLE_DEPENDENCY_ID, DistributedObjectCacheFactory.VALUE_TRUE);
        map_2 = (DistributedMapImpl) DistributedMapFactory.getMap(name_2, props);

        map_2.put(id,
                  data,
                  3, //priority
                  10, //timeout = 10 seconds
                  EntryInfo.NOT_SHARED,
                  depIds);
        result = (String) map_2.get(id);

        assertNotNull(methodName + " - entry not found", result);

        map_2.invalidate(depIds[0]);

        result = (String) map_2.get(id);
        assertNotNull(methodName + " - entry not alive", result);

        System.out.println(methodName + " end");
    }
    //--------------------------------------------------------------
}
//------------------------------------------------------------

