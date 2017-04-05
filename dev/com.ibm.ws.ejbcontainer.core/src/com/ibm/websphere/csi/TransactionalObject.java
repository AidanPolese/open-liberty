/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2000
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.csi;

/**
 * An alternative way to mark an object as transactional,
 * so that JTS will establish a transactional context when
 * the object is invoked through an ORB.
 * 
 * No methods or other interfaces are required.
 */

public interface TransactionalObject extends java.rmi.Remote {}
