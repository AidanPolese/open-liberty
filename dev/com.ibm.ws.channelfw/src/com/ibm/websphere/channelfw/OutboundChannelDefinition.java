//IBM Confidential OCO Source Material
//5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 2003, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//%Z% %I% %W% %G% %U% [%H% %T%]
//
//HISTORY
//~~~~~~~
//
//Change ID    Author    Abstract
//---------    --------  ---------------------------------------------------
//
//======================================================================== */

package com.ibm.websphere.channelfw;

import java.io.Serializable;
import java.util.Map;

/**
 * This interface includes methods to query details about an outbound channel
 * that can be used to talk to the inbound channel that provides this.
 */
public interface OutboundChannelDefinition extends Serializable {

    /**
     * Access method for the channel factory that is required to build the
     * outbound channel defined by this interface.
     * 
     * @return class of the channel factory
     */
    Class<?> getOutboundFactory();

    /**
     * Access the properties of the channel factory required to build the
     * outbound channel defined by this interface. If no properties are
     * necessary, return null.
     * 
     * @return map of channel factory properties
     */
    Map<Object, Object> getOutboundFactoryProperties();

    /**
     * Access the properties required by the outbound channel represented
     * by this interface. If no properties are necessary, return null.
     * 
     * Note that any String based properties are expected to either be English
     * or UTF-8 encoded, other encodings are not supported for configuration
     * values.
     * 
     * @return map of channel properties
     */
    Map<Object, Object> getOutboundChannelProperties();

}
