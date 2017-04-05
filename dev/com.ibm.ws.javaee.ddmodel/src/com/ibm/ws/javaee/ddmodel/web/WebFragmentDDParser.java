/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.ddmodel.web;

import com.ibm.ws.javaee.dd.web.WebApp;
import com.ibm.ws.javaee.dd.web.WebFragment;
import com.ibm.ws.javaee.ddmodel.DDParser;
import com.ibm.ws.javaee.ddmodel.web.common.WebFragmentType;
import com.ibm.wsspi.adaptable.module.Container;
import com.ibm.wsspi.adaptable.module.Entry;

/**
 *
 */
public class WebFragmentDDParser extends DDParser {

    private final int maxVersion;

    public WebFragmentDDParser(Container ddRootContainer, Entry ddEntry, int version) throws ParseException {
        super(ddRootContainer, ddEntry);
        trimSimpleContentAsRequiredByServletSpec = true;
        this.maxVersion = version;
    }

    WebFragment parse() throws ParseException {
        super.parseRootElement();
        return (WebFragment) rootParsable;
    }

    @Override
    protected ParsableElement createRootParsable() throws ParseException {
        if (!"web-fragment".equals(rootElementLocalName)) {
            throw new ParseException(invalidRootElement());
        }
        String vers = getAttributeValue("", "version");
        if (vers == null) {
            throw new ParseException(missingDeploymentDescriptorVersion());
        }

        if (maxVersion == 31)
            runtimeVersion = 70;
        else
            runtimeVersion = 60; //Servlet-3.0 is the earliest Liberty runtime spec.

        if ("3.0".equals(vers)) {
            if ("http://java.sun.com/xml/ns/javaee".equals(namespace)) {
                version = WebApp.VERSION_3_0;
                eePlatformVersion = 60;
                return new WebFragmentType(getDeploymentDescriptorPath());
            }
        } else if (maxVersion >= 31 && "3.1".equals(vers)) {
            if ("http://xmlns.jcp.org/xml/ns/javaee".equals(namespace)) {
                version = WebApp.VERSION_3_1;
                eePlatformVersion = 70;
                return new WebFragmentType(getDeploymentDescriptorPath());
            }
        }
        throw new ParseException(invalidDeploymentDescriptorNamespace(vers));
    }
}
