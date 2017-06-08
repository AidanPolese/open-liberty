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

import javax.annotation.ManagedBean;
import javax.interceptor.Interceptors;

@ManagedBean
@Interceptors({ Interceptor1.class, Interceptor2.class })
public class TestManagedBeanInterceptors {}
