/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.management.j2ee.mbeans.internal;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 *
 */
public class MBeanServerHelper {

    private static final MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

    public static List<String> queryObjectName(final ObjectName objectName) {
        //Query the set of object instances
        Set<ObjectName> objectNameSet = mbeanServer.queryNames(objectName, null);

        final List<String> returnedObjectNames = new ArrayList<String>();
        for (ObjectName server : objectNameSet) {
            returnedObjectNames.add(server.toString());
        }

        return returnedObjectNames;
    }
}
