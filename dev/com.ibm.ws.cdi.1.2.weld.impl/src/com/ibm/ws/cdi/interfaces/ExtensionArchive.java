/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.cdi.interfaces;

import java.util.Set;

public interface ExtensionArchive extends CDIArchive {

    /**
     * @return
     */
    Set<String> getExtraClasses();

    /**
     * @return
     */
    Set<String> getExtraBeanDefiningAnnotations();

    /**
     * @return
     */
    boolean applicationBDAsVisible();

    /**
     * @return
     */
    boolean isExtClassesOnly();
}
