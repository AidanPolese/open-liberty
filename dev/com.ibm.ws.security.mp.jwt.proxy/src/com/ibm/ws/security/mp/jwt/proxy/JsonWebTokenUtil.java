/*
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.mp.jwt.proxy;

import java.security.Principal;
import java.util.Hashtable;

import javax.security.auth.Subject;

public interface JsonWebTokenUtil {
	/*
	 * Retrieve the JsonWebToken from the subject's hashtable and adding it in
	 * the subject as a Principal
	 */
	public void addJsonWebToken(Subject subject, Hashtable<String, ?> customProperties, String key);

	/*
	 * Retrieve the JsonWebToken from the subject and return it as a Principal
	 */
	public Principal getJsonWebTokenPrincipal(Subject subject);

	/*
	 * Clone the JsonWebToken from the subject and return it as a Principal
	 */
	public Principal cloneJsonWebToken(Subject subject);

}