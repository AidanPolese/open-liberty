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
package com.ibm.ws.kernel.feature.internal.cmdline;

import com.ibm.ws.kernel.feature.provisioning.FeatureResource;

public enum APIType {
    API("dev/api", "apiJar"),
    SPI("dev/spi", "spiJar");

    public final String prefix;
    public final String attribute;

    APIType(String prefix, String attribute) {
        this.prefix = prefix;
        this.attribute = attribute;
    }

    public boolean matches(FeatureResource resource) {
        String attrValue = resource.getAttributes().get(attribute);
        return attrValue == null || Boolean.parseBoolean(attrValue);
    }

    public String getElementName() {
        // The feature list element name is the same as the feature manifest
        // attribute name.
        return attribute;
    }

    public static APIType getAPIType(FeatureResource resource) {
        String location = resource.getLocation();
        if (location != null) {
            location = location.trim();

            for (APIType apiType : APIType.values()) {
                if (location.startsWith(apiType.prefix)) {
                    return apiType.matches(resource) ? apiType : null;
                }
            }
        }

        return null;
    }
}
