/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jca.internal;

import com.ibm.ws.runtime.metadata.ModuleMetaData;

/**
 * Bean Validation Helper interface.
 */
public interface BeanValidationHelper {

    public void setBeanValidationSvc(Object svc);

    public void validateInstance(ModuleMetaData mmd, ClassLoader loader, Object instance);
}
