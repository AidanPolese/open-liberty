/* ============================================================================
 * 1.4, 5/13/04
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

/**
 * The interface for method level meta data.
 * 
 */
public interface MethodMetaData extends MetaData {

    /**
     * Gets the compponent meta data associated with this method.
     */
    public ComponentMetaData getComponentMetaData();
}
