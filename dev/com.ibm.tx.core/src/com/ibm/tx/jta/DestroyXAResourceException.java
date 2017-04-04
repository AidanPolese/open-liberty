package com.ibm.tx.jta;
/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5639-D57,5630-A36,5630-A37,5724-D18                                        */
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
/*  Date      Programmer    Defect   Description                              */
/*  --------  ----------    ------   -----------                              */
/*  05/09/02   gareth       ------   Move to JTA implementation               */
/*  25/11/02   awilkins       1513   Repackage ejs.jts -> ws.Transaction      */
/*  21/02/03   gareth    LIDB1673.19 Make any unextended code final           */
/*  10-02-05   hursdlg   LIDB3706-5  Serialization                            */
/*  05/06/07   johawkes     443467   Moved                                    */
/*  09/09/08   johawkes     546427   Chained                                  */
/* ************************************************************************** */

public class DestroyXAResourceException extends Exception 
{
    protected static final long serialVersionUID = -5411376092461769946L;

    public Exception detail;

    public DestroyXAResourceException(Exception e)
    {
        super("Error destroying XAResource: " + e.toString(), e);
        detail = e;
    }
}