package com.ibm.ws.Transaction;
/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 2003, 2004 */
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
/*  Date      Programmer  Defect         Description                                 */
/*  --------  ----------  ------         -----------                                 */
/*  05/09/02  gareth      ------         Move to JTA implementation                  */
/*  25/11/02  awilkins    1513           Repackage ejs.jts -> ws.Transaction         */
/*  23-03-04  mdobbie     LIDB3133-23    Added SPI classification                    */
/*  21-04-04  awilkins    LIDB3133-23.4  Added suspend/resume class A SPIs           */
/*  12-05-04  awilkins    200172         Move suspend/resume to UOWManager           */
/*  18-10-11  johawkes    719671         UOWEventListener support                    */
/* ********************************************************************************* */

import com.ibm.wsspi.tx.UOWEventListener;

/**
 * @ibm-was-base
 */
public interface UOWCurrent
{
   public static final int  UOW_NONE   = 0;
   public static final int  UOW_LOCAL  = 1;
   public static final int  UOW_GLOBAL = 2;

   int             getUOWType ();
   UOWCoordinator  getUOWCoord ();
   void            registerLTCCallback(UOWCallback callback); //Defect 130321
   void			   setUOWEventListener(UOWEventListener el);
   void			   unsetUOWEventListener(UOWEventListener el);
}
