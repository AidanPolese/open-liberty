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

package com.ibm.ws.messaging.security.authentication;

import com.ibm.ws.messaging.security.MessagingSecurityException;

/**
 * Exception class for Messaging Authentication
 * @author Sharath Chandra B
 *
 */
public class MessagingAuthenticationException extends
		MessagingSecurityException {

	private static final long serialVersionUID = 1L;
	
	public MessagingAuthenticationException() {
		super();
	}
	
	public MessagingAuthenticationException(String message) {
		super(message);
	}
	
	public MessagingAuthenticationException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	MessagingAuthenticationException(Throwable throwable) {
		super(throwable);
	}

}
