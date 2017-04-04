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
package com.ibm.ws.sib.admin.internal;

import com.ibm.ws.sib.admin.SIBLocalizationPoint;

/**
 *
 */
public class SIBLocalizationPointImpl implements SIBLocalizationPoint {

	String uuid;
	String identifier;
	boolean sendAllowed = true;
	long highMsgThreshold = 50000;
	String targetUuid;

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid
	 *            the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier
	 *            the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**  */
	@Override
	public boolean isSendAllowed() {
		return this.sendAllowed;
	}


	/**
	 * @param sendAllowed
	 *            the sendAllowed to set
	 */
	public void setSendAllowed(boolean sendAllowed) {
		this.sendAllowed = sendAllowed;
	}

	/**
	 * @return the highMsgThreshold
	 */
	public long getHighMsgThreshold() {
		return highMsgThreshold;
	}

	/**
	 * @param highMsgThreshold
	 *            the highMsgThreshold to set
	 */
	public void setHighMsgThreshold(long highMsgThreshold) {
		this.highMsgThreshold = highMsgThreshold;
	}

	/**
	 * @return the targetUuid
	 */
	public String getTargetUuid() {
		return targetUuid;
	}

	/**
	 * @param targetUuid
	 *            the targetUuid to set
	 */
	public void setTargetUuid(String targetUuid) {
		this.targetUuid = targetUuid;
	}



}
