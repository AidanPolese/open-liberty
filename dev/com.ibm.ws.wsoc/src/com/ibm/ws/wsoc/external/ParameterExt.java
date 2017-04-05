/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.wsoc.external;

import javax.websocket.Extension.Parameter;

/**
 *
 */
public class ParameterExt implements Parameter {

    private String name = "";
    private String value = "";

    public ParameterExt(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.websocket.Extension.Parameter#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.websocket.Extension.Parameter#getValue()
     */
    @Override
    public String getValue() {
        return value;
    }

}
