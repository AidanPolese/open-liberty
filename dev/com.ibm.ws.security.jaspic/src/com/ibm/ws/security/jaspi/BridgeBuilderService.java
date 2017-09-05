/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.security.jaspi;

import javax.security.auth.message.config.AuthConfigFactory;

/**
 * Bridge to create the AuthConfigProvider, AuthConfig, AuthContext, and ServerAuthModule needed for JSR-375.
 */
public interface BridgeBuilderService {

    /**
     * @param appContext
     * @param providerFactory
     *
     */
    void buildBridgeIfNeeded(String appContext, AuthConfigFactory providerFactory);

}
