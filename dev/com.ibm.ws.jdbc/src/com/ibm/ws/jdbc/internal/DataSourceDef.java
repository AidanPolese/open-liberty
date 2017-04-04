/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011,2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jdbc.internal;

/**
 * Property names from @DataSourceDefinition.
 */
public enum DataSourceDef {
    className,
    databaseName,
    description,
    initialPoolSize,
    isolationLevel,
    loginTimeout,
    maxIdleTime,
    maxPoolSize,
    maxStatements,
    minPoolSize,
    name,
    password,
    portNumber,
    properties,
    serverName,
    transactional,
    url,
    user;
}