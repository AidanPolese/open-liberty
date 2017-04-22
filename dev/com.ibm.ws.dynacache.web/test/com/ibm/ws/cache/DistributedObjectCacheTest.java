// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache;

import static junit.framework.Assert.assertTrue;

import java.util.Hashtable;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import test.common.SharedOutputManager;

import com.ibm.websphere.cache.DistributedMap;
import com.ibm.websphere.cache.DistributedNioMap;
import com.ibm.websphere.cache.DistributedObjectCache;

public class DistributedObjectCacheTest {

    SharedOutputManager outputMgr = SharedOutputManager.getInstance().trace(StandaloneCache.TRACE_STRING);

    @Rule
    public TestRule testOutput = outputMgr;

    @Before
    public void setUp() throws Exception {
        StandaloneCache.initialize("DistributedObjectCacheTest");
    }

    @After
    public void tearDown() {
        ((CacheServiceImpl) ServerCache.getCacheService()).stop();
    }

    @Test
    public void testDOC() throws Exception {
        System.out.println("testDOC");
        _testDOC_basic();
        _testDOC_allMethods();
        _testDOC_function();
    }

    private void _testDOC_basic() throws Exception {
        boolean success = false;

        System.out.println("_testDOC_basic");
        DistributedObjectCache doc = null;
        DistributedMap dm = null;
        //DistributedLockingMap   dlm = null;
        DistributedNioMap dnm = null;

        doc = new DistributedMapImpl(ServerCache.cache);

        //--------------------------------------------------------
        // Get a DistributedMap and make non-supprted method calls
        //--------------------------------------------------------
        doc = (new DistributedMapImpl(ServerCache.cache));

        //--------------------------------------------------------
        // try a DistributedLockingMap method call
        //--------------------------------------------------------
        /*
         * 
         * try {
         * doc.getAndLock(null, null, null);
         * success = false;
         * } catch ( RuntimeException e ) {
         * success = true;
         * }
         * assertTrue(" Missing exception ", success );
         */
        //-------------------------------------------------------

        //--------------------------------------------------------
        // try a DistributedNioMap method call
        //--------------------------------------------------------
        try {
            doc.addAlias(null, null);
            success = false;
        } catch (RuntimeException e) {
            success = true;
        }
        assertTrue(" Missing exception ", success);
        //-------------------------------------------------------

        //--------------------------------------------------------
        // try a DistributedNioMap method call on a cast object
        //--------------------------------------------------------
        try {
            ((DistributedNioMap) dm).addAlias(null, null);
            success = false;
        } catch (RuntimeException e) {
            success = true;
        }
        assertTrue(" Missing exception ", success);

        //--------------------------------------------------------
        // try a DistributedNioMap method call on a cast object
        //--------------------------------------------------------
        /*
         * try {
         * ((DistributedNioMap)dlm).addAlias(null,null);
         * success = false;
         * } catch ( RuntimeException e ) {
         * success = true;
         * }
         * assertTrue(" Missing exception ", success );
         */

        //-------------------------------------------------------
        // Must not cause compile errors
        //-------------------------------------------------------
        doc = new DistributedMapImpl(ServerCache.cache);
        //doc = new DistributedLockingMapImpl(ServerCache.cache);
        doc = new DistributedNioMapImpl(ServerCache.cache);

        dm = new DistributedMapImpl(ServerCache.cache);
        //dlm = new DistributedLockingMapImpl(ServerCache.cache);
        dnm = new DistributedNioMapImpl(ServerCache.cache);

        //-------------------------------------------------------
        // Verify instanceof
        //-------------------------------------------------------
        assertTrue("Map extend/inherit problem", dm instanceof DistributedObjectCache);
        //assertTrue("Map extend/inherit problem", dlm instanceof DistributedObjectCache );
        assertTrue("Map extend/inherit problem", dnm instanceof DistributedObjectCache);

        //assertTrue("Map extend/inherit problem", dlm instanceof DistributedMap );
        //assertTrue("Map extend/inherit problem", dlm instanceof DistributedNioMap );

        //assertTrue("Map extend/inherit problem", dm  instanceof DistributedLockingMap );
        assertTrue("Map extend/inherit problem", dm instanceof DistributedNioMap);

        //assertTrue("Map extend/inherit problem", dnm instanceof DistributedLockingMap );
        assertTrue("Map extend/inherit problem", dnm instanceof DistributedMap);
        //-------------------------------------------------------

        //-------------------------------------------------------
        // Must not cause compile errors
        //-------------------------------------------------------
        dm = (new DistributedMapImpl(ServerCache.cache));
        //dlm = (DistributedObjectCache)(new DistributedLockingMapImpl(ServerCache.cache));
        dnm = (new DistributedNioMapImpl(ServerCache.cache));

        //-------------------------------------------------------
        // Verify instanceof
        //-------------------------------------------------------
        assertTrue("Map extend/inherit problem", dm instanceof DistributedObjectCache);
        //assertTrue("Map extend/inherit problem", dlm instanceof DistributedObjectCache );
        assertTrue("Map extend/inherit problem", dnm instanceof DistributedObjectCache);

        //assertTrue("Map extend/inherit problem", dlm instanceof DistributedMap );
        //assertTrue("Map extend/inherit problem", dlm instanceof DistributedNioMap );

        //assertTrue("Map extend/inherit problem", dm  instanceof DistributedLockingMap );
        assertTrue("Map extend/inherit problem", dm instanceof DistributedNioMap);

        //assertTrue("Map extend/inherit problem", dnm instanceof DistributedLockingMap );
        assertTrue("Map extend/inherit problem", dnm instanceof DistributedMap);
        //-------------------------------------------------------

        //-------------------------------------------------------
        // Must cause a compiler error - Verified 02/09/2004 CPF
        //-------------------------------------------------------
        // dm.addAlias(null,null);
        // dm.getAndLock(null, null, null);
        // dnm.getAndLock(null, null, null);
        // dlm.addAlias(null, null);
        // (DistributedNioMapImpl    )(new DistributedMapImpl(ServerCache.cache));
        // (DistributedNioMapImpl    )(new DistributedLockingMapImpl(ServerCache.cache));
        // (DistributedLockingMapImpl)(new DistributedMapImpl(ServerCache.cache));
        // (DistributedLockingMapImpl)(new DistributedNioMapImpl(ServerCache.cache));
        // (DistributedMapImpl       )(new DistributedLockingMapImpl(ServerCache.cache));
        // (DistributedMapImpl       )(new DistributedNioMapImpl(ServerCache.cache));
        //-------------------------------------------------------

    }

