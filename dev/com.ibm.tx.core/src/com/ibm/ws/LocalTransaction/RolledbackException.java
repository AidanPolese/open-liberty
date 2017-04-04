package com.ibm.ws.LocalTransaction;
/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5724-I63, 5724-H88, 5655-N01, 5733-W60                                     */
/* (C) COPYRIGHT International Business Machines Corp. 2002, 2005             */
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
/*  Date      Programmer    Defect    Description                             */
/*  --------  ----------    ------    -----------                             */
/*  01-10-17  irobins   LIDB1181.23.1.1 Initial creation                      */
/*  21/02/03  gareth     LIDB1673.19  Make any unextended code final          */
/*  30/09/03  sykesm     WS18044.02   Add constructors                        */
/*  12/11/03  hursdlg    LIDB2775     Merge zOS and distributed code          */
/*  10-02-05  hursdlg    LIDB3706-5   Serialization                           */
/* ************************************************************************** */

/**
 * 
 * <p> This class is private to WAS.
 * Any use of this class outside the WAS Express/ND codebase 
 * is not supported.
 *
 */

/**
 * Thrown when a LocalTransactionCoordinator is completed with EndModeCommit
 * after the LTC has been marked RollbackOnly. Any enlisted resources are rolled back.
 *
 */
public final class RolledbackException extends Exception
{

    private static final long serialVersionUID = 6433647289443709613L;

    protected Throwable cause = null;

    public RolledbackException()
    {
        super();
    }

    public RolledbackException(String msg)
    {
        super(msg);
    }

    public RolledbackException(String msg, Throwable t)
    {
        super(msg);
        cause = t;
    }

    public Throwable getNestedException()
    {
        return cause;
    }
}
