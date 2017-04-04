/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2017
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.ws.javaee.ddmodel.wsbnd.impl;

import java.util.Map;

import com.ibm.ws.javaee.dd.common.Description;

/**
 *
 */
public class DescriptionImpl implements Description {

    private final String value;
    private final String lang;

    /**
     * @param descriptionConfig
     */
    public DescriptionImpl(Map<String, Object> config) {
        lang = (String) config.get("lang");
        value = (String) config.get("value");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.dd.common.Description#getLang()
     */
    @Override
    public String getLang() {
        return lang;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.javaee.dd.common.Description#getValue()
     */
    @Override
    public String getValue() {
        return value;
    }

}
