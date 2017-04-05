/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javamail.internal.injection;

import java.util.Map;

import com.ibm.ws.javamail.internal.MailSessionService;
import com.ibm.ws.resource.ResourceRefInfo;
import com.ibm.wsspi.resource.ResourceInfo;

/**
 */
public class MailSessionResourceFactory extends MailSessionService implements com.ibm.ws.resource.ResourceFactory {

    /**
     * @see com.ibm.ws.resource.ResourceFactory#createResource(com.ibm.ws.resource.ResourceRefInfo)
     */
    @Override
    public Object createResource(ResourceRefInfo ref) throws Exception {
        return createResource((ResourceInfo) ref);
    }

    /**
     * 
     * @throws Exception if an error occurs.
     */
    @Override
    public void destroy() throws Exception {}

    @Override
    public void modify(Map<String, Object> props) throws Exception {
        throw new UnsupportedOperationException();
    }
}
