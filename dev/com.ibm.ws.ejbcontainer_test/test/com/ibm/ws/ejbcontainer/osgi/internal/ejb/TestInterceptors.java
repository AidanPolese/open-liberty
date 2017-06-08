/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.osgi.internal.ejb;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

@Stateless
@Interceptors({ Interceptor1.class, Interceptor2.class })
public class TestInterceptors implements LocalIntf1 {}
