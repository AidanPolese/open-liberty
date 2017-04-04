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
package com.ibm.ws.dynamic.bundle;

import java.util.ArrayList;
import java.util.List;

public class ServiceComponentDeclaration {
    private String name;
    private String implementationClass;
    private String serviceVendor = "IBM";
    private String servicePid;
    private final List<String> providedInterfaces = new ArrayList<String>();
    private final List<ServiceReferenceDeclaration> references = new ArrayList<ServiceReferenceDeclaration>();

    public ServiceComponentDeclaration name(String name) {
        this.name = name;
        return this;
    }

    public ServiceComponentDeclaration impl(Class<?> implementationClass) {
        return impl(implementationClass.getName());
    }

    public ServiceComponentDeclaration impl(String implementationClass) {
        this.implementationClass = implementationClass;
        return this;
    }

    public ServiceComponentDeclaration vendor(String serviceVendor) {
        this.serviceVendor = serviceVendor;
        return this;
    }

    public ServiceComponentDeclaration pid(String servicePid) {
        this.servicePid = servicePid;
        return this;
    }

    public ServiceComponentDeclaration provide(Class<?> iface) {
        return provide(iface.getName());
    }

    public ServiceComponentDeclaration provide(String iface) {
        providedInterfaces.add(iface);
        return this;
    }

    public ServiceComponentDeclaration require(ServiceReferenceDeclaration ref) {
        references.add(ref);
        return this;
    }

    public String getFileName() {
        return "OSGI-INF/" + name + ".xml";
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version='1.0' encoding='utf-8' standalone='no'?>\n");
        sb.append("<component xmlns='http://felix.apache.org/xmlns/scr/v1.1.0-felix' configuration-policy='ignore' name='").append(name).append("'>\n");
        sb.append("  <implementation class='").append(implementationClass).append("' />\n");
        sb.append("  <service>\n");
        for (String iface : providedInterfaces)
            sb.append("    <provide interface='").append(iface).append("' />\n");
        sb.append("  </service>\n");
        sb.append("  <property name='service.vendor' value='").append(serviceVendor).append("' />\n");
        sb.append("  <property name='service.pid' value='").append(servicePid).append("' />\n");
        for (ServiceReferenceDeclaration ref : references)
            sb.append("  ").append(ref).append("\n");
        sb.append("</component>");
        return sb.toString();
    }
}
