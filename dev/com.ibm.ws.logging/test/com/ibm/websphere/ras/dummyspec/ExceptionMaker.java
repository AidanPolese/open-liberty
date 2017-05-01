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
package com.ibm.websphere.ras.dummyspec;

/**
 * ExceptionMakers can be chained to produce interesting stack traces.
 */
public interface ExceptionMaker {

    Exception constructException();

}