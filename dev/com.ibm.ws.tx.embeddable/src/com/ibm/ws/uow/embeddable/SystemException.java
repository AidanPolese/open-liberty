/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-I63, 5724-H88, 5655-N01, 5733-W60                                            */
/* (C) COPYRIGHT International Business Machines Corp. 2002, 2011                    */
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
/*  YY-MM-DD  Programmer  Defect  Description                                        */
/*  --------  ----------  ------  -----------                                        */
/*  04-05-07  awilkins    200172  Creation                                           */
/*  10-02-05  hursdlg    LIDB3706-5  Serialization                                   */
/*  11-11-24  johawkes    723423  Repackaging                                        */
/* ********************************************************************************* */

package com.ibm.ws.uow.embeddable;

public class SystemException extends Exception
{

    private static final long serialVersionUID = -4045971852610441112L;

    public SystemException(Throwable cause)
    {
        super(cause);
    }
}