    private void _testDOC_allMethods() throws Exception {
        boolean success = false;

        System.out.println("_testDOC_allMethods()");

        DistributedObjectCache doc = null;
        DistributedMap dm = null;
        //DistributedLockingMap   dlm = null;
        DistributedNioMap dnm = null;

        doc = new DistributedMapImpl(ServerCache.cache);
        //dlm = new DistributedLockingMapImpl(ServerCache.cache);
        dnm = new DistributedNioMapImpl(ServerCache.cache);

        // Methods inherited from interface com.ibm.websphere.cache.DistributedMap 
        // addInvalidationListener, enableListener, get, getSharingPolicy, invalidate, invalidate, put, put, removeInvalidationListener, setSharingPolicy 

        //String method = "get" ;
        //invoke( doc, method, 0x01  );

        /*
         * 
         * try {
         * doc.addAlias(null, null);
         * success = false;
         * } catch ( RuntimeException e ) {
         * success = true;
         * }
         * assertTrue(" Missing exception ", success );
         * 
         * try {
         * doc.addInvalidationListener(null);
         * success = false;
         * } catch ( RuntimeException e ) {
         * success = true;
         * }
         * assertTrue(" Missing exception ", success );
         * 
         * doc.clear();
         * 
         * try {
         * doc.clearAllMapEntryLocks(null, null);
         * success = false;
         * } catch ( RuntimeException e ) {
         * success = true;
         * }
         * assertTrue(" Missing exception ", success );
         * 
         * try {
         * doc.clearMapEntryLocks(null, null);
         * success = false;
         * } catch ( RuntimeException e ) {
         * success = true;
         * }
         * assertTrue(" Missing exception ", success );
         * 
         * doc.enableListener(false);
         * doc.equals(null);
         * doc.get(null );
         * doc.get(null, null, null );
         * doc.getAndLock(null, null, null );
         * doc.getCacheEntry(null);
         * doc.getClass();
         * doc.getSharingPolicy();
         * doc.hashCode();
         * doc.invalidate(null);
         * doc.invalidate(null, false);
         * doc.invalidate(null, null, null);
         * doc.invalidateAndUnlock(null, null, null);
         * doc.lockMapEntry(null, null, null);
         * doc.notify();
         * doc.notifyAll();
         * doc.put(null, null);
         * doc.put(null, null, null, null);
         * doc.put(null, null, 0, 0, 0, null);
         * doc.put(null, null, null, 0, 0, 0, null, null );
         * doc.putAndUnlock(null, null, null, null );
         * doc.releaseLruEntries(0);
         * doc.removeAlias(null);
         * doc.removeInvalidationListener(null);
         * doc.setSharingPolicy(-11);
         * doc.toString();
         * doc.unlockMapEntry(null, null, null);
         * doc.wait();
         * 
         * // Methods inherited from interface com.ibm.websphere.cache.DistributedMap
         * // addInvalidationListener, enableListener, get, getSharingPolicy, invalidate, invalidate, put, put, removeInvalidationListener, setSharingPolicy
         * 
         * // Methods inherited from interface java.util.Map
         * // clear, containsKey, containsValue, entrySet, equals, hashCode, isEmpty, keySet, putAll, remove, size, values
         */

    }

    private void _testDOC_function() throws Exception {
        boolean success = false;

        System.out.println("_testDOC_function()");

        DistributedMap dm = null;
        //DistributedLockingMap   dlm = null;
        DistributedNioMap dnm = null;

        String key = "key";
        Hashtable objectKey = new Hashtable();
        objectKey.put(key, key);

        dm = new DistributedMapImpl(ServerCache.cache);
        //dlm = new DistributedLockingMapImpl(ServerCache.cache);
        dnm = new DistributedNioMapImpl(ServerCache.cache);

        dm.put(objectKey, objectKey);
        Hashtable result = (Hashtable) dm.get(objectKey);

        assertTrue("dm.get() Failure", result == objectKey);
        assertTrue("dm.get() Failure", result.get(key) == key);

        //result = (Hashtable)dlm.get(objectKey);

        //assertTrue("dlm.get() Failure", result == objectKey);
        //assertTrue("dlm.get() Failure", result.get(key) == key);

        com.ibm.websphere.cache.CacheEntry ce = dnm.getCacheEntry(objectKey);

        assertTrue("dnm.get() Failure", ce.getValue() == objectKey);
        assertTrue("dnm.get() Failure", ((Hashtable) (ce.getValue())).get(key) == key);

        dm.clear();

        dm.keySet();

    }

    private void invoke(Object object, String method, int type) throws Exception {

        Class[] types = null;
        Object[] parms = null;

        switch (type) {
            case 0x01: {
                types = new Class[1];
                parms = new Object[1];
                types[0] = Object.class;
                parms[0] = null;
            }
        }

        object.getClass().getMethod(method, types).invoke(object, parms);

    }

}
