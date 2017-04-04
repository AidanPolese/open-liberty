/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014, 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.ejbcontainer.v32;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.ejbcontainer.osgi.EJBRuntimeVersion;

@Component(service = EJBRuntimeVersion.class, property = { "version=3.2", "service.ranking:Integer=32" })
public class EJBRuntimeVersionV32 extends EJBRuntimeVersion {}
