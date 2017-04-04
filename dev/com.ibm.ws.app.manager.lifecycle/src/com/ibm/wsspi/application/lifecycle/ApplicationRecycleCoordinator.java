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
package com.ibm.wsspi.application.lifecycle;

import java.util.Set;

/**
 *
 */
public interface ApplicationRecycleCoordinator {
    public void recycleApplications(Set<String> dependentApplications);
}
