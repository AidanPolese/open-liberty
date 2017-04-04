package com.ibm.ws.Transaction.JTA;
/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/*  ORIGINS: 27                                                                      */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1999, 2004 */
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
/*  Date      Programmer        Defect    Description                                */
/*  --------  ----------        ------    -----------                                */
/*  02-02-06  awilkins          115601-2  add setXAResource                          */
/*  02-02-07  hursdlg           111021    reduce log record size                     */
/*  02-02-25  hursdlg           111021.1  log resource recovery data                 */
/*  05/09/02   gareth           ------    Move to JTA implementation                 */
/*  03-08-28  johawkes          173214    Replace RegisteredResource vectors         */
/*  03-08-28  johawkes          174516    Enable Oracle XA optimisation              */
/*  03-09-18  hursdlg           177194    Migrate to 8 byte recovery ids             */
/*  10/02/04  hursdlg           190239    Update JTAResource states                  */
/*  25/03/04  hursdlg           196258    Make recovery get new connection           */
/* ********************************************************************************* */

import javax.transaction.xa.Xid;

public interface JTAXAResource extends StatefulResource
{
    /**
     * return XID associated with this JTAXAResource object.
     */
    public Xid getXID();
    
    /**
     * return recoveryId associated with this JTAXAResource object.
     */
    public long getRecoveryId();
    
}
