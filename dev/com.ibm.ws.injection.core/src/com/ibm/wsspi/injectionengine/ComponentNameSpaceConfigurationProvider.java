/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.injectionengine;

/**
 * A provider for a component namespace configuration. This interface allows
 * the caller to delay creation of a ComponentNameSpaceConfiguration until it
 * is needed to resolve metadata.
 */
public interface ComponentNameSpaceConfigurationProvider
{
    ComponentNameSpaceConfiguration getComponentNameSpaceConfiguration()
                    throws InjectionException;
}
