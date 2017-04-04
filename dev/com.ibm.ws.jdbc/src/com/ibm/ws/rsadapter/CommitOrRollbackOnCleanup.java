/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2001,2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

package com.ibm.ws.rsadapter;

/**
 * commitOrRollbackOnCleanup specifies the action to take when a connection is destroyed or
 * returned to the pool and a database local transaction (autocommit = false) might still be active.
 * If the database supports unit of work detection, this property is only applied when in a
 * database unit of work.
 */
public enum CommitOrRollbackOnCleanup {
    commit,
    rollback;
}
