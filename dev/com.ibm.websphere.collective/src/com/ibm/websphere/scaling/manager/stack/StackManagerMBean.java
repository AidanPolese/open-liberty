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
package com.ibm.websphere.scaling.manager.stack;

/**
 * The StackManagerMBean provides the interface for controlling the StackManager service.
 * <p>
 * The ObjectName for this MBean is {@value #OBJECT_NAME}.
 * 
 * @ibm-api
 */
public interface StackManagerMBean {

    /**
     * A String representing the {@link javax.management.ObjectName} that this MBean maps to.
     */
    String OBJECT_NAME = "WebSphere:feature=scalingController,type=StackManager,name=StackManager";

    /**
     * Scans the stack groups directory for additions, modifications, and deletions
     * on all replicas(controllers).
     * The operation returns after the scan is completed.
     * 
     * <p>
     * 
     * @param None
     * @throws None
     */
    void scan();

    /**
     * Scans the stack groups directory for additions, modifications, and deletions.
     * The operation returns after the scan is completed.
     * 
     * <p>
     * 
     * @param None
     * @throws None
     */
    void scanLocal();

}
