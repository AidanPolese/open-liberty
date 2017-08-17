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
package com.ibm.example.jca.anno;

import javax.resource.cci.InteractionSpec;
import javax.resource.spi.AdministeredObject;
import javax.resource.spi.ConfigProperty;

/**
 * Example InteractionSpec implementation with a single property, functionName,
 * which determines the function that the interaction performs. Must be one of: ADD, FIND, REMOVE.
 */
@AdministeredObject
public class InteractionSpecImpl implements InteractionSpec {
    private static final long serialVersionUID = -4153264175499435511L;

    @ConfigProperty(description = "Function name. Supported values are: ADD, FIND, REMOVE")
    private String functionName;

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
}
