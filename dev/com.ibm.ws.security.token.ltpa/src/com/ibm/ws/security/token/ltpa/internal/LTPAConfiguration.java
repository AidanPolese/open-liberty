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
package com.ibm.ws.security.token.ltpa.internal;

import com.ibm.wsspi.security.ltpa.TokenFactory;

/**
 * Service class to indicate the LTPA configuration is available and
 * ready for use.
 */
public interface LTPAConfiguration {

    /**
     * The token keys file.
     */
    public static final String CFG_KEY_IMPORT_FILE = "keysFileName";

    /**
     * The token keys file password.
     */
    public static final String CFG_KEY_PASSWORD = "keysPassword";

    /**
     * The token expiration.
     */
    public static final String CFG_KEY_TOKEN_EXPIRATION = "expiration";

    /**
     * The token keys file's monitor interval.
     */
    public static final String CFG_KEY_MONITOR_INTERVAL = "monitorInterval";

    /**
     * @return TokenFactory instance corresponding to this LTPA configuration
     */
    TokenFactory getTokenFactory();

    /**
     * @return LTPAKeyInfoManager instance corresponding to this LTPA configuration
     */
    LTPAKeyInfoManager getLTPAKeyInfoManager();

    /**
     * @return LTPA key file
     */
    String getKeyFile();

    /**
     * @return LTPA key password
     */
    String getKeyPassword();

    /**
     * @return LTPA expiration
     */
    long getTokenExpiration();

}
