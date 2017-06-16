/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.container.service.app.deploy.extended;

import java.util.List;

import com.ibm.ws.container.service.app.deploy.ContainerInfo;

/**
 *
 */
public interface LibraryClassesContainerInfo extends LibraryContainerInfo {
    /**
     * Get the ContainerInfo for all of the classes directly or indirectly available
     * through this library.
     * 
     * @return The classes container infos for this module.
     */
    public List<ContainerInfo> getClassesContainerInfo();
}
