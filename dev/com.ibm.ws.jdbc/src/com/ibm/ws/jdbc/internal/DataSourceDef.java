/*******************************************************************************
 * Copyright (c) 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
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