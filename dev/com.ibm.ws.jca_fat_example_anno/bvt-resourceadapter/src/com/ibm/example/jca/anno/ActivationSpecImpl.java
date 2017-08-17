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

import javax.resource.ResourceException;
import javax.resource.cci.MessageListener;
import javax.resource.spi.Activation;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.InvalidPropertyException;
import javax.resource.spi.ResourceAdapter;

/**
 * Example activation spec.
 */
@Activation(messageListeners = MessageListener.class)
public class ActivationSpecImpl implements ActivationSpec {
    private ResourceAdapter adapter;

    @ConfigProperty(description = "Function name (ADD or REMOVE), upon successful completion of which to invoke the message driven bean.")
    private String functionName;

    public String getFunctionName() {
        return functionName;
    }

    @Override
    public ResourceAdapter getResourceAdapter() {
        return adapter;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    @Override
    public void setResourceAdapter(ResourceAdapter adapter) throws ResourceException {
        this.adapter = adapter;
    }

    @Override
    public void validate() throws InvalidPropertyException {
        if (!"ADD".equalsIgnoreCase(functionName) && !"REMOVE".equalsIgnoreCase(functionName))
            throw new InvalidPropertyException("functionName: " + functionName);
    }
}