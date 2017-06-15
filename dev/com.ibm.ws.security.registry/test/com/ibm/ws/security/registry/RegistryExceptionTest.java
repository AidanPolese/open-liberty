/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.security.registry;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 *
 */
public class RegistryExceptionTest {

    /**
     * Test method for {@link com.ibm.ws.security.registry.RegistryException#RegistryException(java.lang.String)}.
     */
    @Test
    public void consturctor() {
        assertNotNull("Constructor(String) should succeed",
                      new RegistryException("msg"));
    }

    /**
     * Test method for {@link com.ibm.ws.security.registry.RegistryException#RegistryException(java.lang.String, java.lang.Throwable)}.
     */
    @Test
    public void causeConstructor() {
        assertNotNull("Constructor(String,Throwable) should succeed",
                      new RegistryException("msg", new Throwable()));
    }
}
