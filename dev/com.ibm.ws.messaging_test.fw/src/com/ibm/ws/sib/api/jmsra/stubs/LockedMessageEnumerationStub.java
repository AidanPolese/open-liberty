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
 * Reason          Date        Origin   Description
 * --------------- ------      -------- ---------------------------------------
 * 174369.1        11-Sep-2003 dcurrie  Original
 * 181796.6        05-Nov-03   djhoward Core SPI move to com.ibm.wsspi.sib.core
 * 177816.5        03-Mar-04   sambo    jmsra to cope with extra exceptions thrown from the core SPI
 * 201972.4        28-Jul-04   pnickoll Update core SPI exceptions
 * 219476.4        31-Aug-04   dcurrie  Z3 core SPI updates
 * ============================================================================
 */
package com.ibm.ws.sib.api.jmsra.stubs;

import com.ibm.websphere.sib.exception.SIIncorrectCallException;
import com.ibm.wsspi.sib.core.SIBusMessage;
import com.ibm.wsspi.sib.core.ConsumerSession;
import com.ibm.wsspi.sib.core.LockedMessageEnumeration;
import com.ibm.wsspi.sib.core.SITransaction;
import com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException;
import com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException;
import com.ibm.wsspi.sib.core.exception.SISessionDroppedException;
import com.ibm.wsspi.sib.core.exception.SISessionUnavailableException;

/**
 * Stub class for LockedMessageEnumeration.
 */
public class LockedMessageEnumerationStub implements LockedMessageEnumeration {

    private final ConsumerSession _session;

    private final SIBusMessage[] _messages;

    private int _cursor = 0;

    /**
     * Constructor for the stub
     * 
     * @param theSession
     * @param theMessages
     */
    public LockedMessageEnumerationStub(ConsumerSession theSession,
            SIBusMessage[] theMessages) {
        _session = theSession;
        _messages = theMessages;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.core.LockedMessageEnumeration#nextLocked()
     */
    public SIBusMessage nextLocked() {
        SIBusMessage message = null;
        if (_cursor < _messages.length) {
            message = _messages[_cursor++];
        }
        return message;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.core.LockedMessageEnumeration#unlockCurrent()
     */
    public void unlockCurrent() {

        // Do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.core.LockedMessageEnumeration#deleteCurrent(com.ibm.ws.sib.core.SITransaction)
     */
    public void deleteCurrent(SITransaction arg0) {

        // Do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.core.LockedMessageEnumeration#deleteSeen(com.ibm.ws.sib.core.SITransaction)
     */
    public void deleteSeen(SITransaction arg0) {

        // Do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.core.LockedMessageEnumeration#resetCursor()
     */
    public void resetCursor() {
        _cursor = 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.core.LockedMessageEnumeration#getConsumerSession()
     */
    public ConsumerSession getConsumerSession() {
        return _session;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.ws.sib.core.LockedMessageEnumeration#getRemainingMessageCount()
     */
    public int getRemainingMessageCount() {
        return _messages.length - _cursor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.wsspi.sib.core.LockedMessageEnumeration#hasNext()
     */
    public boolean hasNext() throws SISessionUnavailableException,
            SISessionDroppedException, SIConnectionUnavailableException,
            SIConnectionDroppedException, SIIncorrectCallException {

        return (getRemainingMessageCount() > 0);

    }

}
