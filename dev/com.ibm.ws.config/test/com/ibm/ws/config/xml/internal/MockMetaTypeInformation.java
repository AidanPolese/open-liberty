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
package com.ibm.ws.config.xml.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.equinox.metatype.EquinoxMetaTypeInformation;
import org.eclipse.equinox.metatype.EquinoxObjectClassDefinition;
import org.osgi.framework.Bundle;

/**
 *
 */
public class MockMetaTypeInformation implements EquinoxMetaTypeInformation {

    private final Map<String, EquinoxObjectClassDefinition> pidOCDMap = new HashMap<String, EquinoxObjectClassDefinition>();
    private final List<String> pids = new ArrayList<String>();
    private final List<String> factoryPids = new ArrayList<String>();
    private final Bundle bundle;

    public MockMetaTypeInformation() {
        this(null);
    }

    public MockMetaTypeInformation(Bundle bundle) {
        this.bundle = bundle;
    }

    public void add(String pid, boolean isFactory, EquinoxObjectClassDefinition ocd) {
        pidOCDMap.put(pid, ocd);
        if (isFactory) {
            factoryPids.add(pid);
        } else {
            pids.add(pid);
        }
    }

    @Override
    public EquinoxObjectClassDefinition getObjectClassDefinition(String id, String locale) {
        return pidOCDMap.get(id);
    }

    @Override
    public String[] getLocales() {
        return null;
    }

    @Override
    public String[] getPids() {
        return pids.toArray(new String[pids.size()]);
    }

    @Override
    public String[] getFactoryPids() {
        return factoryPids.toArray(new String[factoryPids.size()]);
    }

    @Override
    public Bundle getBundle() {
        return bundle;
    }

}
