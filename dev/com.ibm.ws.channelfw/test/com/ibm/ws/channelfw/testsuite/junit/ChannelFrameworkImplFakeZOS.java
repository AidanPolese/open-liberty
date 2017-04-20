package com.ibm.ws.channelfw.testsuite.junit;

import com.ibm.websphere.channelfw.RegionType;
import com.ibm.ws.channelfw.internal.ChannelFrameworkImpl;

/**
 * Test class for pretending to be running on z/OS.
 */
public class ChannelFrameworkImplFakeZOS extends ChannelFrameworkImpl {

    private boolean onZ = false;
    private int currentRegion = RegionType.SR_REGION;

    /**
     * Constructor.
     */
    public ChannelFrameworkImplFakeZOS() {
        super();
    }

    protected void setOnZ(boolean value) {
        onZ = value;
    }

    protected void setCurrentRegion(int value) {
        currentRegion = value;
    }

    /**
     * Determines if we are running on a Z/OS system.
     * If we are running on WAS, then this will be overridden by
     * com.ibm.ws.channel.framework.WSChannelFrameworkImpl.currentlyOnZ()
     * If we are not on WAS, assume that we are not on Z, and return false
     * 
     * @return if true we are running on Z/OS, else false
     */
    protected boolean currentlyOnZ() {
        return onZ;
    }

    /**
     * Determines what Z region we are running on.
     * If we are running on WAS, then this will be overridden by
     * com.ibm.ws.channel.framework.WSChannelFrameworkImpl.currentlyZRegion()
     * If we are not on WAS, then this function should never be called, but put it
     * here for testing purposes and for compiling.
     * 
     * @return the constand defining the Z Region thate we are running on
     */
    protected int currentZRegion() {

        return currentRegion;
    }

}
