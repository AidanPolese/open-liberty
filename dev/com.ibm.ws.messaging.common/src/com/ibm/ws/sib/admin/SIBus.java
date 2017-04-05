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
 * Interface representing the default sibus object
 *
 */
public interface SIBus extends LWMConfig {
	/**
	 * Get the name of the bus.The default is defaultBus
	 * @return String
	 */
	public String getName();

	/**
	 * Se the name of the bus
	 * @param value
	 */
	public void setName(String value);

	/**
	 * Get the UUID of the default bus
	 * @return
	 */
	public String getUuid();

	/**
	 * Set the UUID of the default bus
	 * @param value
	 */
	public void setUuid(String value);

	/**
	 * Get the description of the default bus
	 * @return String
	 */
	public String getDescription();

	/**
	 * Set the description of the bus
	 * @param value
	 */
	public void setDescription(String value);

	

}
