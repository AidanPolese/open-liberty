/* ============================================================================
 * 1.5, 5/13/04
 *
 * IBM Confidential OCO Source Material
 * 5630-A36 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * Change activity:
 *
 * Reason          Date     userid    Description
 * -------         ------   --------  -----------------------------------------
 * LIDB3133-2.2.1  040301   tmusta    Adding SPI tags
 * 
 * ============================================================================ */

package com.ibm.ws.runtime.metadata;

import com.ibm.websphere.csi.J2EEName;

/**
 * The interface for module level meta data.
 * 
 * @ibm-private-in-use
 */

public interface ModuleMetaData extends MetaData {

    /**
     * Gets the J2EEName associated with the module.
     */
    public J2EEName getJ2EEName();

    /**
     * Gets the application meta data object associated with
     * this module.
     */

    public ApplicationMetaData getApplicationMetaData();

    /**
     * @deprecated This method is going away with LIDB859.
     */

    public ComponentMetaData[] getComponentMetaDatas();
}
