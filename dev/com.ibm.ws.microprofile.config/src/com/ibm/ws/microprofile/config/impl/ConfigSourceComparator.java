/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.microprofile.config.impl;

import java.util.Comparator;

import org.eclipse.microprofile.config.spi.ConfigSource;

public class ConfigSourceComparator implements Comparator<ConfigSource> {

    public static final ConfigSourceComparator INSTANCE = new ConfigSourceComparator();

    /** {@inheritDoc} */
    @Override
    public int compare(ConfigSource o1, ConfigSource o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return 1;
        }
        if (o2 == null) {
            return -1;
        }

        //we want highest ordinal first in the list
        int ord1 = o1.getOrdinal();
        int ord2 = o2.getOrdinal();

        if (ord2 > ord1) {
            return 1;
        }
        if (ord2 < ord1) {
            return -1;
        }
        //if the ordinals are equal, just compare the config source name
        String name1 = o1.getName();
        if (name1 == null) {
            return 1;
        }
        String name2 = o2.getName();
        if (name2 == null) {
            return -1;
        }
        if (name1.equals(name2)) {
            //arbitrary but repeatable order based on hashCode
            //TODO there is a really small possibility that this could still result in 0
            return (o1.hashCode() - o2.hashCode());
        }
        //string natural order
        return name1.compareTo(name2);
    }

}