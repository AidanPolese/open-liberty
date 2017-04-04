/* ============================================================================
 * 
 * @(#) 1.4 SERV1/ws/code/runtime.fw/src/com/ibm/ws/runtime/metadata/MetaData.java, WAS.runtime.fw, WAS80.SERV1, h1116.09 7/24/08 13:25:30 [4/23/11 20:12:43]
 *
 * IBM Confidential OCO Source Material
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002,2008
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 *
 * Change activity:
 *
 * Reason          Date        userid    Description
 * -------         ------      --------  -----------------------------------------
 * LIDB3133-2.2.1  040301      tmusta    Adding SPI tags
 * D495464.1       07/19/2008  frowe     Update javadoc to reflect new behavior
 * 
 * ============================================================================ */

package com.ibm.ws.runtime.metadata;

/**
 * Base interface for all meta data. One feature of meta data is
 * an exensibility mechanism by which components can register a
 * location (<i>aka</i> "slot") in which to put their own specific
 * data.
 * 
 * @ibm-private-in-use
 */

public interface MetaData {

    /**
     * Gets the name associated with this meta data. For application, module
     * and component metadata, this should be the same as the corresponding
     * name in {@link #getJ2EEName}.
     */
    public String getName();

    /**
     * Sets the meta data associated with the given slot.
     * If the specified slot number is greater than highest slot number
     * currently allocated, sufficient additional slots will be allocated
     * 
     * @param slot integer slot number of desired location
     * @param metadata data to be stored
     */
    public void setMetaData(MetaDataSlot slot, Object metadata);

    /**
     * Gets the meta data associated with the given slot.
     * If the slot has been reserved, but no metadata allocated, null
     * will be returned.
     * 
     * @see com.ibm.ws.runtime.service.MetaDataService#reserveSlot(Class)
     * @param slot the desired location
     * @return null if no metadata allocated, otherwise the metadata associated
     *         with the specified slot
     */
    public Object getMetaData(MetaDataSlot slot);

    /**
     * Releases any resources that are not needed for runtime.
     * 
     * @see com.ibm.ws.runtime.service.MetaDataService#reserveSlot(Class)
     */
    public void release();
}
