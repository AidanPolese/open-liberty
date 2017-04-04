/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.jsf;

import java.util.List;

public interface FacesConfigAbsoluteOrdering {
    /**
     * @return true if &lt;others> is specified
     */
    boolean isSetOthers();

    /**
     * @return &lt;name>, appearing before &lt;others> if specified, as a read-only list
     */
    List<String> getNamesBeforeOthers();

    /**
     * @return &lt;name>, appearing after &lt;others> if specified, as a read-only list
     */
    List<String> getNamesAfterOthers();
}