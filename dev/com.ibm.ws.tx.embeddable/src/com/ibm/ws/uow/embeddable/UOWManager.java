/* ********************************************************************************* */
/* COMPONENT_NAME: WAS.transactions                                                  */
/*                                                                                   */
/* ORIGINS: 27                                                                       */
/*                                                                                   */
/* IBM Confidential OCO Source Material                                              */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  COPYRIGHT International Business Machines Corp. 2004,2011  */
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
/*  YY-MM-DD  Programmer  Defect    Description                                      */
/*  --------  ----------  ------    -----------                                      */
/*  04-05-07  awilkins    200172    Creation                                         */
/*  04-05-12  awilkins    202175    UOWScope and UOWScopeCallback                    */
/*  04-05-25  awilkins    202175.1  Provide accessor to current UOWScope             */
/*  04-06-24  awilkins    210577    Correct javadoc for getUOWScope()                */
/*  06-10-13  johawkes    LIDB4548-1.1 Extend SPI                                    */
/*  08-04-02  mallam      498639    provide suspendAll/resumeAll methods             */
/*  11-11-24  johawkes    723423    Repackaging                                      */
/* ********************************************************************************* */

package com.ibm.ws.uow.embeddable;

import com.ibm.ws.Transaction.UOWCallback;
import com.ibm.ws.uow.UOWScope;
import com.ibm.ws.uow.UOWScopeCallback;

public interface UOWManager extends com.ibm.wsspi.uow.UOWManager
{    
    /** 
     * Suspends all units of work that are currently active on the calling
     * thread. In the event of an exception being thrown no change will have
     * been made to the state of the calling thread.
     * 
     * @exception SystemException Thrown if an unexpected internal error occurs.
     * 
     * @return A <code>UOWToken</code> that represents the suspended unit(s) of
     * work or null if no units of work were active.
     */
    public UOWToken suspend() throws SystemException;
   
    /**
     * Resumes the unit(s) of work represented by the given <code>UOWToken</code>.
     * In the event of an exception being thrown no change will have been made to
     * the state of the calling thread.
     * 
     * @param uowToken The token that represents the unit(s) of work to be resumed.
     * <code>null</code> is a valid input and will result in the thread's state
     * remaining unchanged.
     * 
     * @exception IllegalThreadStateException Thrown if the calling thread is
     * already associated with a unit of work of the same type as one that is
     * encapsulated in the given <code>UOWToken</code>.
     * 
     * @exception IllegalArgumentException Thrown if the given
     * <code>UOWToken</code> represents on or more units of work that are invalid
     * 
     * @exception SystemException Thrown if an unexpected internal error occurs
     */
    public void resume(UOWToken uowToken) throws IllegalThreadStateException, IllegalArgumentException, SystemException;

    /** 
     * Suspends all units of work that are currently active on the calling
     * thread. In the event of an exception being thrown no change will have
     * been made to the state of the calling thread.
     * In addition to tx, LTC and ActivitySessions, this method also suspends all
     * ActivityService context.  HLSLite context will not be passed on subsequent iiop 
     * requests after this method is called until a corresponding resumeAll call is made
     * 
     * @exception SystemException Thrown if an unexpected internal error occurs.
     * 
     * @return A <code>UOWToken</code> that represents the suspended unit(s) of
     * work or null if no units of work were active.
     */
    public UOWToken suspendAll() throws SystemException;

    /**
     * Resumes the unit(s) of work represented by the given <code>UOWToken</code>.
     * In the event of an exception being thrown no change will have been made to
     * the state of the calling thread.
     * 
     * @param uowToken The token that represents the unit(s) of work to be resumed.
     * This should be obtained via a call to suspendAll.
     * In addition to tx, LTC and ActivitySessions, this method also resumes 
     * ActivityService context.  HLSLite context will resume being passed on iiop 
     * requests after this method call completes.
     * <code>null</code> is a valid input and will result in the thread's state
     * remaining unchanged.
     * 
     * @exception Exception Thrown if an unexpected internal error occurs
     */
    public void resumeAll(UOWToken uowToken) throws Exception;
   
    
    /** 
     * Registers the given UOWScopeCallback for POST_BEGIN of all UOW
     * types, both user and container initiated, and in client and server
     * environments.
     * 
     * @param callback The callback to be registered for POST_BEGIN notification
     */
    public void registerCallback(UOWScopeCallback callback);
    
    /** 
     * Registers the given UOWCallback for POST_BEGIN and POST_END for
     * all UOWs started with com.ibm.wsspi.uow.UOWManager.runUnderUOW()
     * 
     * @param callback The callback to be registered for POST_BEGIN and POST_END notification
     */
    public void registerRunUnderUOWCallback(UOWCallback callback);
    
    /**
     * Returns the UOWScope on the calling thread that is responsible for coordinating
     * enlisted resources. 
     * 
     * @return The currently active UOWScope responsible for coordinating enlisted resources
     * 
     * @exception SystemException Thrown if an unexpected internal error occurs
     */
    public UOWScope getUOWScope() throws SystemException;
            
}
