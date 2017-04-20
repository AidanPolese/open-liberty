/**
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date      Origin   Description
 * --------------- ------    -------- ---------------------------------------
 * 174369.1        11-Sep-03 dcurrie  Original
 * 173765.6        25-Sep-03 djhoward Add additional inherited methods 
 *                                    based on 173765 core changes.
 * 179038          07-Oct-03 djhoward Fix registerAsynchConsumerCallback to 
 *                                    properly hold onto callback.
 * 181796.6        05-Nov-03 djhoward Core SPI move to com.ibm.wsspi.sib.core
 * 186193          23-Dec-03 dcurrie  Add new ConsumerSessionMethods
 * 188161          23-Jan-04 dcurrie  Add new methods without Reliability
 * 188358          26-Jan-04 sambo    The unrecoverableReliability parameter has been moved
 * 195758.4        08-Apr-04 pnickoll Updated with new interface
 * 199220          20-Apr-04 pnickoll Removed methods that are no longer in the interface and corrected javadoc
 * 192474.1        20-Apr-04 pnickoll Removed extra methods methods
 * 176658.4.2.3    28-Apr-04 pnickoll Updated with core SPI changes.
 * 209436.3        22-Jun-04 pnickoll Milestone 8 Core SPI updates
 * 201972.4        28-Jul-04 pnickoll Update core SPI exceptions
 * 219476.4        31-Aug-04 dcurrie  Z3 core SPI updates
 * 436549          04-May-07 ajw      Support Pause/Resume of MDB
 * 496719          07-Feb-08 timoward BuildBreak, add a parameter to registerStoppableAsynchConsumerCallback
 * PK73713         16-Sep-08 ajw      Allow messageset to be unlocked and not increased lock count
 * F013661         20-Apr-12 chetbhat unlockAll(incrementUnlockCount) support
 * ============================================================================ 
 */
package com.ibm.ws.sib.api.jmsra.stubs;

import java.util.HashSet;
import java.util.Set;

import com.ibm.websphere.sib.SIDestinationAddress;
import com.ibm.websphere.sib.exception.SIIncorrectCallException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.wsspi.sib.core.OrderingContext;
import com.ibm.wsspi.sib.core.SIBusMessage;
import com.ibm.wsspi.sib.core.AsynchConsumerCallback;
import com.ibm.wsspi.sib.core.ConsumerSession;
import com.ibm.wsspi.sib.core.LockedMessageEnumeration;
import com.ibm.wsspi.sib.core.SICoreConnection;
import com.ibm.wsspi.sib.core.SIMessageHandle;
import com.ibm.wsspi.sib.core.SITransaction;
import com.ibm.wsspi.sib.core.StoppableAsynchConsumerCallback;
import com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;
import com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException;
import com.ibm.wsspi.sib.core.exception.SILimitExceededException;
import com.ibm.wsspi.sib.core.exception.SIMessageNotLockedException;
import com.ibm.wsspi.sib.core.exception.SISessionDroppedException;
import com.ibm.wsspi.sib.core.exception.SISessionUnavailableException;

/**
 * Stub class for ConsumerSession.
 */
public class ConsumerSessionStub implements ConsumerSession {

    private AsynchConsumerCallback _callback = null;

    private static Set _sessions = new HashSet();

    /**
     * Constructor for the stub
     */
    public ConsumerSessionStub() {
        _sessions.add(this);
    }

