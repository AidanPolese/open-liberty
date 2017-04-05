package com.ibm.ws.Transaction.JTA;
/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002, 2007 */
/* The source code for this program is not published or otherwise divested    */
/* of its trade secrets, irrespective of what has been deposited with the     */
/* U.S. Copyright Office.                                                     */
/*                                                                            */
/* %Z% %I% %W% %G% %U% [%H% %T%]                                              */
/*                                                                            */
/*  DESCRIPTION:                                                              */
/*                                                                            */
/*                                                                            */
/*  Change History:                                                           */
/*                                                                            */
/*  Date      Programmer    Defect    Description                             */
/*  --------  ----------    ------    -----------                             */
/*  05/09/02  gareth        ------    Move to JTA implementation              */
/*  11/10/02  awilkins      1452      XAException handling                    */
/*  25/11/02  awilkins      1513      Repackage ejs.jts -> ws.Transaction     */
/*  07/03/03  hursdlg       159733    Handle logging at resource level        */
/*  15/07/03   mallam       171151    Rollback using TMSUCCESS (TMFAIL on t/o)*/
/*  28/08/03  johawkes      173214    Replace RegisteredResource vectors      */
/*  28/08/03  johawkes      174516    Distribute all ends before prepares     */
/*  10/02/04  hursdlg       190239    Update JTAResource states               */
/*  19/04/04  johawkes      193919.1  New methods for adminconsole            */
/*  04/08/06  maples        373006    WESB performance isSameRM optimization  */
/*  20/06/07  hursdlg       LI3968-1.2 Add commit priority getter             */
/*  17/07/08  johawkes      536926    Remove JET dependencies on org.omg      */
/* ************************************************************************** */

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import com.ibm.ws.recoverylog.spi.RecoverableUnitSection;

public interface JTAResource extends StatefulResource
{
    /** The underlying XAResource is not enlisted in a transaction. */
    public static final int NOT_ASSOCIATED = 0;

    /** The underlying XAResource has been suspended. */
    public static final int SUSPENDED = 1;

    /** The underlying XAResource is currently involved in an active transaction. */
    public static final int ACTIVE = 2;

    /** The underlying XAResource is in a failed state */
    public static final int FAILED = 3;

    /** The underlying XAResource is in a rollback only state */
    public static final int ROLLBACK_ONLY = 4;

    /** The underlying XAResource is in an idle state awaiting completion */
    public static final int IDLE = 5;
    
    /** The underlying XAResource is not enlisted in a transaction, should
     *  use the TMJOIN when the resource gets issued start called. */
    public static final int NOT_ASSOCIATED_AND_TMJOIN = 6;

    public static final int DEFAULT_COMMIT_PRIORITY = 0;

    public static final int LAST_IN_COMMIT_PRIORITY = Integer.MIN_VALUE;

    public void start() throws XAException;

    public void end(int flag) throws XAException;
    
    public int  prepare() throws XAException;

    public void commit() throws XAException;

    public void commit_one_phase() throws XAException;

    public void rollback() throws XAException;
    
    public void forget() throws XAException;

    public Xid getXID();
    public int getState();
    public XAResource XAResource();

    public void destroy();

    public void setState(int state);

    public void log(RecoverableUnitSection rus) throws javax.transaction.SystemException;
    
    public String describe();

    public int getPriority();

    public enum JTAResourceVote
    {
        commit,
        rollback,
        readonly,
        heuristic,
        none;
    }
}