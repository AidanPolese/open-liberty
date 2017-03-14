//* ===========================================================================
//*
//* IBM SDK, Java(tm) 2 Technology Edition, v5.0
//* (C) Copyright IBM Corp. 2005, 2006
//*
//* The source code for this program is not published or otherwise divested of
//* its trade secrets, irrespective of what has been deposited with the U.S.
//* Copyright office.
//*
//* ===========================================================================
//
//@(#) 1.4 SERV1/ws/code/channelfw.impl/src/com/ibm/ws/channel/framework/impl/ChildChannelDataImpl.java, WAS.channelfw, WASX.SERV1 5/10/04 22:24:56 [1/4/05 10:07:26]

package com.ibm.ws.channelfw.internal;

import java.util.Map;

import com.ibm.websphere.channelfw.ChannelData;

/**
 * This class represents the channel data objects referenced by the runtime
 * channels in the framework. The plain ChannelDataImpl object are considered
 * the parents. The purpose of this is to support chain convergence. That is,
 * allowing two chains to converge at a channel. To the user, there will be
 * one channel to configure, but in the framework, there will be multiple
 * channel instances, child instances. The data will be in the parent, but the
 * name and a reference to the parent will be here.
 */
public class ChildChannelDataImpl implements ChannelData {

    /** Serialization ID string */
    private static final long serialVersionUID = -220865176896833236L;

    /** Parent channel data object */
    private ChannelDataImpl parent = null;
    /** Name of this child channel */
    private String name = null;
    /** Whether the channel represented by this data is inbound or outbound. */
    private boolean isInbound = true;

    /**
     * Constructor.
     * 
     * @param inputName
     * @param inputParent
     */
    public ChildChannelDataImpl(String inputName, ChannelDataImpl inputParent) {
        super();
        this.name = inputName;
        this.parent = inputParent;
    }

    /**
     * Accessor method for parent channel data object.
     * 
     * @return parent channel data
     */
    public ChannelDataImpl getParent() {
        return this.parent;
    }

    /**
     * @see com.ibm.websphere.channelfw.ChannelData#getName()
     */
    public String getName() {
        return this.name;
    }

    /**
     * @see com.ibm.websphere.channelfw.ChannelData#getFactoryType()
     */
    public Class<?> getFactoryType() {
        return this.parent.getFactoryType();
    }

    /**
     * @see com.ibm.websphere.channelfw.ChannelData#getPropertyBag()
     */
    public Map<Object, Object> getPropertyBag() {
        return this.parent.getPropertyBag();
    }

    /**
     * @see com.ibm.websphere.channelfw.ChannelData#getDiscriminatorWeight()
     */
    public int getDiscriminatorWeight() {
        return this.parent.getDiscriminatorWeight();
    }

    /**
     * Access the device interface definition.
     * 
     * @return Class<?>
     */
    public Class<?> getDeviceInterface() {
        return this.parent.getDeviceInterface();
    }

    /**
     * Set the device side interface. This is called right before a channel is
     * created.
     * 
     * @param inputDeviceInterface
     */
    public void setDeviceInterface(Class<?> inputDeviceInterface) {
        this.parent.setDeviceInterface(inputDeviceInterface);
    }

    /**
     * @see com.ibm.websphere.channelfw.ChannelData#isInbound()
     */
    public boolean isInbound() {
        return this.isInbound;
    }

    /**
     * @see com.ibm.websphere.channelfw.ChannelData#isOutbound()
     */
    public boolean isOutbound() {
        return !this.isInbound;
    }

    /**
     * This is called when a chain is being built and the caller knows for sure
     * whether
     * this channel is being used for inbound or outbound.
     * 
     * @param flag
     */
    public void setIsInbound(boolean flag) {
        this.isInbound = flag;
    }

    /**
     * @see ChannelData#getExternalName()
     */
    public String getExternalName() {
        return this.parent.getName();
    }
}
