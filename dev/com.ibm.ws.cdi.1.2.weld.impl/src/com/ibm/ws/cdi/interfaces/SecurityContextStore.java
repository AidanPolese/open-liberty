/*******************************************************************************
 * Copyright (c) 2012, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.cdi.interfaces;

import java.security.Principal;

/**
 * Stores security information for multiple applications
 */
public interface SecurityContextStore {

    /**
     * Gets the current caller identity.
     * 
     * @return current caller identity or <code>null</code> if none provided.
     */
    public Principal getCurrentPrincipal();

}
