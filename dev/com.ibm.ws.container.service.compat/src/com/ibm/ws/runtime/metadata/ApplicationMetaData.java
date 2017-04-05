/* ============================================================================
 * 1.6, 3/12/06
 *
 * IBM Confidential OCO Source Material
 * 5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
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
 * D349516         060312   ericvn    Flag ibm-private-in-use
 * 
 * ============================================================================ */

package com.ibm.ws.runtime.metadata;

import com.ibm.websphere.csi.J2EEName;

/**
 * The interface for application level meta data.
 * 
 * @ibm-private-in-use
 */

public interface ApplicationMetaData extends MetaData {

    /**
     * ???
     */
    boolean createComponentMBeans();

    /**
     * Gets the J2EEName associated with this application.
     */

    J2EEName getJ2EEName();
}
