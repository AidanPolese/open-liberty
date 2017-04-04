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
 *                                 Original
 * ============================================================================
 */
package com.ibm.ws.sib.admin;

/**
 * Interface to create destination of type AliasDestination
 *
 */
public interface AliasDestination extends BaseDestination {

	/**
	 * Get the target destination
	 * @return String 
	 */
    public String getTargetDestination();

    /**
     * Set the target destination
     * @param targetDestination The target destination name or identifier
     */
    public void setTargetDestination(String targetDestination);

    /**
     * Get overrideOfQOSByProducerAllowed attribute
     * @return String
     */
    public String isOverrideOfQOSByProducerAllowed();

    /**
     * Set the overrideOfQOSByProducerAllowed attribute
     * @param overrideOfQOSByProducerAllowed Three vlaues are valid Inherit, true and false
     */
    public void setOverrideOfQOSByProducerAllowed(String overrideOfQOSByProducerAllowed);

    /**
     * Get the default reliability of the destination
     * @return String
     */
    public String getDefaultReliability();

    /**
     * Sets the default reliability of the destination
     * Values accepted are : 
     * Inherit - Indicates that vlaue has to be inherited from the target destination
     * BestEffortNonPersistent
     * ExpressNonPersistent
     * ReliableNonPersistent
     * ReliablePersistent
     * AssuredPersistent
     * @param defaultReliability
     */
    public void setDefaultReliability(String defaultReliability);

    /**
     * Get the maximum relaiability of a destination
     * @return String
     */
    public String getMaximumReliability();

    /**
     * Sets the maximum reliability of the destination
     * Values accepted are : 
     * Inherit - Indicates that vlaue has to be inherited from the target destination
     * BestEffortNonPersistent
     * ExpressNonPersistent
     * ReliableNonPersistent
     * ReliablePersistent
     * AssuredPersistent
     * @param maximumReliability
     */
    public void setMaximumReliability(String maximumReliability);

    /**
     * Indicates if messages can be sent to the destination
     * @return String
     */
    public String isSendAllowed();

    /**
     * Sets the sendAllowed atribute
     * @param sendAllowed
     */
    public void setSendAllowed(String sendAllowed);

    /**
     * Indicates if messages can be received from the destination
     * @return String
     */
    public String isReceiveAllowed();

    /**
     * Sets the receiveAllowed attribute
     * @param receiveAllowed
     */
    public void setReceiveAllowed(String receiveAllowed);

    /**
     * Sets delegateAuthCheckToTargetDestination attribute.Indicates if Authorization Check can be delegated to target destination
     * @param delegateAuthCheckToTargetDestination
     */
    public void setDelegateAuthCheckToTargetDestination(boolean delegateAuthCheckToTargetDestination);

    /**
     * Indicates if Authorization Check can be delegated to target destination
     * @return boolean 
     */
    public boolean getDelegateAuthCheckToTargetDestination();

}
