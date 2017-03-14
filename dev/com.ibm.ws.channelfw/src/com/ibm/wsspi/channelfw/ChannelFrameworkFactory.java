// * ===========================================================================
// *
// * IBM SDK, Java(tm) 2 Technology Edition, v5.0
//* (C) Copyright IBM Corp. 2005, 2006
// *
// * The source code for this program is not published or otherwise divested of
// * its trade secrets, irrespective of what has been deposited with the U.S.
// * Copyright office.
// *
// * ===========================================================================
//
//Change History:
//
//Date      UserId      Defect      Description
//-------------------------------------------------------------------------------
//08/20/05  clanzen     299300      File creation.  Ensure single CF instance in JVM.
//-------------------------------------------------------------------------------

package com.ibm.wsspi.channelfw;

import com.ibm.ws.bytebuffer.internal.WsByteBufferPoolManagerImpl;
import com.ibm.ws.channelfw.internal.ChannelFrameworkImpl;
import com.ibm.wsspi.bytebuffer.WsByteBufferPoolManager;

/**
 * Factory access for the channel framework. This must be a singleton object per
 * JVM; however, can be accessed from numerous types of callers.
 * 
 */
public class ChannelFrameworkFactory {

    /**
     * Access the channel framework instance for this JVM.
     * 
     * @return ChannelFramework
     */
    public static ChannelFramework getChannelFramework() {
        return ChannelFrameworkImpl.getRef();
    }

    /**
     * Access the WSByteBuffer pool manager instance.
     * 
     * @return WsByteBufferPoolManager
     */
    public static WsByteBufferPoolManager getBufferManager() {
        return WsByteBufferPoolManagerImpl.getRef();
    }
}
