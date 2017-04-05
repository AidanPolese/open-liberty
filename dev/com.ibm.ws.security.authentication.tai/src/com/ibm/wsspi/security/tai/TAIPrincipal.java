/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.security.tai;

/**
 * <p>
 * This marker interface only needs to be implemented by a TAI
 * provider in cases where they want to put in multiple Principal
 * objects in the Subject that is returned by the TAI.getSubject()
 * API. There can only be one principal which implements this
 * interface, otherwise, it's non-deterministic regarding which
 * Principal should be used for the TAI/WAS username.
 * </p>
 * <p>
 * In cases where there is only one principal in the Subject, it is
 * assumed that the principal implements java.security.Principal and
 * the getName() API is used to retrieve the authenticated username.
 * </p>
 * 
 * @author IBM Corporation
 * @version 1.0
 * @since 1.0
 * @ibm-spi
 */
public interface TAIPrincipal extends java.security.Principal {}
