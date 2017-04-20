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
package com.ibm.ws.classloading.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import test.common.SharedOutputManager;

import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.classloading.ClassLoaderConfiguration;

public class ClassLoaderConfigurationImplTest {
    @Rule
    public final SharedOutputManager outputManager = SharedOutputManager.getInstance();

    private final Mockery mockery = new Mockery();

    @Test
    public void testNativeLibraryContainers() {
        ClassLoaderConfiguration clc = new ClassLoaderConfigurationImpl();
        Assert.assertEquals(Collections.emptyList(), clc.getNativeLibraryContainers());
        clc.setNativeLibraryContainers((Container[]) null);
        Assert.assertEquals(Collections.emptyList(), clc.getNativeLibraryContainers());
        clc.setNativeLibraryContainers((List<Container>) null);
        Assert.assertEquals(Collections.emptyList(), clc.getNativeLibraryContainers());

        Container container = mockery.mock(Container.class);

        clc = new ClassLoaderConfigurationImpl();
        clc.setNativeLibraryContainers(container);
        Assert.assertEquals(Arrays.asList(container), clc.getNativeLibraryContainers());
        clc.setNativeLibraryContainers(Arrays.asList(container));
        Assert.assertEquals(Arrays.asList(container), clc.getNativeLibraryContainers());
    }
}
