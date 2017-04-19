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

import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateless;

@Stateless
@Remote
@LocalBean
public class TestEmptyRemoteAndLocalBean implements RemoteIntf1 {}
