/*******************************************************************************
 * Copyright (c) 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.jca.adapter;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionRequestInfo;
import javax.security.auth.Subject;

/**
 * WebSphere Application Server extensions to the ConnectionRequestInfo interface.
 */
public abstract class WSConnectionRequestInfo implements ConnectionRequestInfo {
    /**
     * Populates the connection request with information from the Subject (for example, for trusted context).
     * 
     * @param sub the subject
     * @throws ResourceException if an error occurs
     */
    public void populateWithIdentity(Subject sub) throws ResourceException {}
}