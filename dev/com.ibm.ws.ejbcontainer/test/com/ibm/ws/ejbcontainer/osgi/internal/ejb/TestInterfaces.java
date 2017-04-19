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

import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.LocalHome;
import javax.ejb.Remote;
import javax.ejb.RemoteHome;
import javax.ejb.Stateless;

@Stateless
@RemoteHome(RemoteHomeIntf.class)
@LocalHome(LocalHomeIntf.class)
@Remote({ RemoteIntf1.class, RemoteIntf2.class })
@Local({ LocalIntf1.class, LocalIntf2.class })
@LocalBean
public class TestInterfaces {}
