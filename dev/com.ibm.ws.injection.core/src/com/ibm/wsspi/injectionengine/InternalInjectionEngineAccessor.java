/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.injectionengine;

import com.ibm.ws.injectionengine.InternalInjectionEngine;

/**
 * Accessor for InternalInjectionEngine. <p>
 *
 * Used to expose package protected methods of InjectionEngineAccessor
 * within the injection.impl build component. <p>
 */
public final class InternalInjectionEngineAccessor
{
    /**
     * Do not allow instances to be created.
     */
    private InternalInjectionEngineAccessor()
    {
        //Private constructor to follow the singleton pattern
    }

    /**
     * Returns the single instance of the InternalInjectionEngine for the
     * current process.
     */
    public final static InternalInjectionEngine getInstance()
    {
        return InjectionEngineAccessor.getInternalInstance();
    }

    /**
     * Internal mechanism to support providing a server type specific
     * implementation of the InjectionEngine. <p>
     */
    public static void setInjectionEngine(InternalInjectionEngine ie)
    {
        InjectionEngineAccessor.setInjectionEngine(ie);
    }
}
