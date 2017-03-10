/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013, 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.logging.internal;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.framework.Bundle;

/**
 * Yet Another Manifest Parser (YAMP) - we're only doing simple parsing here, and don't
 * want a dependency on anything higher up the food chain like aries util.
 */
public class BundleManifest {

    private final Set<String> privatePackages = new HashSet<String>();
    private final Set<String> exportedPackages = new HashSet<String>();
    private final Dictionary<String, String> headers;
    private final String vendor;
    private final String distributor;

    public BundleManifest(Bundle bundle) {
        headers = bundle.getHeaders("");
        vendor = headers.get("Bundle-Vendor");
        distributor = headers.get("Bundle-Distributor");
    }

    public Set<String> getPrivatePackages() {
        if (privatePackages.isEmpty()) {
            String privatePackagesHeader = headers.get("Private-Package");
            parseHeader(privatePackagesHeader, privatePackages);
        }
        return privatePackages;
    }

    public Set<String> getExportedPackages() {
        if (exportedPackages.isEmpty()) {
            String exportedPackagesHeader = headers.get("Export-Package");
            parseHeader(exportedPackagesHeader, exportedPackages);
        }
        return exportedPackages;
    }

    /**
     * This algorithm is nuts. Bnd has a nice on in OSGIHeader.
     *
     * @param header
     * @param packages
     */
    void parseHeader(String header, Set<String> packages) {
        if (header != null && header.length() > 0) {
            List<String> splitPackages = new ArrayList<String>();
            // We can't just split() on commas, because there may be things in quotes, like uses clauses
            int lastIndex = 0;
            boolean inQuotedSection = false;
            int i = 0;
            for (i = 0; i < header.length(); i++) {
                if ((header.charAt(i) == ',') && !(inQuotedSection)) {
                    String packagePlusAttributesAndDirectives = header.substring(lastIndex, i);
                    lastIndex = i + 1;
                    splitPackages.add(packagePlusAttributesAndDirectives);
                } else if (header.charAt(i) == '"') {
                    inQuotedSection = !inQuotedSection;
                }
            }
            // Add the last string
            splitPackages.add(header.substring(lastIndex, i));

            // Now go through and handle the attributes

            for (String packagePlusAttributesAndDirectives : splitPackages) {
                String[] bits = packagePlusAttributesAndDirectives.split(";");
                // We could also process ibm-api-type and other declarations here to get better information if we need it
                packages.add(bits[0]);

            }
        }
    }

    /**
     * @return
     */
    public String getBundleVendor() {
        return vendor;
    }

    public Object getBundleDistributor() {
        return distributor;
    }
}
