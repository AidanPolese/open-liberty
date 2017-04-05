
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
 *    Newly added to liberty release   051212
 * ============================================================================
 */
package com.ibm.ws.sib.admin;


public class SIBExceptionBase extends Exception {

	private static final long serialVersionUID = -7076891032889941247L;

	/**
	 * @see java.lang.Throwable#Throwable(String)
	 */
	public SIBExceptionBase(String msg) {
		super(msg);
	}

	/**
	 * @see java.lang.Throwable#Throwable(Throwable)
	 */
	public SIBExceptionBase(Throwable t) {
		super(t);
	}
}
