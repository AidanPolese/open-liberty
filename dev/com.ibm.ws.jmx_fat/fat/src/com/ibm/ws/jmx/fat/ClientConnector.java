package com.ibm.ws.jmx.fat;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

/**
 *
 */
public class ClientConnector {

    MBeanServerConnection mbsc = null;

    public ClientConnector() {
        int port = Integer.valueOf(System.getProperty("JMXTest", "8999"));
        String URL = "service:jmx:rmi:///jndi/rmi://localhost:" + port + "/server";
        System.out.println("JMX ClientConnector URL " + URL);

        JMXServiceURL url;
        try {
            url = new JMXServiceURL(URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        JMXConnector jmxc;
        try {
            jmxc = JMXConnectorFactory.connect(url, null);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        try {
            mbsc = jmxc.getMBeanServerConnection();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public MBeanServerConnection getMBeanServer() {
        return mbsc;
    }
}
