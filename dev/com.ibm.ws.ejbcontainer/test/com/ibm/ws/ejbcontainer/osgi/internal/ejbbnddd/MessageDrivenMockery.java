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
package com.ibm.ws.ejbcontainer.osgi.internal.ejbbnddd;

import org.jmock.Expectations;
import org.jmock.Mockery;

import com.ibm.ws.javaee.dd.ejbbnd.MessageDriven;

public class MessageDrivenMockery extends EnterpriseBeanMockery<MessageDrivenMockery> {

    MessageDrivenMockery(Mockery mockery, String name) {
        super(mockery, name);
    }

    public MessageDriven mock() {
        final MessageDriven messageDriven = mockEnterpriseBean(MessageDriven.class);
        mockery.checking(new Expectations() {
            { /* empty check */
            }
        });
        return messageDriven;
    }
}
