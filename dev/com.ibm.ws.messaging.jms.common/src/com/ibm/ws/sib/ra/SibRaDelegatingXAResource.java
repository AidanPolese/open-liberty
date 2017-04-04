/**
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date      Origin   Description
 * --------------- ------    -------- ---------------------------------------
 * 373006		   07-Aug-06 ajw	  1pc optimistaion
 * ============================================================================
 */
package com.ibm.ws.sib.ra;

import com.ibm.wsspi.sib.core.SIXAResource;

/**
 * Interface for XAResources which delegate to an <code>SIXAResource</code>.
 */
public interface SibRaDelegatingXAResource
{
    /**
     * Returns the delegated <code>SIXAResource</code>.
     * 
     * @return the <code>SIXAResource</code>
     */
    public SIXAResource getSiXaResource();
}
