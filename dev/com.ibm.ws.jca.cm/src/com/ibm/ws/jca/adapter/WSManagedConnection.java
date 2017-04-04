/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jca.adapter;

import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.Executor;

import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;

/**
 * WebSphere Application Server extensions to the ManagedConnection interface.
 */
public abstract class WSManagedConnection implements ManagedConnection {
    /**
     * Invoke to abort a connection that may be stuck waiting for a net work response or
     * the database to respond.
     * 
     * @throws SQLFeatureNotSupportedException
     */
    public void abort(Executor e) throws SQLFeatureNotSupportedException {}

    /**
     * isAborted will return true if the connection was aborted.
     */
    public boolean isAborted() {
        return false;
    }

    /**
     * Invoked after completion of a z/OS RRS (Resource Recovery Services) global transaction.
     */
    public void afterCompletionRRS() {}

    /**
     * Invoked when enlisting in a z/OS RRS (Resource Recovery Services) global transaction.
     */
    public void enlistRRS() {}

    /**
     * Returns ConnectionRequestInfo reflecting the current state of this connection.
     * 
     * @return ConnectionRequestInfo reflecting the current state of this connection.
     */
    public abstract ConnectionRequestInfo getConnectionRequestInfo();

    /**
     * Indicates whether or not this managed connection should enlist in application server managed transactions.
     * 
     * @return true if this connection should be enlisted. False if it should not be enlisted.
     */
    public boolean isTransactional() {
        return true;
    }

    /**
     * Marks the managed connection as stale.
     */
    public void markStale() {}

    /**
     * Claim the unused managed connection as a victim connection,
     * which can then be reauthenticated and reused.
     */
    public void setClaimedVictim() {}
}