/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.security.token.ltpa.internal;

public class LTPAConstants {

    /**
     * Used to identify the expiration limit of the LTPA2 token.
     */
    protected static final String EXPIRATION = "expiration";

    /**
     * Used to identify the LTPA shared key.
     */
    protected static final String SECRET_KEY = "ltpa_shared_key";

    /**
     * Used to identify the LTPA private key.
     */
    protected static final String PRIVATE_KEY = "ltpa_private_key";

    /**
     * Used to identify the LTPA public key.
     */
    protected static final String PUBLIC_KEY = "ltpa_public_key";

    /**
     * Used to identify the unique identifier of a user.
     */
    protected static final String UNIQUE_ID = "unique_id";
}
