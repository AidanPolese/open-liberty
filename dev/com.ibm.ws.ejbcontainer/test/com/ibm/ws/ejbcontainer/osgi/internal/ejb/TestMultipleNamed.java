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
package com.ibm.ws.ejbcontainer.osgi.internal.ejb;

import javax.ejb.MessageDriven;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;

@Stateless(name = TestMultipleNamed.STATELESS_NAME)
@Stateful(name = TestMultipleNamed.STATEFUL_NAME)
@Singleton(name = TestMultipleNamed.SINGLETON_NAME)
@MessageDriven(name = TestMultipleNamed.MESSAGE_DRIVEN_NAME)
public class TestMultipleNamed {
    public static final String STATELESS_NAME = "MultipleNamedStateless";
    public static final String STATEFUL_NAME = "MultipleNamedStateful";
    public static final String SINGLETON_NAME = "MultipleNamedSingleton";
    public static final String MESSAGE_DRIVEN_NAME = "MultipleNamedMessageDriven";
}
