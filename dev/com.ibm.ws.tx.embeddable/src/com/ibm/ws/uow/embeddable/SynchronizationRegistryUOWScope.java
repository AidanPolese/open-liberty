/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2006, 2011 */
/* The source code for this program is not published or otherwise divested           */
/* of its trade secrets, irrespective of what has been deposited with the            */
/* U.S. Copyright Office.                                                            */
/*                                                                                   */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                                     */
/*                                                                                   */
/*  DESCRIPTION:                                                                     */
/*                                                                                   */
/*  Change History:                                                                  */
/*                                                                                   */
/*  YY-MM-DD  Developer  Defect    Description                                       */
/*  --------  ---------  ------    -----------                                       */
/*  06-08-09  awilkins   LIDB4244  Creation                                          */
/*  07-04-16  awilkins   415521    Added getUOWName                                  */
/*  11-11-24  johawkes   723423    Repackaging                                       */
/*                                                                                   */
/* ********************************************************************************* */

package com.ibm.ws.uow.embeddable;

import javax.transaction.Synchronization;

import com.ibm.ws.uow.UOWScope;

/**
 * This interface declares the methods required to support the implementation
 * of TransactionSynchronizationRegistry and UOWSynchronizationRegistry.
 * 
 * @see com.ibm.websphere.uow.UOWSynchronizationRegistry
 * @see javax.transaction.TransactionSynchronizationRegistry
 */
public interface SynchronizationRegistryUOWScope extends UOWScope
{
	// This set of three modifiers are used to ensure that local id's returned from
	// getLocalId are unique across all three UOW types. We do not change the most
	// significant bit to avoid causing the numbers to become negative. In
	// binary the three modifiers are 0110, 0100, and 0010 respectively. This limits
	// us to 61 bits until there is a risk of clashes in local id between the three
	// UOW types. This equates to 2305843009213693951 UOWs of each type; sufficient
	// to create one uow per millisecond for in excess of 73 million years until a
	// clash may occur.
//	public static final long GLOBAL_TRANSACTION_LOCAL_ID_MODIFIER = 0x6000000000000000L;
	public static final long LOCAL_TRANSACTION_LOCAL_ID_MODIFIER = 0x4000000000000000L;
	public static final long ACTIVITYSESSION_LOCAL_ID_MODIFIER = 0x2000000000000000L;
	
	public void putResource(Object key, Object resource);
    public Object getResource(Object key);
    
    public long getLocalId();
    
    public boolean getRollbackOnly();
    public void setRollbackOnly();
    
    public int getUOWStatus();
    public int getUOWType();
    
    public void registerInterposedSynchronization(Synchronization sync);
    public String getUOWName();
}
