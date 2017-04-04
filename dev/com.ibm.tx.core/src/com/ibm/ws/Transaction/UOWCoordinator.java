package com.ibm.ws.Transaction;

import javax.transaction.xa.Xid;

import com.ibm.wsspi.tx.UOWEventEmitter;

/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5630-A36. (C) COPYRIGHT International Business Machines Corp. 2002, 2004   */
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
/*  Date      Programmer  Defect      Description                             */
/*  --------  ----------  ------      -----------                             */
/*  05/09/02   gareth     ------      Move to JTA implementation              */
/*  25/11/02   awilkins   1513        Repackage ejs.jts -> ws.Transaction     */
/*  05/12/02   awilkins   1535        getTxType moved to UOWCoordinator       */
/*  03-07-31   irobins    173218      Add getRollbackOnly to UOWCoordinator   */
/*  04-03-12   awilkins   186747.3    Add ActivitySession tx type             */
/*  04-03-23   mdobbie    LIDB3133-23 Added SPI classification                */
/*  04-04-22   awilkins   198904.1    Add getXid                              */
/* ************************************************************************** */

/**
 * 
 * <p> This interface is private to WAS.
 * Any use of this interface outside the WAS Express/ND codebase
 * is not supported.
 * 
 */
public interface UOWCoordinator extends UOWEventEmitter
{
    public static final int TXTYPE_LOCAL = 0; // A local transaction
    public static final int TXTYPE_INTEROP_GLOBAL = 1; // OTS-compliant global transaction
    public static final int TXTYPE_NONINTEROP_GLOBAL = 2; // Non-OTS-compliant global transaction
    public static final int TXTYPE_ACTIVITYSESSION = 3; // An ActivitySession

    boolean isGlobal();

    byte[] getTID();

    public Xid getXid();

    /**
     * Indicates the <i>type</i> of transaction on the thread.
     * This method is called by the EJB TranStrategy collaborators
     * to determine how to proceed with method dispatch, based on the application-configured
     * transaction attribute.
     * 
     * @return the <i>type</i> of transaction on the thread where the types are
     *         <dl>
     *         <dd><b>LOCAL</b>
     *         <dt>A local transaction.
     *         <dd><b>INTEROP_GLOBAL</b>
     *         <dt>an OTS compliant global transaction
     *         <dd><b>NONINTEROP_GLOBAL</b>
     *         <dt>a global transaction received from a foreign EJS that does not support
     *         transactional interoperability
     *         </dl>
     */
    public int getTxType();

    public boolean getRollbackOnly();

}
