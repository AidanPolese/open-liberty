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

import com.ibm.ws.classloading.internal.util.Keyed;
import com.ibm.wsspi.classloading.ClassLoaderIdentity;

abstract class IdentifiedLoader extends LibertyLoader implements Keyed<ClassLoaderIdentity> {
    public IdentifiedLoader(ClassLoader parent) {
        super(parent);
    }
}