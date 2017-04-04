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
 * Interface containing the basic functionality for a destination
 * All destination must implement this
 *
 */
public interface BaseDestination extends LWMConfig{
	
	/**
	 * Get the name of the destination
	 * @return String Name of the destination
	 */
	public String getName();

	/**
	 * Sets the name of the destination
	 * @param name Name of the destination
	 */
	public void setName(String name);

	/**
	 * Is this a local destination
	 * @param isLocal 
	 */
	public void setLocal(boolean isLocal);

	/**
	 * Is this is an alias destination 
	 * @return boolean
	 */
	public boolean isLocal();

	
	/**
	 * Indicated if the destination is if type ALIAS
	 * @param isAlias
	 */
	public void setAlias(boolean isAlias);


	/**
	 * Is this destination an alias to another destination
	 * 
	 * @return boolean
	 */
	public boolean isAlias();
	
	/**
	 * @return String Returns the UUID of the destiantion
	 */
	public String getUUID();

	/**
	 * Sets the UUID of the destination
	 * @param value
	 */
	public void setUUID(String value);

}
