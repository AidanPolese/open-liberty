/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.app.manager.ejb.internal;

import org.osgi.service.component.annotations.Component;

import com.ibm.wsspi.application.handler.ApplicationTypeSupported;

@Component(service = ApplicationTypeSupported.class,
           property = { "service.vendor=IBM", "type:String=ejb" })
public class EJBApplicationTypeSupported implements ApplicationTypeSupported {}
