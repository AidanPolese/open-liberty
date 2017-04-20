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

import javax.annotation.ManagedBean;
import javax.ejb.Stateless;

@Stateless
@ManagedBean(TestStatelessMBNamed.NAME)
public class TestStatelessMBNamed {
    public static final String NAME = "StatelessMBNamed";
}
