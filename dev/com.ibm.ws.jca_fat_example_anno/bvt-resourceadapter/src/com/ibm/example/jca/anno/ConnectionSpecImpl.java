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

import java.util.TreeMap;

import javax.resource.cci.ConnectionSpec;
import javax.resource.spi.AdministeredObject;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.ConnectionRequestInfo;

/**
 * Example ConnectionSpec implementation with a single property, readOnly,
 * which determines whether or not the connection is in read only mode.
 */
@AdministeredObject
public class ConnectionSpecImpl implements ConnectionSpec {
    @ConfigProperty
    private Boolean readOnly = false;

    ConnectionRequestInfoImpl createConnectionRequestInfo() {
        ConnectionRequestInfoImpl cri = new ConnectionRequestInfoImpl();
        cri.put("readOnly", isReadOnly());
        return cri;
    }

    public Boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    static class ConnectionRequestInfoImpl extends TreeMap<String, Object> implements ConnectionRequestInfo {
        private static final long serialVersionUID = -5986306401192493903L;
    }
}
