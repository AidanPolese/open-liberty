/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2001,2017
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.rsadapter.impl;

import com.ibm.ws.rsadapter.AdapterUtil;

import java.sql.Connection;
import java.util.Arrays;

import javax.sql.PooledConnection;

/**
 * Data structure used for the results of a connection request.
 */
public class ConnectionResults
{
    /**
     * Connection to the database.
     */
    Connection connection;

    /**
     * The PooledConnection or XAConnection (which implements PooledConnection).
     * This value will be null if the connection was requested from a DataSource
     * rather than a ConnectionPoolDataSource or XADataSource.
     */
    PooledConnection pooledConnection;

    /**
     * Construct a list of connection results.
     * 
     * @param pooledConnection PooledConnection, XAConnection, or null.
     * @param connection connection to the database. Null if the connection isn't available yet.
     */
    public ConnectionResults(PooledConnection pooledConnection, Connection connection) {
        this.pooledConnection = pooledConnection;
        this.connection = connection;
    }

    /**
     * Format the connection results as text.
     * 
     * @return text displaying this ConnectionResults object, pooled connection, connection, and cookie.
     */
    @Override
    public String toString() {
        return Arrays.asList
                        (
                         AdapterUtil.toString(this),
                         AdapterUtil.toString(pooledConnection),
                         AdapterUtil.toString(connection)
                        ).toString();
    }
}
