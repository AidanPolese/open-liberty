/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011, 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.classloading.internal;

import static com.ibm.ws.classloading.internal.TestUtil.getClassLoadingService;

import com.ibm.wsspi.classloading.ClassLoaderConfiguration;
import com.ibm.wsspi.classloading.ClassLoaderIdentity;
import com.ibm.wsspi.classloading.GatewayConfiguration;

/**
 * Test the search order is as expected for a top-level application classloader
 */
public class OrderingOfSystemClassesInTopLevelClassLoaderTest extends GatewayClassLoaderTest {

    @Override
    ClassLoader createGatewayToParent(ClassLoader parentLoader) throws Exception {
        ClassLoadingServiceImpl classLoadingService = getClassLoadingService(parentLoader);
        GatewayConfiguration gwCfg = classLoadingService.createGatewayConfiguration();
        ClassLoaderConfiguration clCfg = classLoadingService.createClassLoaderConfiguration();
        clCfg.setDelegateToParentAfterCheckingLocalClasspath(true);
        ClassLoaderIdentity id = classLoadingService.createIdentity("UnitTest", "DirectGatewayClassLoaderTest");
        clCfg.setId(id);
        return classLoadingService.createTopLevelClassLoader(null, gwCfg, clCfg);
    }

}
