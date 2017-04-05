/*
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
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 *                                 Version 1.2 copied from CMVC
 * ============================================================================
 */

package com.ibm.ws.sib.admin;

import com.ibm.ws.sib.utils.SIBUuid8;

public interface BaseLocalizationDefinition extends Cloneable {

    /**
     * Return the UUID of the localization. In the case where the object that
     * implements this interface is created using a WCCM configuration EObject,
     * then the UUID will be as set in the EObject. If a dynamic instance of a
     * LocalizationDefinition is created, then the UUID should be set by the
     * class that creates it.
     * 
     * @return
     */
    public String getUuid();

    public void setUUID(SIBUuid8 uuid);

    /**
     * Return the name of the localization.
     * 
     * @return
     */
    public String getName();

    /**
     * Return the ConfigId to use when instantiating a JMX MBean which represents
     * this localization. An object that implements this interface will derive the
     * configId from the matching WCCM configuration EObject instance. If that object
     * does not have a reference to that instance for any reason, then the configId
     * will be based on the name of the implementing class.
     * 
     * @return String the ConfigId
     */
//  public String getConfigId();

    public long getAlterationTime();

    public void setAlterationTime(long value);

    public Object clone();
}
