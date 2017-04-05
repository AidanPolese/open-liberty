/*
 * IBM Confidential OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 * 
 * Change activity:
 * ---------------  --------  --------  ------------------------------------------
 * Reason           Date      Origin    Description
 * ---------------  --------  --------  ------------------------------------------
 */

package com.ibm.ws.messaging.security;

/**
 * Exception class for MessagingSecurity component
 * @author Sharath Chandra B
 *
 */
public class MessagingSecurityException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public MessagingSecurityException(String message) {
		super(message);
	}
	
	public MessagingSecurityException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public MessagingSecurityException(Throwable throwable) {
		super(throwable);
	}
	
	public MessagingSecurityException() {
		super();
	}

}
