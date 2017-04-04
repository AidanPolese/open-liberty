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
/*
 * Some of the code was derived from code supplied by the Apache Software Foundation licensed under the Apache License, Version 2.0.
 */
package com.ibm.ws.transport.iiop.security.config.tss;

import org.omg.CSIIOP.SCS_GSSExportedName;
import org.omg.CSIIOP.ServiceConfiguration;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.transport.iiop.security.config.ConfigException;
import com.ibm.ws.transport.iiop.security.util.GSSExportedName;
import com.ibm.ws.transport.iiop.security.util.Util;

public class TSSGSSExportedNameConfig extends TSSServiceConfigurationConfig {

    private final String name;
    private final String oid;

    public TSSGSSExportedNameConfig(String name, String oid) {
        this.name = name;
        this.oid = oid;
    }

    public TSSGSSExportedNameConfig(GSSExportedName name) {
        this.name = name.getName();
        this.oid = name.getOid();
    }

    public String getName() {
        return name;
    }

    public String getOid() {
        return oid;
    }

    @Override
    public ServiceConfiguration generateServiceConfiguration() throws ConfigException {
        ServiceConfiguration config = new ServiceConfiguration();

        config.syntax = SCS_GSSExportedName.value;
        config.name = Util.encodeGSSExportName(oid, name);

        if (config.name == null)
            throw new ConfigException("Unable to encode GSSExportedName");

        return config;
    }

    @Override
    @Trivial
    void toString(String spaces, StringBuilder buf) {
        String moreSpaces = spaces + "  ";
        buf.append(spaces).append("TSSGSSExportedNameConfig: [\n");
        buf.append(moreSpaces).append("oid : ").append(oid).append("\n");
        buf.append(moreSpaces).append("name: ").append(name).append("\n");
        buf.append(spaces).append("]\n");
    }
}
