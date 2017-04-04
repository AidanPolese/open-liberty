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

/**
 * Services should implement this interface to allow application class loaders to delegate to them.
 */
public interface ClassProvider {
    /** @return the class loader to delegate to */
    LibertyClassLoader getDelegateLoader();
}
