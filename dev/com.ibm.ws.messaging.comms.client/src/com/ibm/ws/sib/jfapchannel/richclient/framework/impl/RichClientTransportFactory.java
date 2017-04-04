/*
 * @start_prolog@
 * Version: @(#) 1.1 SIB/ws/code/sib.jfapchannel.client.rich.impl/src/com/ibm/ws/sib/jfapchannel/framework/impl/RichClientTransportFactory.java, SIB.comms, WASX.SIB, uu1215.01 06/09/14 10:10:13 [4/12/12 22:14:19]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61  (C) Copyright IBM Corp. 2003, 2006 
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * SIB0048b.com.5  060913 mattheg  JFap channel for Portly client rework
 *  93606          031304 Venu     Multiple ssl/tcp options support for Outbound
 *  95897          020413 Venu     Dynamic modify support for Commsoutbound
 *  95897          041613 Chetan   Comms Outbound Chain revamp
 *  99283          220413 Chetan   Dynamic update of ssl feature leads to NPE
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.richclient.framework.impl;

import com.ibm.websphere.channelfw.CFEndPoint;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.framework.FrameworkException;
import com.ibm.ws.sib.jfapchannel.framework.NetworkConnectionFactory;
import com.ibm.ws.sib.jfapchannel.framework.NetworkTransportFactory;
import com.ibm.ws.sib.jfapchannel.impl.CommsClientServiceFacade;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.channelfw.ChannelFramework;
import com.ibm.wsspi.channelfw.VirtualConnectionFactory;

/**
 * An implementation of com.ibm.ws.sib.jfapchannel.framework.NetworkTransportFactory. It is the
 * top level class that provides access to the NetworkConnectionFactory classes from which
 * connections can be created.
 * 
 * @see com.ibm.ws.sib.jfapchannel.framework.NetworkTransportFactory
 * 
 * @author Gareth Matthews
 */
public class RichClientTransportFactory implements NetworkTransportFactory
{
    /** Trace */
    private static final TraceComponent tc = SibTr.register(RichClientTransportFactory.class,
                                                            JFapChannelConstants.MSG_GROUP,
                                                            JFapChannelConstants.MSG_BUNDLE);

    /** Class name for FFDC's */
    private static final String CLASS_NAME = RichClientTransportFactory.class.getName();

    /** Log class info on load */
    static
    {
        if (tc.isDebugEnabled())
            SibTr.debug(tc,
                        "@(#) SIB/ws/code/sib.jfapchannel.client.rich.impl/src/com/ibm/ws/sib/jfapchannel/framework/impl/RichClientTransportFactory.java, SIB.comms, WASX.SIB, uu1215.01 1.1");
    }

    /** Local reference to the channel framework */
    private ChannelFramework channelFramework = null;

    /**
     * Constructor.
     * 
     * @param channelFramework
     */
    public RichClientTransportFactory(ChannelFramework channelFramework)
    {
        if (tc.isEntryEnabled())
            SibTr.entry(this, tc, "<init>", channelFramework);
        this.channelFramework = channelFramework;
        if (tc.isEntryEnabled())
            SibTr.exit(tc, "<init>");
    }

    /**
     * @see com.ibm.ws.sib.jfapchannel.framework.NetworkTransportFactory#getOutboundNetworkConnectionFactoryByName(java.lang.String)
     */
    @Override
    public NetworkConnectionFactory getOutboundNetworkConnectionFactoryByName(String chainName) throws FrameworkException
    {
        if (tc.isEntryEnabled())
            SibTr.entry(this, tc, "getOutboundNetworkConnectionFactoryByName", chainName);

        NetworkConnectionFactory connFactory = null;

        try
        {
            // Get the virtual connection factory from the channel framework using the chain name

            VirtualConnectionFactory vcFactory = CommsClientServiceFacade.getChannelFramewrok().getOutboundVCFactory(chainName);

            connFactory = new CFWNetworkConnectionFactory(vcFactory);

        } catch (com.ibm.wsspi.channelfw.exception.ChannelException e) {

            FFDCFilter.processException(e, CLASS_NAME + ".getOutboundNetworkConnectionFactoryByName",
                                        JFapChannelConstants.RICHCLIENTTRANSPORTFACT_GETCONNFACBYNAME_01,
                                        new Object[] { this, chainName });

            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, "Failure to obtain OVC for Outbound chain" + chainName, e);

            throw new FrameworkException(e);
        } catch (com.ibm.wsspi.channelfw.exception.ChainException e) {

            FFDCFilter.processException(e, CLASS_NAME + ".getOutboundNetworkConnectionFactoryByName",
                                        JFapChannelConstants.RICHCLIENTTRANSPORTFACT_GETCONNFACBYNAME_01,
                                        new Object[] { this, chainName });
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled())
                SibTr.debug(tc, "Failure to obtain OVC for Outbound chain" + chainName, e);

            throw new FrameworkException(e);
        }

        if (tc.isEntryEnabled())
            SibTr.exit(this, tc, "getOutboundNetworkConnectionFactoryByName", connFactory);
        return connFactory;
    }

    /**
     * @see com.ibm.ws.sib.jfapchannel.framework.NetworkTransportFactory#getOutboundNetworkConnectionFactoryFromEndPoint(java.lang.Object)
     */
    @Override
    public NetworkConnectionFactory getOutboundNetworkConnectionFactoryFromEndPoint(Object endPoint)
    {
        if (tc.isEntryEnabled())
            SibTr.entry(this, tc, "getOutboundNetworkConnectionFactoryFromEndPoint", endPoint);

        NetworkConnectionFactory connFactory = null;
        if (endPoint instanceof CFEndPoint)
        {
            // Get the virtual connection factory from the EP and wrap it in our implementation of
            // the NetworkConnectionFactory interface
            VirtualConnectionFactory vcFactory = ((CFEndPoint) endPoint).getOutboundVCFactory();
            connFactory = new CFWNetworkConnectionFactory(vcFactory);
        }

        if (tc.isEntryEnabled())
            SibTr.exit(this, tc, "getOutboundNetworkConnectionFactoryFromEndPoint", connFactory);
        return connFactory;
    }

}