    /**
     * Delivers the messages. Calls consume messages on the callback
     * 
     * @param messages
     *            The messages to deliver
     * @throws Throwable
     */
    public void deliverAsynchMessages(SIBusMessage[] messages) throws Throwable {
        if (_callback != null) {
            LockedMessageEnumeration enumeration = new LockedMessageEnumerationStub(
                    this, messages);
            _callback.consumeMessages(enumeration);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.core.ConsumerSession#close()
     */
    public void close() {
        _sessions.remove(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.core.ConsumerSession#stop()
     */
    public void stop() {

        // Do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.core.ConsumerSession#getConnection()
     */
    public SICoreConnection getConnection() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.core.ConsumerSession#deregisterAsynchConsumerCallback()
     */
    public void deregisterAsynchConsumerCallback() {
        _callback = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.core.ConsumerSession#unlockAll()
     */
    public void unlockAll() {

        // Do nothing
    }

    /**
     * Returns the set of sessions
     * 
     * @return the set of sessions
     */
    public static Set getSessions() {
        return _sessions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.DestinationSession#getDestinationAddress()
     */
    public SIDestinationAddress getDestinationAddress() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.ConsumerSession#receiveNoWait(com.ibm.wsspi.sib.core.SITransaction)
     */
    public SIBusMessage receiveNoWait(SITransaction tran) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.ConsumerSession#receiveWithWait(com.ibm.wsspi.sib.core.SITransaction,
     *      long)
     */
    public SIBusMessage receiveWithWait(SITransaction tran, long timeout) {
        return null;
    }

    /**
     * @see com.ibm.wsspi.sib.core.ConsumerSession#getId()
     */
    public long getId() {
        return 0;
    }

    /**
     * @see com.ibm.wsspi.sib.core.ConsumerSession#activateAsynchConsumer(boolean)
     */
    public void activateAsynchConsumer(boolean arg0) {

        // Do nothing
    }

    /**
     * @see com.ibm.wsspi.sib.core.ConsumerSession#start(boolean)
     */
    public void start(boolean arg0) {

        // Do nothing
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.ConsumerSession#registerAsynchConsumerCallback(com.ibm.wsspi.sib.core.AsynchConsumerCallback,
     *      int, long, int, com.ibm.wsspi.sib.core.OrderingContext)
     */
    public void registerAsynchConsumerCallback(AsynchConsumerCallback callback,
            int maxActiveMessages, long messageLockExpiry, int maxBatchSize,
            OrderingContext extendedMessageOrderingContext)
            throws SISessionUnavailableException, SISessionDroppedException,
            SIConnectionUnavailableException, SIConnectionDroppedException,
            SIIncorrectCallException {

        _callback = callback;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.AbstractConsumerSession#deleteSet(com.ibm.wsspi.sib.core.SIMessageHandle[],
     *      com.ibm.wsspi.sib.core.SITransaction)
     */
    public void deleteSet(SIMessageHandle[] msgHandles, SITransaction tran)
            throws SISessionUnavailableException, SISessionDroppedException,
            SIConnectionUnavailableException, SIConnectionDroppedException,
            SIResourceException, SIConnectionLostException,
            SILimitExceededException, SIIncorrectCallException,
            SIMessageNotLockedException {

        // Do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.AbstractConsumerSession#unlockSet(com.ibm.wsspi.sib.core.SIMessageHandle[])
     */
    public void unlockSet(SIMessageHandle[] msgHandles)
            throws SISessionUnavailableException, SISessionDroppedException,
            SIConnectionUnavailableException, SIConnectionDroppedException,
            SIResourceException, SIConnectionLostException,
            SIIncorrectCallException, SIMessageNotLockedException {

        // Do nothing

    }

	public void unlockSet(SIMessageHandle[] msgHandles, boolean incrementLockCount) throws SISessionUnavailableException, SISessionDroppedException, SIConnectionUnavailableException, SIConnectionDroppedException, SIResourceException, SIConnectionLostException, SIIncorrectCallException, SIMessageNotLockedException
    {
      // Do nothing
      
    }

  public void deregisterStoppableAsynchConsumerCallback() throws SISessionUnavailableException, SISessionDroppedException, SIConnectionUnavailableException, SIConnectionDroppedException, SIIncorrectCallException {
        _callback = null;
		
	}

	public void registerStoppableAsynchConsumerCallback(StoppableAsynchConsumerCallback callback, int maxActiveMessages, long messageLockExpiry, int maxBatchSize, OrderingContext extendedMessageOrderingContext, int maxSequentialFailures, long hiddenMessageDelay) throws SISessionUnavailableException, SISessionDroppedException, SIConnectionUnavailableException, SIConnectionDroppedException, SIIncorrectCallException {
        _callback = callback;
		
	}

	@Override
	public void unlockAll(boolean incrementUnlockCount)
	throws SISessionUnavailableException, SISessionDroppedException,
	SIConnectionUnavailableException, SIConnectionDroppedException,
	SIResourceException, SIConnectionLostException, SIIncorrectCallException
	{
	  // Do nothing
	}
    
}
