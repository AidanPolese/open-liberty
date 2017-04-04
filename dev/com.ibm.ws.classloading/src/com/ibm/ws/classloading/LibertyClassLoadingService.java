/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.classloading;

import java.io.File;
import java.util.List;

import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.classloading.ClassLoaderConfiguration;
import com.ibm.wsspi.classloading.ClassLoadingService;
import com.ibm.wsspi.classloading.GatewayConfiguration;
import com.ibm.wsspi.library.Library;

/**
 * This interface constrains the {@link ClassLoadingService} to use the {@link LibertyClassLoader}.
 */
public interface LibertyClassLoadingService extends ClassLoadingService {
    @Override
    LibertyClassLoader createTopLevelClassLoader(List<Container> classPath, GatewayConfiguration gwConfig, ClassLoaderConfiguration config);

    @Override
    LibertyClassLoader createBundleAddOnClassLoader(List<File> classPath, ClassLoader gwClassLoader, ClassLoaderConfiguration config);

    @Override
    LibertyClassLoader createChildClassLoader(List<Container> classpath, ClassLoaderConfiguration config);

    @Override
    LibertyClassLoader getShadowClassLoader(ClassLoader loader);

    @Override
    LibertyClassLoader getSharedLibraryClassLoader(Library lib);

    @Override
    LibertyClassLoader createThreadContextClassLoader(ClassLoader applicationClassLoader);
}
