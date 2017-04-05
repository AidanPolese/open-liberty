/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2002, 2006
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.csi;

/**
 * Interface for provision of data related to a single active EJB instance
 * within the EJB container.
 */
public interface BeanInstanceInfo
{

    public Object getBeanInstance();

} // BeanInstanceInfo
