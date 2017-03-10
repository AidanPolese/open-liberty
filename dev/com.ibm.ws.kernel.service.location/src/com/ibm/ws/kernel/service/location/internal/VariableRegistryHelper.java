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
package com.ibm.ws.kernel.service.location.internal;

import com.ibm.wsspi.kernel.service.location.VariableRegistry;

/**
 *
 */
public class VariableRegistryHelper implements VariableRegistry {

    private final SymbolRegistry registry;

    public VariableRegistryHelper() {
        this(SymbolRegistry.getRegistry());
    }

    public VariableRegistryHelper(SymbolRegistry registry) {
        this.registry = registry;
    }

    @Override
    public boolean addVariable(String variable, String value) {
        return registry.addStringSymbol(variable, value);
    }

    @Override
    public void replaceVariable(String variable, String value) {
        registry.replaceStringSymbol(variable, value);
    }

    /** {@inheritDoc} */
    @Override
    public String resolveString(String string) {
        return registry.resolveSymbolicString(string);
    }

    /** {@inheritDoc} */
    @Override
    public String resolveRawString(String string) {
        return registry.resolveRawSymbolicString(string);
    }

    /** {@inheritDoc} */
    @Override
    public void removeVariable(String symbol) {
        registry.removeSymbol(symbol);
    }

}