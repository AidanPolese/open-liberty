/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 1997, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.j2c;

/**
 * JetStream has some recovery information which is dynamic. Our current approach
 * of registering recovery info once is insufficient to meet this requirement.
 * This particular solution allows JetStream to log the proper recovery information
 * on the fly and has minimal code changes relative to alternative options. In
 * short, this approach allows JetStream to log its own recovery object with the
 * transaction service any time its information changes and it provides a means
 * for the connection manager and container to prompt for the current recoveryId
 * before enlisting in a transaction.
 * <p>
 * In order to participate in this special processing, both ManagedConnectionFactorys
 * and ActivationSpecs would implement this SelfXARecoverable interface. This
 * interface will serve a couple of purposes. First, we will be able to associate
 * the configured XARecoveryAlias with the object and second, the JCA runtime may
 * use this interface as a marker to indicate that it does not need to invoke
 * registerResourceInfo or registerActivationSpec for this resource adapter
 * (although the recovery ids returned from these methods would not be used anyway),
 * and that later it will need to dynamically retrieve the recoveryId from the XAResource.
 * 
 */
public interface SelfXARecoverable {

    /**
     * @param alias Authentication Alias to be used for XARecovery.
     */
    void setXARecoveryAlias(String alias);
}
