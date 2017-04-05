/**
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
package com.ibm.websphere.management.j2ee;


/**
 * The base model for J2EEApplication and J2EEModule. All J2EEDeployedObject
 * managed objects contain the original XML deployment descriptor that was created
 * for the application or module during the deployment process.
 */
public interface J2EEDeployedObjectMBean extends J2EEManagedObjectMBean {

    /**
     * The deploymentDescriptor string must contain the original XML
     * deployment descriptor that was created for this module during the deployment
     * process. The deploymentDescriptor attribute must provide a full deployment
     * descriptor based on any partial deployment descriptor plus deployment
     * annotations.
     */
    String getdeploymentDescriptor();

    /**
     * The J2EE server the application or module is deployed on.
     */
    String getserver();

}
