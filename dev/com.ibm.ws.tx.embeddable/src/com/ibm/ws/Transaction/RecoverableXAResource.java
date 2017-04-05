package com.ibm.ws.Transaction;
/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5630-A36. (C) COPYRIGHT International Business Machines Corp. 2002, 2003   */
/* The source code for this program is not published or otherwise divested    */
/* of its trade secrets, irrespective of what has been deposited with the     */
/* U.S. Copyright Office.                                                     */
/*                                                                            */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                              */
/*                                                                            */
/*  DESCRIPTION:                                                              */
/*                                                                            */
/*  Change History:                                                           */
/*                                                                            */
/*  Date      Programmer    Defect   Description                              */
/*  --------  ----------    ------   -----------                              */
/*  07/01/04  schommer      LIDB2110 For new XARecovery coordination code     */
/*  23-03-04  mdobbie     LIDB3133-23 Added SPI classification                */
/* ************************************************************************** */

import javax.transaction.xa.XAResource;

/**
 *
 * This class is currently used to help JetStream implement XARecovery in an environment 
 * where the recovery information dynacially changes.  During transaction enlistment 
 * the JCA runtime or EJB container should detect that the XAResource implements 
 * this interface and use the recovery token obtained from getXARecoveryToken 
 * on the enlist call on WebSphereTransactionManager.  This method should be 
 * called prior to every enlistment.  The value should not be cached by the 
 * caller as the resource adapter may be connecting to different resource managers.  
 * The resource adapter will obtain this recovery id via the registerResourceInfo 
 * method on WebSphereTransactionManager and will provide its own implementation 
 * of the XAResourceInfo and XAResourceFactory interfaces.  
 * Note that the same mechanism will be used for both recovery of both inbound and 
 * outbound work i.e. the registerActivationSpec method will not be used.
 * 
 * <p> This interface is private to WAS.
 * Any use of this interface outside the WAS Express/ND codebase 
 * is not supported.
 *
 */
public interface RecoverableXAResource extends XAResource
{
    /**
     * @return int RecoveryToken (or RecoveryId) which is orginally obtained from the
     * transaction service when registering the recovery information.  This information
     * is expected to change over time so it should be retrieved each time before 
     * an enlist with the TM (i.e. don't cache it).
     */
    int getXARecoveryToken();
}