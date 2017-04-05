/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.library;

/**
 * This interface should be implemented by those interested in being notified
 * when a shared library has changed (either in configuration or content).
 * <p>
 * A service should be registered under this interface with a property of
 * library=id, where id is the library id in config.
 * <p>
 * To allow for parent first nested config, where &lt;library&gt; can be nested
 * under other elements, the presence of libraryRef containing the library pid
 * implicitly registers the service as a listener if it implements this interface.
 */
public interface LibraryChangeListener {

    void libraryNotification();

}
