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
package com.ibm.ws.microprofile.faulttolerance_fat.cdi.beans;

import javax.enterprise.context.RequestScoped;

import org.eclipse.microprofile.faulttolerance.Retry;

import com.ibm.ws.microprofile.faulttolerance_fat.util.ConnectException;

@RequestScoped
public class RetryBeanC {

    private int connectCount = 0;

    // Should always abort as ConnectException.class is always thrown
    @Retry(maxRetries = 3, abortOn = { IllegalArgumentException.class, ConnectException.class })
    public void connectC() throws ConnectException {
        throw new ConnectException("RetryBeanC Connect: " + (++connectCount));
    }

}
