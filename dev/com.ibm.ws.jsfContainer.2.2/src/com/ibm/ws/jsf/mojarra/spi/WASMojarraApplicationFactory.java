/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.jsf.mojarra.spi;

import javax.faces.application.Application;

import com.ibm.ws.jsf.container.JSFContainer;
import com.sun.faces.application.ApplicationFactoryImpl;

public class WASMojarraApplicationFactory extends ApplicationFactoryImpl {

    private volatile boolean initialized = false;

    @Override
    public Application getApplication() {
        Application a = super.getApplication();
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    if (JSFContainer.isCDIEnabled())
                        JSFContainer.initializeCDI(a);
                    if (JSFContainer.isBeanValidationEnabled())
                        JSFContainer.initializeBeanValidation();
                    initialized = true;
                }
            }
        }
        return a;
    }
}