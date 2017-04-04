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
package com.ibm.ws.jaxrs20.metadata.builder;

import java.util.HashSet;
import java.util.Set;

import com.ibm.ws.jaxrs20.api.JaxRsModuleInfoBuilderExtension;
import com.ibm.ws.jaxrs20.metadata.JaxRsModuleType;

/**
 * The base impl of JaxWsModuleInfoBuilderExtension, set the enclosing JaxWsModuleInfoBuilder types
 */
public abstract class AbstractJaxRsModuleInfoBuilderExtension implements JaxRsModuleInfoBuilderExtension {

    private final Set<JaxRsModuleType> supportTypes = new HashSet<JaxRsModuleType>();

    public AbstractJaxRsModuleInfoBuilderExtension(JaxRsModuleType... supportTypes) {
        for (JaxRsModuleType supportType : supportTypes) {
            this.supportTypes.add(supportType);
        }
    }

    @Override
    public Set<JaxRsModuleType> getSupportTypes() {
        return this.supportTypes;
    }
}
