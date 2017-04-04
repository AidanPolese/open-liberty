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

package com.ibm.ws.messaging.security.authorization;

import com.ibm.ws.messaging.security.MessagingSecurityException;

/**
 * Exception class for Messaging Authorization
 * @author Sharath Chandra B
 *
 */
public class MessagingAuthorizationException extends MessagingSecurityException {

	private static final long serialVersionUID = 1L;
	
	public MessagingAuthorizationException() {
		super();
	}
	
	public MessagingAuthorizationException(String message) {
		super(message);
	}
	
	public MessagingAuthorizationException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public MessagingAuthorizationException(Throwable throwable) {
		super(throwable);
	}

}
