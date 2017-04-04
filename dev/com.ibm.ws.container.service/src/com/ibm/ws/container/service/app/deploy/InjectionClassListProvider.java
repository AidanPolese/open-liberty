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
package com.ibm.ws.container.service.app.deploy;

import java.util.List;

import com.ibm.wsspi.adaptable.module.Container;

/**
 *
 */
public interface InjectionClassListProvider {

    public List<String> getInjectionClasses(Container moduleContainer);
}
