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
 */
package com.ibm.websphere.channelfw.osgi;

import java.util.Map;

import com.ibm.wsspi.channelfw.ChannelFactory;

/**
 * Bundles that provide channels can use this interface to inform the
 * channel framework of the ones this bundle provides. This allows a
 * lightweight service component to start up and notify the framework
 * of the channels provided, but not start anything else yet. The first
 * time something else performs a lookup against a factory-type from this
 * provider, the init() is called so the provider can initialize any
 * remaining components of the bundle.
 */
public interface ChannelFactoryProvider {

    /**
     * Query the type/factory matchs provided by this class. The keys are
     * the type names and the values are the factory class for each type.
     * 
     * @return Map<String,Class<? extends ChannelFactory>>
     */
    Map<String, Class<? extends ChannelFactory>> getTypes();

    /**
     * Called by the framework the first time a lookup is performed against
     * one of the factory-types handled by this provider. The provider may
     * then perform initialization actions, such as loading other service
     * components required for runtime.
     */
    void init();
}
