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
package com.ibm.ws.jca.utils.xml.ra.v10;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 */
@XmlType(name = "authenticationMechanismType", propOrder = { "description", "authMechType", "credentialInterface" })
public class Ra10AuthenticationMechanism {

    private static final String PasswordCredential = "javax.resource.spi.security.PasswordCredential";
    private static final String GenericCredential = "javax.resource.spi.security.GenericCredential";
    private static final Set<String> credentialInterfaceTypes;

    private enum AuthenticationMechanismType {
        BasicPassword,
        Kerbv5
    };

    static {
        Set<String> types = new HashSet<String>();
        types.add(PasswordCredential);
        types.add(GenericCredential);
        credentialInterfaceTypes = Collections.unmodifiableSet(types);
    }

    private String description;
    private String authMechType;
    private String credentialInterface;

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    @XmlElement(name = "description")
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the authMechType
     */
    public String getAuthMechType() {
        return authMechType;
    }

    @XmlElement(name = "auth-mech-type", required = true)
    public void setAuthMechType(String authMechType) {
        AuthenticationMechanismType type = AuthenticationMechanismType.valueOf(authMechType);
        this.authMechType = type.name();
    }

    /**
     * @return the credentialInterface
     */
    public String getCredentialInterface() {
        return credentialInterface;
    }

    @XmlElement(name = "credential-interface", required = true)
    public void setCredentialInterface(String credIntf) {
        if (credIntf == null || !credentialInterfaceTypes.contains(credIntf))
            throw new IllegalArgumentException("credential-interface: " + credIntf);
        credentialInterface = credIntf;
    }

}
