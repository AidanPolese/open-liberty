/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

package com.ibm.wsspi.security.authorization.jacc;
import java.security.Policy;
import javax.security.jacc.PolicyConfigurationFactory;

public interface ProviderService {
    /**
     * Returns the instance representing the provider-specific implementation 
     * of the java.security.Policy abstract class. 
     * @return An instance which implements java.security.Policy class.
     */
    public Policy getPolicy();
    /**
     * Returns the instance representing the provider-specific implementation
     * of the javax.security.jacc.PolicyConfigurationFactory abstract class.
     * @return An instance which implements PolicyConfigurationFactory class.
     */
    public PolicyConfigurationFactory getPolicyConfigFactory();
}
