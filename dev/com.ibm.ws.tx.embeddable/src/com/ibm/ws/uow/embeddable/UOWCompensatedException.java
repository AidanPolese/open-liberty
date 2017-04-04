/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  COPYRIGHT International Business Machines Corp. 2008,2011  */
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
/*  08-07-147  mallam     523634  Creation                                           */
/*  11-11-24   johawkes   723423  Repackaging                                        */
/* ********************************************************************************* */

package com.ibm.ws.uow.embeddable;

public class UOWCompensatedException extends RuntimeException
{
    private static final long serialVersionUID = -1868184181459590196L;;

    public UOWCompensatedException(String message)
    {
        super(message);
    }

    public UOWCompensatedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public UOWCompensatedException(Throwable cause)
    {
        super(cause);
    }


}
