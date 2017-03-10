/*
 * IBM Confidential
 * 
 * OCO Source Materials
 * 
 * Copyright IBM Corp. 2009
 * 
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 * 
 * Change activity:
 * 
 * Issue Date Name Description
 * ----------- ----------- -------- ------------------------------------
 * 
 */

package com.ibm.ws.config.xml.internal;

import java.util.Collections;
import java.util.List;

import com.ibm.ws.config.xml.internal.ConfigComparator.DeltaType;
import com.ibm.ws.config.xml.internal.MetaTypeRegistry.RegistryEntry;

class ConfigDelta {

    private final ConfigElement configElement;
    private final DeltaType delta;
    private final RegistryEntry registryEntry;
    private final List<ConfigDelta> nestedDelta;

    public static enum REASON {
        PROPERTIES_UPDATE, NESTED_UPDATE_ONLY
    };

    private final REASON reason;

    public ConfigDelta(ConfigElement configElement, DeltaType delta, List<ConfigDelta> nestedDelta, RegistryEntry registryEntry, REASON r) {
        this.configElement = configElement;
        this.delta = delta;
        this.nestedDelta = nestedDelta;
        this.registryEntry = registryEntry;
        this.reason = r;
    }

    public ConfigElement getConfigElement() {
        return configElement;
    }

    public DeltaType getDelta() {
        return delta;
    }

    public List<ConfigDelta> getNestedDelta() {
        return (nestedDelta == null) ? Collections.<ConfigDelta> emptyList() : nestedDelta;
    }

    public RegistryEntry getRegistryEntry() {
        return registryEntry;
    }

    @Override
    public String toString() {
        return "ConfigDelta[delta=" + delta + ", configElement=" + configElement.getFullId() + "]";
    }

    /**
     * @return the reason
     */
    public REASON getReason() {
        return reason;
    }

}
