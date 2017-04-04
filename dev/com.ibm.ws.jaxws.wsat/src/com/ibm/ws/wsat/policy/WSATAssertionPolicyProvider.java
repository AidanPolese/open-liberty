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
package com.ibm.ws.wsat.policy;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.apache.cxf.ws.policy.AbstractPolicyInterceptorProvider;

import com.ibm.ws.jaxws.wsat.Constants;

/**
 *
 */
public class WSATAssertionPolicyProvider extends AbstractPolicyInterceptorProvider {
    /**  */

    private static final long serialVersionUID = 1888067784675731010L;

    public static final Collection<QName> ASSERTION_TYPES;

    static {
        ASSERTION_TYPES = new ArrayList<QName>();
        ASSERTION_TYPES.add(Constants.AT_ASSERTION_QNAME);
    }

    public WSATAssertionPolicyProvider() {
        super(ASSERTION_TYPES);
        //      getInInterceptors().add(new WSATPolicyAwareInterceptor(Phase.RECEIVE, false));
    }
}
