/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi.internal.ejbextdd;


import org.jmock.Expectations;
import org.jmock.Mockery;

import com.ibm.ws.javaee.dd.ejbext.Session;

public class SessionMockery extends EnterpriseBeanMockery<SessionMockery> {

    SessionMockery(Mockery mockery, String name) {
        super(mockery, name);
    }

    public Session mock() {
        final Session session = mockEnterpriseBean(Session.class);
        mockery.checking(new Expectations() {
            {
            }
        });
        return session;
    }
}
