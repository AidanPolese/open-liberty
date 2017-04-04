/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.security.jaspi;

import javax.security.auth.message.config.AuthConfigFactory;
import javax.security.auth.message.config.AuthConfigProvider;

/**
 * This is the OSGI service interface that a JASPI provider bundle
 * must provide to run on the WebSphere application server Liberty profile.
 */
public interface ProviderService {
    /**
     * This method is called to construct the AuthConfigProvider by invoking
     * the JSR-196 defined constructor of the AuthConfigProvider:
     * <p><code>
     * public MyAuthConfigProviderImpl(java.util.Map properties, AuthConfigFactory factory);
     * </code>
     * <p>This method may read it's own provider configuration properties and
     * and pass them to the constructor.
     * <p>
     * @param factory An AuthConfigFactory instance
     * @return An object instance that implements AuthConfigProvider
     */
    public AuthConfigProvider getAuthConfigProvider(AuthConfigFactory factory);
}
