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

public interface FacesConfigOrdering {
    /**
     * @return true if &lt;after> is specified
     * @see #getAfterNames
     * @see #isSetAfterOthers
     */
    boolean isSetAfter();

    /**
     * @return &lt;name>, within &lt;after> if specified, as a read-only list
     * @see #isSetAfter
     */
    List<String> getAfterNames();

    /**
     * @return true if &lt;others>, within &lt;after> if specified, is specified
     * @see #isSetAfter
     */
    boolean isSetAfterOthers();

    /**
     * @return true if &lt;before> is specified
     * @see #getBeforeNames
     * @see #isSetBeforeOthers
     */
    boolean isSetBefore();

    /**
     * @return &lt;name>, within &lt;before> if specified, as a read-only list
     * @see #isSetBefore
     */
    List<String> getBeforeNames();

    /**
     * @return true if &lt;others>, within &lt;before> if specified, is specified
     * @see #isSetBefore
     */
    boolean isSetBeforeOthers();
}