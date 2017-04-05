package com.ibm.tx.jta;
/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/*  ORIGINS: 27                                                                      */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-I63, 5724-H88, 5655-N01, 5733-W60                                            */
/* (C) COPYRIGHT International Business Machines Corp. 2002, 2005                    */
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
/*  Date      Programmer  Defect       Description                                   */
/*  --------  ----------  ------       -----------                                   */
/*  05/09/02  gareth      ------       Move to JTA implementation                    */
/*  25/11/02  awilkins    1513         Repackage ejs.jts -> ws.Transaction           */
/*  21/02/03  gareth      LIDB1673.19  Make any unextended code final                */
/*  03/12/04  awilkins    245505       Add default constructor                       */
/*  10-02-05  hursdlg     LIDB3706-5   Serialization                                 */
/*  05/06/07  johawkes    443467       Moved                                         */
/* ********************************************************************************* */

public class XAResourceNotAvailableException extends Exception 
{
    protected static final long serialVersionUID = -3663835677857622787L;

    public Throwable detail;

    public XAResourceNotAvailableException(Throwable t)
    {
        super(t);        
        detail = t;
    }
    
    public XAResourceNotAvailableException()
    {
        super();
    }
}
