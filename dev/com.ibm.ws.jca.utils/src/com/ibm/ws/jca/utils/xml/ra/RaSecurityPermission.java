/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012, 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jca.utils.xml.ra;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;

import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.jca.utils.xml.ra.v10.Ra10SecurityPermission;

/**
 * ra.xml security-permission element
 */
@Trivial
@XmlType(propOrder = { "description", "securityPermissionSpec" })
public class RaSecurityPermission {
    private String securityPermissionSpec;
    private List<RaDescription> description = new LinkedList<RaDescription>();
    @XmlID
    @XmlAttribute(name = "id")
    private String id;

    public String getSecurityPermissionSpec() {
        return securityPermissionSpec;
    }

    @XmlElement(name = "security-permission-spec", required = true)
    public void setSecurityPermissionSpec(String securityPermissionSpec) {
        this.securityPermissionSpec = securityPermissionSpec;
    }

    public List<RaDescription> getDescription() {
        return description;
    }

    @XmlElement(name = "description")
    public void setDescription(List<RaDescription> description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void copyRa10Settings(Ra10SecurityPermission secPermission) {
        if (secPermission.getDescription() != null) {
            RaDescription desc = new RaDescription();
            desc.setValue(secPermission.getDescription());
            description.add(desc);
        }
        securityPermissionSpec = secPermission.getSecurityPermissionSpec();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RaSecurityPermission{security-permission-spec='");
        if (securityPermissionSpec != null)
            sb.append(securityPermissionSpec).append("'}");
        return sb.toString();
    }
}
