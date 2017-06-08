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
import javax.ejb.Stateless;

@Stateless
@Local
@LocalBean
public class TestEmptyLocalAndLocalBean implements LocalIntf1 {}
