/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.config.admin;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public interface SystemConfigSupport {

    ExtendedConfiguration lookupConfiguration(ConfigID referenceId);

    Set<ConfigID> getReferences(ConfigID configId);

    void registerConfiguration(ConfigID configId, ExtendedConfiguration config);

    ExtendedConfiguration findConfiguration(String alias);

    boolean waitForAll(Collection<Future<?>> endingFuturesForChanges, long timeout, TimeUnit unit);

    void openManagedServiceTrackers();

    void fireMetatypeRemovedEvent(String pid);

    void fireMetatypeAddedEvent(String pid);
}
