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
 * Reason        Date        Origin       Description
 * ------------  --------    ----------   ---------------------------------------
 *               15/12/04    van Leersum  Original
 * 291186        08/11/05    schofiel     Refactor lists to improve CPU utilisation
 * ============================================================================
 */
package com.ibm.ws.sib.msgstore.cache.links;

/**
 * Share the priority constants
 */
public interface Priorities {
    static final int HIGHEST_PRIORITY = 9;
    static final int LOWEST_PRIORITY = 0;
    static final int NUMBER_OF_PRIORITIES = 10;
}
