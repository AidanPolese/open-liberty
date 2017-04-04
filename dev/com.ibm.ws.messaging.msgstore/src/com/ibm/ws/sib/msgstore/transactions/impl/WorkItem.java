package com.ibm.ws.sib.msgstore.transactions.impl;
/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date     Origin       Description
 * ------------- -------- -----------  ----------------------------------------
 * 186657.1      24/05/04  gareth      Per-work-item error checking.
 * SIB0002.ms.1  28/07/05  schofiel    Changes for remote MQ subordinate resources (moved from SIB.msgstore)
 * ============================================================================
 */

/**
 * This interface is a marker that allows us to define a type for passing 
 * on the WorkList.addWork() method without externalising the task package.
 */
public interface WorkItem {}
