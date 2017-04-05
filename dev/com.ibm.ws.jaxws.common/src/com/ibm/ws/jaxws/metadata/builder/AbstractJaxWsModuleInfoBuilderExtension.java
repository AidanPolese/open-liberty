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
package com.ibm.ws.jaxws.metadata.builder;

import java.util.HashSet;
import java.util.Set;

import com.ibm.ws.jaxws.metadata.JaxWsModuleType;

/**
 * The base impl of JaxWsModuleInfoBuilderExtension, set the enclosing JaxWsModuleInfoBuilder types
 */
public abstract class AbstractJaxWsModuleInfoBuilderExtension implements JaxWsModuleInfoBuilderExtension {

    private final Set<JaxWsModuleType> supportTypes = new HashSet<JaxWsModuleType>();

    public AbstractJaxWsModuleInfoBuilderExtension(JaxWsModuleType... supportTypes) {
        for (JaxWsModuleType supportType : supportTypes) {
            this.supportTypes.add(supportType);
        }
    }

    @Override
    public Set<JaxWsModuleType> getSupportTypes() {
        return this.supportTypes;
    }
}
