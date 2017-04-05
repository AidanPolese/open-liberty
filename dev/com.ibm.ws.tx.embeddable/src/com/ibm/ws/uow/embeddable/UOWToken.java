/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-i63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 2004,2011  */
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
/*  YY-MM-DD  Programmer  Defect         Description                                 */
/*  --------  ----------  ------         -----------                                 */
/*  04-04-21  awilkins    LIDB3133-23.4  Creation                                    */
/*  04-05-12  awilkins    200172         Implementation work - repackage             */  
/*  11-11-24  johawkes    723423         Repackaging                                 */  
/*                                                                                   */
/* ********************************************************************************* */

package com.ibm.ws.uow.embeddable;

/**
 * A <code>UOWToken</code> is used to manipulate unit of work contexts via 
 * the <code>suspend()</code> and <code>resume()</code> methods of the 
 * <code>UOWManager</code> interface.
 * 
 * @see UOWManager#suspend()
 * @see UOWManager#resume(UOWToken)
 *  
 */
public interface UOWToken
{

}
