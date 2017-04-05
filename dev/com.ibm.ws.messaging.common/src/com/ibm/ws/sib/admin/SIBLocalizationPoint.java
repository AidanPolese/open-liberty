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
package com.ibm.ws.sib.admin;

/**
 * Interface representing the Localization point of the destination
 *
 */
public interface SIBLocalizationPoint extends LWMConfig {

	/**
	 * @return the uuid
	 */
	public String getUuid();

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid);

	/**
	 * @return the identifier
	 */
	public String getIdentifier();

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier);

	
	/**
	 * @return the sendAllowed
	 */
	public boolean isSendAllowed();

	/**
	 * @param sendAllowed
	 *            the sendAllowed to set
	 */
	public void setSendAllowed(boolean sendAllowed);

	/**
	 * @return the highMsgThreshold
	 */
	public long getHighMsgThreshold();

	/**
	 * @param highMsgThreshold the highMsgThreshold to set
	 */
	public void setHighMsgThreshold(long highMsgThreshold);

	/**
	 * @return the targetUuid
	 */
	public String getTargetUuid();

	/**
	 * @param targetUuid the targetUuid to set
	 */
	public void setTargetUuid(String targetUuid);

	


}
