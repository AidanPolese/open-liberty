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
package com.ibm.ws.javaee.ddmodel.permissions;

import com.ibm.ws.javaee.dd.permissions.PermissionsConfig;
import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.Entry;

final class PermissionsConfigDDParser extends DDParser {

    public PermissionsConfigDDParser(Container ddRootContainer, Entry ddEntry) throws ParseException {
        super(ddRootContainer, ddEntry);
    }

    PermissionsConfig parse() throws ParseException {
        super.parseRootElement();
        return (PermissionsConfig) rootParsable;
    }

    @Override
    protected ParsableElement createRootParsable() throws ParseException {
        if (!"permissions".equals(rootElementLocalName)) {
            throw new ParseException(invalidRootElement());
        }
        String vers = getAttributeValue("", "version");

        if ("http://xmlns.jcp.org/xml/ns/javaee".equals(namespace)) {
            if ("7".equals(vers)) {
                // javaee 7 only
                version = 70;
                return new PermissionsConfigType(getDeploymentDescriptorPath());
            }
        }
        throw new ParseException(invalidDeploymentDescriptorNamespace(vers));
    }
}
