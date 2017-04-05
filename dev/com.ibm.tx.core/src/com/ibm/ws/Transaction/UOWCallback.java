/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 2002, 2004 */
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
/*  23-03-04  mdobbie     LIDB3133-23  Added SPI classification                      */
/*  14-05-04  awilkins    202175       Remove surplus context change types           */
/*                                                                                   */
/* ********************************************************************************* */

package com.ibm.ws.Transaction;

/**
 * This interface provides a callback method that can be used
 * to inform interested parties of a context change between 
 * Units of Work. The Units of Work in question will either 
 * be Local or Global transaction coordinators.
 *
 * <p> This interface is private to WAS.
 * Any use of this interface outside the WAS Express/ND codebase 
 * is not supported.
 *
 */
public interface UOWCallback
{
    /**
     * Change types for transaction context changes 
     * between Local and Global transactions.
     */
    static public final int PRE_BEGIN  = 0;
    static public final int POST_BEGIN = 1;
    static public final int PRE_END    = 2;
    static public final int POST_END   = 3;

    /**
     * 
     * @param typeOfChange One of the following values:
     * <PRE>
     * PRE_BEGIN
     * POST_BEGIN
     * PRE_END
     * POST_END
     * </PRE>
     * @param UOW The Unit of Work that will be affected by the begin/end i.e.
     * <PRE>
     * PRE_BEGIN  - NULL
     * POST_BEGIN - The UOW that was just begun
     * PRE_END    - The UOW to be ended
     * POST_END   - NULL
     * </PRE>
     * 
     * @exception IllegalStateException
     */
    public void contextChange(int typeOfChange, UOWCoordinator UOW) throws IllegalStateException;
}
