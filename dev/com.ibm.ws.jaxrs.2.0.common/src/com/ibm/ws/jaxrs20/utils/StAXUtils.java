/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.jaxrs20.utils;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;

/**
 *
 */
public class StAXUtils {

    private static final TraceComponent tc = Tr.register(StAXUtils.class);

    public static final String IBM_XLXP2_XML_OUTPUT_FACTORY = "com.ibm.xml.xlxp2.api.stax.XMLOutputFactoryImpl";

    public static final String IBM_XLXP2_XML_INPUT_FACTORY = "com.ibm.xml.xlxp2.api.wssec.WSSXMLInputFactory";

    public static final String IBM_XLXP2_XML_EVENT_FACTORY = "com.ibm.xml.xlxp2.api.stax.XMLEventFactoryImpl";

    @FFDCIgnore(ClassNotFoundException.class)
    public static ClassLoader getStAXProviderClassLoader() {
        try {
            Class.forName(IBM_XLXP2_XML_OUTPUT_FACTORY);
            Class.forName(IBM_XLXP2_XML_INPUT_FACTORY);
            Class<?> eventFactoryClass = Class.forName(IBM_XLXP2_XML_EVENT_FACTORY);
            return eventFactoryClass.getClassLoader();
        } catch (ClassNotFoundException e) {
            if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
                Tr.debug(tc, "Unable to load IBM STAX XLXP2 Provider " + e.getMessage() + ", StAX from JRE is used");
            }
            return ClassLoader.getSystemClassLoader();
        }
    }
}
