/*******************************************************************************
 * Copyright (c) 1997, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.webcontainer;

import java.util.ArrayList;

import com.ibm.ws.container.DeployedModule;
import com.ibm.ws.webcontainer.session.IHttpSessionContext;
import com.ibm.ws.webcontainer.webapp.WebApp;

@SuppressWarnings("unchecked")
public interface SessionRegistry {
    IHttpSessionContext getSessionContext(DeployedModule webModuleConfig, WebApp ctx, String vhostName, ArrayList[] listeners) throws Throwable;
}
