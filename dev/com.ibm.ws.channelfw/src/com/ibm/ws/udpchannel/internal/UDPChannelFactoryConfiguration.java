//-------------------------------------------------------------------------------
//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2003, 2006
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//Change History:
//
//Change ID     Author    Abstract
//---------     --------  -------------------------------------------------------
//d306341		mjohn256  Add RAS logging support to UDP Channel.
//-------------------------------------------------------------------------------
package com.ibm.ws.udpchannel.internal;

import java.util.Map;

import com.ibm.websphere.channelfw.ChannelFactoryData;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.channelfw.exception.ChannelFactoryException;
import com.ibm.wsspi.udpchannel.UDPConfigConstants;

/**
 * @author mjohnson
 */
public class UDPChannelFactoryConfiguration {
    private static final TraceComponent tc = Tr.register(UDPChannelFactoryConfiguration.class, UDPMessages.TR_GROUP, UDPMessages.TR_MSGS);

    private ChannelFactoryData myConfig = null;
    private boolean uniqueWorkerThreads = true;

    /**
     * Constructor.
     * 
     * @param config
     * @throws ChannelFactoryException
     */
    public UDPChannelFactoryConfiguration(ChannelFactoryData config) throws ChannelFactoryException {
        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.entry(tc, "UDPChannelFactoryConfiguration");
        }
        this.myConfig = config;
        setValues();

        if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) {
            Tr.exit(tc, "UDPChannelFactoryConfiguration");
        }
    }

    private void setValues() throws ChannelFactoryException {
        for (Map.Entry<Object, Object> entry : this.myConfig.getProperties().entrySet()) {
            String key = (String) entry.getKey();

            try {
                if (key.equalsIgnoreCase(UDPConfigConstants.CHANNEL_FACTORY_UNIQUE_WORKER_THREADS)) {
                    setUniqueWorkerThreads(Boolean.parseBoolean((String) entry.getValue()));
                    continue;
                }
            } catch (Exception x) {
                ChannelFactoryException e = new ChannelFactoryException("UDP Channel Factory Caught an Exception processing property: " + " name: " + key + " value: "
                                                                        + entry.getValue(),
                                x);
                throw e;
            }
        }
    }

    /**
     * Access the raw property map for this factory.
     * 
     * @return Map<Object,Object>
     */
    public Map<Object, Object> getProperties() {
        return this.myConfig.getProperties();
    }

    /**
     * @return boolean
     */
    public boolean isUniqueWorkerThreads() {
        return this.uniqueWorkerThreads;
    }

    /**
     * @param flag
     */
    public void setUniqueWorkerThreads(boolean flag) {
        this.uniqueWorkerThreads = flag;
    }
}
