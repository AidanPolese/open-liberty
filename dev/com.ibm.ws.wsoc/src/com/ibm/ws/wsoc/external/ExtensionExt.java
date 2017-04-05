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

import java.util.Collections;
import java.util.List;

import javax.websocket.Extension;

/**
 *
 */
public class ExtensionExt implements Extension {

    private String name = "";

    private List<Parameter> parameters = Collections.emptyList();

    public ExtensionExt(String name, List<Parameter> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.websocket.Extension#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.websocket.Extension#getParameters()
     */
    @Override
    public List<Parameter> getParameters() {
        return parameters;
    }

}
