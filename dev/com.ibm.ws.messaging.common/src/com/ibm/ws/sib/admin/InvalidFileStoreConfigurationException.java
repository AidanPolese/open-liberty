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
 *Exception representing invalid file store configuration
 */
public class InvalidFileStoreConfigurationException extends SIBExceptionBase {

	private static final long serialVersionUID = 2326898619518568826L;

	/**
	 * @param msg
	 */
	public InvalidFileStoreConfigurationException(String msg) {
		super(msg);
	}

}
