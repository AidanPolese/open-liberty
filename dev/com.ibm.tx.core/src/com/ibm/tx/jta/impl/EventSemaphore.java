package com.ibm.tx.jta.impl;
/* ************************************************************************** */
/* COMPONENT_NAME: WAS.transactions                                           */
/*                                                                            */
/*  ORIGINS: 27                                                               */
/*                                                                            */
/* IBM Confidential OCO Source Material                                       */
/* 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 2002, 2009 */
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
/*  12/09/02   gareth       ------   Move to JTA implementation               */
/*  21/02/03   gareth   LIDB1673.19  Make any unextended code final           */
/*  07-04-16   dmatthew     412459   call Object.wait from within a loop      */
/*  17/05/07   johawkes     438575   Further componentization                 */
/*  06/06/07   johawkes     443467   Moved                                    */
/*  02/07/07   johawkes     446894.1 Make clear() public                      */
/*  02/06/09   mallam       596067   package move                             */
/* ************************************************************************** */

/**
 * The EventSemaphore interface provides operations that wait for and post an
 * event semaphore.
 * <p>
 * This is specifically to handle the situation where the event may have been
 * posted before the wait method is called.  This behaviour is not supported by
 * the existing wait and notify methods.
 */
public final class EventSemaphore
{
    boolean _posted;

    /**
     * Default Constructor
     */
    public EventSemaphore() {}


    /**
     * Creates the event semaphore in the given posted state.
     * 
     * @param posted  Indicates whether the semaphore should be posted.
     */
    EventSemaphore( boolean posted )
    {
        _posted = posted;
    }


    /**
     * Waits for the event to be posted.
     *  <p>
     *  If the event has already been posted, then the operation returns immediately.
     * 
     * @exception InterruptedException
     *                   The wait was interrupted.
     */
    synchronized public void waitEvent() throws InterruptedException
    {
        while ( !_posted )
        {
            wait();            
        }
    }


    /**
     * Posts the event semaphore.
     *  <p>
     *  All waiters are notified.
     */
    public synchronized void post()
    {
        if ( !_posted )
            notifyAll();
        _posted = true;
    }


    /**
     * Clears a posted event semaphore.
     */
    public synchronized void clear()
    {
        _posted = false;
    }
}