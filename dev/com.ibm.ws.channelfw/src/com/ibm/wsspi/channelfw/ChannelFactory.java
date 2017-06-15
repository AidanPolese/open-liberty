/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.wsspi.channelfw;

import java.util.Map;

import com.ibm.websphere.channelfw.ChannelData;
import com.ibm.websphere.channelfw.ChannelFactoryData;
import com.ibm.websphere.channelfw.OutboundChannelDefinition;
import com.ibm.wsspi.channelfw.exception.ChannelException;
import com.ibm.wsspi.channelfw.exception.ChannelFactoryException;
import com.ibm.wsspi.channelfw.exception.ChannelFactoryPropertyIgnoredException;

/**
 * The ChannelFactory is responsible for finding or creating a Channel
 * for a given named configuration. There is only Channel for each unique
 * configuration. The factory can be used to create Channel Data objects
 * that are ready to populate.
 * <p>
 * The ChannelFactory also gives the ideal place for resources bound to all
 * Channels. To help manage these kinds of global resources for a specific
 * Channel, this factory has 2 lifecycle methods. The init method will be called
 * before any Channels are created and the destroy method will be called once
 * all created channels are destroyed.
 * <p>
 * The ChannelFactory may be instantiated at any time and may not necessarily be
 * used to create Channels. Hypothetically, there could exist multiple
 * instantiations of the ChannelFactory implemented at once. However, there will
 * only be one instance of each ChannelFactory that will exist at once which has
 * been initialized and created channels. So, all global resources should be
 * created in the init method instead of in the constructor.
 * <p>
 * Because the ChannelFactory has a dual purpose and is designed for these
 * global resources such as threads or object pools, the ChannelFactory is able
 * to have a configuration of its own.
 */
public interface ChannelFactory {
    /**
     * Find or create the channel with the specified channel named config.
     * There should be only one channel for each unique name.
     * 
     * @param config
     *            The Channel's configuration data to be found or created.
     * @return Channel
     * @throws ChannelException
     *             if unable to find or create a channel.
     */
    Channel findOrCreateChannel(ChannelData config) throws ChannelException;

    /**
     * Overlay the common properties that are to be shared by all channel
     * instances
     * generated by this factory.
     * 
     * @param properties
     *            common channel properties
     * @throws ChannelFactoryPropertyIgnoredException
     *             if one or more of the properties are rejected
     */
    void updateProperties(Map<Object, Object> properties) throws ChannelFactoryPropertyIgnoredException;

    /**
     * Initializes the channel factory and the resources associated with it.
     * Combined with destroy, encompasses the lifecycle
     * of the channel factory.
     * 
     * @param data
     *            Input data including properties to be initialized with.
     * @throws ChannelFactoryException
     *             implementations of
     *             this interface should throw this exception if they get
     *             initialized twice or if there are problems in the properties
     *             passed in.
     * 
     */
    void init(ChannelFactoryData data) throws ChannelFactoryException;

    /**
     * Destroys the channel factory and its associated resources (if they are no
     * longer in use). Notifies
     * the channel factory that the framework will no longer use its instance and
     * will create a
     * new instance if it need one.
     * 
     */
    void destroy();

    /**
     * Get the common properties that are to be shared by all channel instances
     * generated by this factory.
     * 
     * @return common properties
     */
    Map<Object, Object> getProperties();

    /**
     * Returns the interface into this channel. Return the interface class that
     * this channel presents to adjacent
     * channels on its application side.
     * <p>
     * If this is a ChannelFactory for an Application Channel, this interface
     * should simply return null.
     * 
     * @return Class
     */
    Class<?> getApplicationInterface();

    /**
     * Return the list of device interface classes supported. These
     * interfaces represent the type of objects that can be passed from
     * an adjacted channel to this channel on its device side.
     * <p>
     * If this ChannelFactory is for a Connector Channel, this interface should
     * simply return null.
     * 
     * @return Class []
     */
    Class<?>[] getDeviceInterface();

    /**
     * Return a definition of an outbound channel that can be used on a client
     * side to communicate with inbound channels that are created by this
     * factory on the server side. If this channel factory is only used to
     * create outbound channel instances, this method should return null.
     * If the factory generates channels that would not be used in an
     * outbound chain (i.e. application channels), then this should return
     * null.
     * 
     * @param props
     *            associated with the inbound channel
     * @return OutboundChannelDefinition
     */
    OutboundChannelDefinition getOutboundChannelDefinition(Map<Object, Object> props);
}
