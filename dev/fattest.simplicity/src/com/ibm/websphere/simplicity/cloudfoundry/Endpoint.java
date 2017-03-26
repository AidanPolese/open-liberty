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
package com.ibm.websphere.simplicity.cloudfoundry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 */
public final class Endpoint {

    private final String name;
    private final String contextRoot;

    public Endpoint(String appName) {
        this.name = appName;
        this.contextRoot = "";
    }

    public Endpoint(String appName, String contextRoot) {
        this.name = appName;
        this.contextRoot = contextRoot;
    }

    private String getURL() {
        return "http://" + this.name + ".stage1.mybluemix.net/" + this.contextRoot;
    }

    public String getHttpResponse() throws IOException {
        URL testURL = new URL(getURL());
        URLConnection connection = testURL.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer buffer = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            buffer.append(inputLine + " ");
        }
        in.close();

        return buffer.toString().trim();
    }

}
