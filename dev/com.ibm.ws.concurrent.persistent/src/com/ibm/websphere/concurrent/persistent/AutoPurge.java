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
package com.ibm.websphere.concurrent.persistent;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * Execution property that controls whether or not task information is removed from the persistent store
 * upon completion. Supported values are <code>ALWAYS</code>, <code>NEVER</code>, and <code>ON_SUCCESS</code>.
 * The default is <code>ON_SUCCESS</code>.
 */
@Trivial
public enum AutoPurge {
    /**
     * Value for <code>AUTO_PURGE</code> execution property that indicates that entries for completed tasks
     * should always be purged from the persistent store, regardless of whether the task execution was successful.
     */
    ALWAYS,

    /**
     * Value for <code>AUTO_PURGE</code> execution property that indicates that entries for completed tasks
     * should never be purged from the persistent store.
     */
    NEVER,

    /**
     * Value for <code>AUTO_PURGE</code> execution property that indicates that entries for completed tasks
     * should be purged from the persistent store upon successful completion, but should remain in the persistent
     * store in all other cases, such as cancellation or failure.
     */
    ON_SUCCESS;

    /**
     * Name of the execution property for auto purge.
     * Usage:
     * executionProperties.put(AutoPurge.PROPERTY_NAME, AutoPurge.NEVER.toString());
     */
    public static final String PROPERTY_NAME = "com.ibm.ws.concurrent.AUTO_PURGE"; // TODO if proposed spec is approved, switch to spec name (and AutoPurge class)
}
