/*
 * @start_prolog@
 * Version: @(#) 1.22 SIB/ws/code/sib.jfapchannel.client.rich.impl/src/com/ibm/ws/sib/jfapchannel/impl/JFapChannelFactory.java, SIB.comms, WASX.SIB, uu1215.01 06/10/02 04:33:05 [4/12/12 22:14:17]
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
 * Creation        030424 prestona Original
 * F166959         030521 prestona Rebase on non-prototype CF + TCP Channel
 * F167363         030523 prestona Rebase on LIBD_1891_2255 CF + TCP Channel
 * F177053         030917 prestona Rebase JFAP Channel on pre-M4 CF + TCP
 * D181601         031031 prestona Improve quality of JFAP Channel RAS
 * F184828         031204 prestona Update CF + TCP prereqs to MS 5.1 level
 * F188491         040128 prestona Migrate to M6 CF + TCP Channel
 * F189000         040130 prestona Expose WLM endpoints through CF
 * D194678         040317 mattheg  Migrate to M7 CF + TCP Channel
 * D196658         040331 mattheg  Allow outbound chains to be started in new M7 CFW
 * F196678.10      040426 prestona JS Client Administration
 * D196678.10.1    040525 prestona Insufficient chain information passed to TRM
 * D199145         040812 prestona Fix Javadoc
 * D232185         041007 mattheg  Serviceability improvements
 * SIB0048b.com.4  060905 mattheg  Re-packaging into client / server
 * ============================================================================ 
 */
package com.ibm.ws.sib.jfapchannel.richclient.impl;

import java.lang.reflect.Constructor;
import java.util.Map;

import com.ibm.websphere.channelfw.ChannelData;
import com.ibm.websphere.channelfw.OutboundChannelDefinition;
import com.ibm.wsspi.channelfw.ChannelFactory;
import com.ibm.websphere.channelfw.ChannelFactoryData;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.channelfw.Channel;
import com.ibm.wsspi.channelfw.exception.ChannelException;
import com.ibm.wsspi.channelfw.exception.ChannelFactoryException;
import com.ibm.wsspi.channelfw.exception.ChannelFactoryPropertyIgnoredException;

/**
 * Factory class for JFAP channels.  Part of the code required to work in the channel framework.
 * @author prestona
 */
public class JFapChannelFactory implements ChannelFactory                                // D196658
{
   private static final TraceComponent tc = SibTr.register(JFapChannelFactory.class, 
                                                           JFapChannelConstants.MSG_GROUP, 
                                                           JFapChannelConstants.MSG_BUNDLE);

   static
   {
      if (tc.isDebugEnabled()) SibTr.debug(tc, "@(#) SIB/ws/code/sib.jfapchannel.client.rich.impl/src/com/ibm/ws/sib/jfapchannel/impl/JFapChannelFactory.java, SIB.comms, WASX.SIB, uu1215.01 1.22");
   }

   /** Constant for channel properties map to pass in connection table */
   public static final String CONNECTION_TABLE = "jfapchannel.CONNECTION_TABLE";

   /** Constant for channel properties map to pass in accept listener */
   public static final String ACCEPT_LISTENER = "jfapchannel.ACCEPT_LISTENER";
   
   /** Array to pass to out on getDeviceInterface() */
   private Class[] devSideInterfaceClasses = null;                                        // D232185
	
   private ChannelFactoryData channelFactoryData;                          // D196678.10.1
   
   /**
    * Creates a new channel.  Uses channel configuration to determine if the channel should be
    * inbound or outbound.
    * @see BaseChannelFactory#createChannel(com.ibm.websphere.channelfw.ChannelData)
    */
   protected Channel createChannel(ChannelData config) throws ChannelException   
   {
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "createChannel", config);
      
      Channel retChannel;
      if (config.isInbound())
      {
         if (tc.isDebugEnabled()) SibTr.debug(this, tc, "createChannel", "inbound");
         try
         {
            Class clazz = Class.forName(JFapChannelConstants.INBOUND_CHANNEL_CLASS);
            Constructor contruct = clazz.getConstructor(new Class[] 
                                                        {
                                                           ChannelFactoryData.class, 
                                                           ChannelData.class 
                                                        });
            retChannel = (Channel) contruct.newInstance(new Object[] 
                                                        { 
                                                           channelFactoryData, 
                                                           config 
                                                        });
         }
         catch (Exception e)
         {
            FFDCFilter.processException(e, 
                                        "com.ibm.ws.sib.jfapchannel.impl.JFapChannelFactory.createChannel",  
                                        JFapChannelConstants.JFAPCHANNELFACT_CREATECHANNEL_01,
                                        this);
            
            if (tc.isDebugEnabled()) SibTr.debug(this, tc, "Unable to instantiate inbound channel", e);

            // Rethrow as a channel exception
            throw new ChannelException(e);
         }
      }
      else
      {
         retChannel = new JFapChannelOutbound(channelFactoryData, config);
      }
      
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "createChannel", retChannel);
      return retChannel;
   }
   
   /**
    * Returns the device side interfaces which our channels can work with.
    * This is always the TCPServiceContext.
    */
   public Class[] getDeviceInterface()                                    // F177053
   {
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "getDeviceInterface");        // F177053

      // Start D232185
      if (devSideInterfaceClasses == null)
      {
         devSideInterfaceClasses = new Class[1];
         devSideInterfaceClasses[0] = com.ibm.wsspi.tcpchannel.TCPConnectionContext.class;   // f167363, F184828, F189000
      }
      // End D232185
      
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "getDeviceInterface", devSideInterfaceClasses);        // F177053
      return devSideInterfaceClasses;
   }

   // begin F177053
   public void init(ChannelFactoryData properties) throws ChannelFactoryException         // D194678
   {
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "init", properties);
      channelFactoryData = properties;                                                    // D196678.10.1
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "init");
   }
   // end F177053
   
   // begin F177053    
   public void destroy()
   {
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "destroy");
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "destroy");
   }
   // end F177053   
   
   // begin D196658
   public Class getApplicationInterface()
   {
      if (tc.isEntryEnabled()) SibTr.entry(this, tc, "getApplicationInterface");
      if (tc.isEntryEnabled()) SibTr.exit(this, tc, "getApplicationInterface");
      return JFapChannelFactory.class;                                           // 196678.10
   }
   // end D196658

@Override
public Channel findOrCreateChannel(ChannelData config) throws ChannelException {
	// TODO Auto-generated method stub
   return new JFapChannelOutbound(channelFactoryData, config);
}

@Override
public OutboundChannelDefinition getOutboundChannelDefinition(
		Map<Object, Object> props) {
	// TODO Auto-generated method stub
	return null;
}

@Override
public Map<Object, Object> getProperties() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public void updateProperties(Map<Object, Object> properties)
		throws ChannelFactoryPropertyIgnoredException {
	// TODO Auto-generated method stub
	
}
}
