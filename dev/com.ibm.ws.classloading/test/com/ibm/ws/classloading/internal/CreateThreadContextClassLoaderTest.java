/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.classloading.internal;

import static com.ibm.ws.classloading.internal.TestUtil.createAppClassloader;
import static com.ibm.ws.classloading.internal.TestUtil.getClassLoadingService;
import static com.ibm.ws.classloading.internal.TestUtil.getOtherClassesURL;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.net.URL;

import org.junit.Rule;
import org.junit.Test;

import test.common.SharedOutputManager;

import com.ibm.wsspi.classloading.ClassLoadingService;

public class CreateThreadContextClassLoaderTest {

    @Rule
    public final SharedOutputManager outputManager = SharedOutputManager.getInstance();

    /**
     * Create two ThreadContextClassLoaders from two different AppClassLoaders with the same ID.
     */
    @Test
    public void testCreatingThreadContextClassLoaders() throws Exception {
        // create two class loaders with the same ID
        String id = this.getClass().getName();
        URL url = getOtherClassesURL(true);
        AppClassLoader appLoader1 = createAppClassloader(id, url, false);
        AppClassLoader appLoader2 = createAppClassloader(id, url, false);

        ClassLoadingService cls = getClassLoadingService(null);
        ClassLoader tccl1 = cls.createThreadContextClassLoader(appLoader1);
        ClassLoader tccl2 = cls.createThreadContextClassLoader(appLoader2);
        assertThat("Creating ThreadContextClassLoaders from two AppClassLoaders with the same ID should produce two differents instances.",
                   tccl1,
                   is(not(sameInstance(tccl2))));
    }
}
