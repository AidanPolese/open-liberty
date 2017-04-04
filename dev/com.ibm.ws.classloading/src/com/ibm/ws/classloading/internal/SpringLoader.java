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

import java.lang.instrument.ClassFileTransformer;

/**
 * Declare the methods expected by Spring's ReflectiveLoadTimeWeaver.
 * This interface is only used internally to document the methods that
 * Spring needs for class weaving.
 */
interface SpringLoader {
    boolean addTransformer(ClassFileTransformer cft);

    ClassLoader getThrowawayClassLoader();
}
