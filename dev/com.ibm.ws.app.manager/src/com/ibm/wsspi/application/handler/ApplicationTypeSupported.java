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
package com.ibm.wsspi.application.handler;

/**
 * A marker service which provides an indication that we support a type of application when the
 * application handler for that type may not have become available yet. Each instance is expected
 * to have a service property with the type which it declares support for, i.e. "type:String=war"
 */
public interface ApplicationTypeSupported {}