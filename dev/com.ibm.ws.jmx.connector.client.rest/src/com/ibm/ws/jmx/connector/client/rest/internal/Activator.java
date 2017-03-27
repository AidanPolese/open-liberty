/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.jmx.connector.client.rest.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

@Component(configurationPolicy = ConfigurationPolicy.IGNORE)
public class Activator {

    private final static String PKGS_KEY = "jmx.remote.protocol.provider.pkgs";
    private final static String PKGS = "com.ibm.ws.jmx.connector.client";
    private final static Pattern P = Pattern.compile("(?:\\A|,)" + PKGS + "(?:\\z|,)");

    @Activate
    protected void activate() {

        String jmx = System.getProperty(PKGS_KEY);
        System.setProperty(PKGS_KEY, add(jmx));
    }

    @Deactivate
    protected void deactivate() {
        String jmx = System.getProperty(PKGS_KEY);
        if (PKGS.equals(jmx)) {
            System.clearProperty(PKGS_KEY);
        } else {
            System.setProperty(PKGS_KEY, remove(jmx));
        }
    }

    static String add(String jmx) {
        if (jmx == null) {
            return PKGS;
        } else {
            return jmx + "," + PKGS;
        }
    }

    static String remove(String jmx) {
        Matcher m = P.matcher(jmx);
        return m.replaceAll("");
    }

}
