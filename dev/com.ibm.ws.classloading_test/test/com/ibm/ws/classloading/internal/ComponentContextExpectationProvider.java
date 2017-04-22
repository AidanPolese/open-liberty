/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.classloading.internal;

import org.jmock.Mockery;
import org.osgi.service.component.ComponentContext;

public interface ComponentContextExpectationProvider {
    void addExpectations(Mockery mockery, ComponentContext cc);
}
