/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.injectionengine.osgi.util;

import com.ibm.websphere.ras.annotation.Trivial;

/**
 * Helper to break apart an ejb-link or a message-destination-link.
 * The link can optionally be of the format moduleName#linkName.
 * 
 */
public class Link {

    /**
     * Parse a message-destination-link
     * 
     * @param origin original module name.
     * @param link link provided on the link element
     * @return
     */
    public static Link parseMessageDestinationLink(String origin, String link) {
        return parse(origin, link, false);
    }

    /**
     * Parse a link from an ejb-link or a message-destination-link
     * 
     * @param origin original module name.
     * @param link link provided on the link element
     * @param allowModuleName boolean if a module name is allowed on the link
     * @return
     */
    public static Link parse(String origin, String link, boolean allowModuleName) {
        String originMod = origin;
        int index = link.indexOf('#');
        if (index == -1) {
            if (allowModuleName) {
                index = link.lastIndexOf('/');
                if (index == -1) {
                    return new Link(null, null, link);
                }

                return new Link(null, link.substring(0, index), link.substring(index + 1));
            }

            return new Link(null, null, link);
        }

        String name = link.substring(index + 1);
        String uri = link.substring(0, index);
        if (originMod == null) {
            originMod = "";
        } else {
            index = originMod.lastIndexOf('/');
            originMod = index == -1 ? "" : originMod.substring(0, index);
        }

        if (uri.startsWith("../")) {
            uri = uri.substring(3);
        }

        while (uri.startsWith("../")) {
            index = originMod.lastIndexOf('/');
            if (index == -1) {
                originMod = "";
            } else {
                originMod = originMod.substring(0, index);
            }
            uri = uri.substring(3);
        }

        return new Link(originMod.isEmpty() ? uri : originMod + '/' + uri, null, name);
    }

    public final String moduleURI;
    public final String moduleName;
    public final String name;

    private Link(String moduleURI, String moduleName, String name) {
        this.moduleURI = moduleURI;
        this.moduleName = moduleName;
        this.name = name;
    }

    @Override
    @Trivial
    public String toString() {
        if (moduleURI != null) {
            return "[uri=" + moduleURI + ", name=" + name + ']';
        }

        if (moduleName != null) {
            return "[module=" + moduleName + ", name=" + name + ']';
        }

        return "[name=" + name + ']';
    }
}
