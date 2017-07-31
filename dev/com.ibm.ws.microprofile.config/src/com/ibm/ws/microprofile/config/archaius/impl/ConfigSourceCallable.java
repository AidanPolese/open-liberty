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
package com.ibm.ws.microprofile.config.archaius.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.eclipse.microprofile.config.spi.ConfigSource;

import com.netflix.archaius.config.polling.PollingResponse;

/**
 *
 */
public class ConfigSourceCallable implements Callable<PollingResponse> {

    private final ConfigSource source;

    /**
     * Constructor
     *
     * @param source
     */
    public ConfigSourceCallable(ConfigSource source) {
        this.source = source;
    }

    /** {@inheritDoc} */
    @Override
    public PollingResponse call() throws Exception {
        return new PollingResponse() {

            @Override
            public boolean hasData() {
                return true;
            }

            @Override
            public Collection<String> getToRemove() {
                return Collections.emptySet();
            }

            @Override
            public Map<String, String> getToAdd() {
                Map<String, String> toAdd = new HashMap<>();
                Map<String, String> props = source.getProperties();
                if (props != null) {
                    toAdd.putAll(props);
                }
                return toAdd;
            }
        };
    }

}
