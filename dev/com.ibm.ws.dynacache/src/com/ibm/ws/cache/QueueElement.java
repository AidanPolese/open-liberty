// 1.2, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache;

  /**
   *  A <code>QueueElement</code> provides a base class for elements that
   *  are stored in a <code>Queue</code>. <p>
   */

  public class QueueElement {

      /**
       *  Pointer to previous and next elements in the queue. 
       */

      protected QueueElement previous;
      protected QueueElement next;

      /**
       *  The <code>Queue</code> this element is currently an element of.
       *
       *  Note, a <code>QueueElement</code> can belong to at most one queue
       *  at a time.
       */

      protected Queue queue;

      /**
       *  Remove this <code>QueueElement</code> from its associated queue.
       *
       *  It is an error to attempt to remove a queue element that is not
       *  currently associated with any queue. 
       */

      public void removeFromQueue() {

	  if (queue == null) {
	      throw new RuntimeException("Queue element is not " +
					 "member of a queue");
	  }

	  queue.remove(this);

      }				// removeFromQueue

  }				// QueueElement

