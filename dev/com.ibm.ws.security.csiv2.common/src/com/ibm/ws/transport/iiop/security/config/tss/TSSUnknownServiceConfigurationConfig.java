/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.transport.iiop.security.config.tss;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.omg.CSIIOP.ServiceConfiguration;

import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.transport.iiop.security.config.ConfigException;

/**
 *
 */
public class TSSUnknownServiceConfigurationConfig extends TSSServiceConfigurationConfig {

    private final int syntax;
    private final byte[] name;

    /**
     * @param syntax2
     * @param name
     */
    public TSSUnknownServiceConfigurationConfig(int syntax, byte[] name) {
        this.syntax = syntax;
        this.name = Arrays.copyOf(name, name.length);
    }

    /** {@inheritDoc} */
    @Override
    public ServiceConfiguration generateServiceConfiguration() throws ConfigException {
        ServiceConfiguration config = new ServiceConfiguration();

        config.syntax = syntax;
        config.name = name;

        return config;
    }

    /** {@inheritDoc} */
    @Override
    @FFDCIgnore(UnsupportedEncodingException.class)
    void toString(String spaces, StringBuilder buf) {
        String moreSpaces = spaces + "  ";
        buf.append(spaces).append("TSSUnknownServiceConfigurationConfig: [\n");
        buf.append(moreSpaces).append("syntax VMCID : ").append(Integer.toHexString(syntax >> 12)).append("\n");
        buf.append(moreSpaces).append("syntax organization-scoped syntax identifier : ").append(Integer.toHexString(syntax & 0XFFF)).append("\n");
        try {
            buf.append(moreSpaces).append("name: ").append(Arrays.asList(name)).append(" (").append(new String(name, "ISO-8859-1")).append(")\n");
        } catch (UnsupportedEncodingException e) {
        }
        buf.append(spaces).append("]\n");
    }

    public int getSyntax() {
        return syntax;
    }

    public byte[] getName() {
        return Arrays.copyOf(name, name.length);
    }
}
