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
package com.ibm.ws.cdi.extension;

/**
 * This is a *marker* interface for Weld Runtime extension. All runtime extensions need to register a service
 * under this interface. This bundle will find all of the services and then get hold of the bundle classloader and
 * pass onto Weld.
 */
public interface WebSphereCDIExtension {}
