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
package com.ibm.ws.ejbcontainer.osgi.internal.ejb;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

@Stateless
@Local(LocalIntf1.class)
@Remote(LocalIntf1.class)
public class TestLocalAndRemoteError {}
