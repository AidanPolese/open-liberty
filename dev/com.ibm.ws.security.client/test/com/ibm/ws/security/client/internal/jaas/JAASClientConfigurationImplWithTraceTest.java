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
package com.ibm.ws.security.client.internal.jaas;

import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Drive all of the tests with trace enabled. This has two purposes:
 * 1. Catches any potentially issues that only occur when trace is turned on.
 * 2. Helps improve code coverage by not "penalizing" for not executing trace lines.
 */
public class JAASClientConfigurationImplWithTraceTest extends JAASClientConfigurationImplTest {

    @BeforeClass
    public static void enableTrace() {
        outputMgr.trace("*=all=enabled");
    }

    @AfterClass
    public static void disableTrace() {
        outputMgr.trace("*=all=disabled");
    }
}