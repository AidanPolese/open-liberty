/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel.client;

import com.ibm.ws.javaee.dd.client.ApplicationClient;
import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.Entry;

public class ApplicationClientDDParser extends DDParser {
    private final int maxVersion;

    public ApplicationClientDDParser(Container ddRootContainer, Entry ddEntry, int maxVersion) throws ParseException {
        super(ddRootContainer, ddEntry);
        this.maxVersion = maxVersion;
    }

    ApplicationClient parse() throws ParseException {
        super.parseRootElement();
        return (ApplicationClient) rootParsable;
    }

    private static final String APPCLIENT_DTD_PUBLIC_ID_12 = "-//Sun Microsystems, Inc.//DTD J2EE Application Client 1.2//EN";
    private static final String APPCLIENT_DTD_PUBLIC_ID_13 = "-//Sun Microsystems, Inc.//DTD J2EE Application Client 1.3//EN";

    @Override
    protected ParsableElement createRootParsable() throws ParseException {
        if (!"application-client".equals(rootElementLocalName)) {
            throw new ParseException(invalidRootElement());
        }
        String vers = getAttributeValue("", "version");
        if (vers == null) {
            if (namespace == null && dtdPublicId != null) {
                if (APPCLIENT_DTD_PUBLIC_ID_12.equals(dtdPublicId)) {
                    version = ApplicationClient.VERSION_1_2;
                    eePlatformVersion = 12;
                    return new ApplicationClientType(getDeploymentDescriptorPath());
                }
                if (APPCLIENT_DTD_PUBLIC_ID_13.equals(dtdPublicId)) {
                    version = ApplicationClient.VERSION_1_3;
                    eePlatformVersion = 13;
                    return new ApplicationClientType(getDeploymentDescriptorPath());
                }
            }
            throw new ParseException(unknownDeploymentDescriptorVersion());
        }
        if ("1.4".equals(vers)) {
            if ("http://java.sun.com/xml/ns/j2ee".equals(namespace)) {
                version = ApplicationClient.VERSION_1_4;
                eePlatformVersion = 14;
                return new ApplicationClientType(getDeploymentDescriptorPath());
            }
        }
        else if ("5".equals(vers)) {
            if ("http://java.sun.com/xml/ns/javaee".equals(namespace)) {
                version = ApplicationClient.VERSION_5;
                eePlatformVersion = 50;
                return new ApplicationClientType(getDeploymentDescriptorPath());
            }
        }
        else if ("6".equals(vers)) {
            if ("http://java.sun.com/xml/ns/javaee".equals(namespace)) {
                version = ApplicationClient.VERSION_6;
                eePlatformVersion = 60;
                return new ApplicationClientType(getDeploymentDescriptorPath());
            }
        }
        else if (maxVersion >= ApplicationClient.VERSION_7 && "7".equals(vers)) {
            if ("http://xmlns.jcp.org/xml/ns/javaee".equals(namespace)) {
                version = ApplicationClient.VERSION_7;
                eePlatformVersion = 70;
                return new ApplicationClientType(getDeploymentDescriptorPath());
            }
        }
        throw new ParseException(invalidDeploymentDescriptorNamespace(vers));
    }
}
