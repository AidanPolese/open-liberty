/*
 * @start_prolog@
 * Version: @(#) 1.51 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/proxyqueue/impl/ReadAheadProxyQueueImpl.java, SIB.comms, WASX.SIB, uu1215.01 08/04/16 21:38:29 [4/12/12 22:14:07]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2004, 2008
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * Creation        030702 prestona Original
 * d169897.2       030707 schmittm Provide remote client implementation of new Core API as defined
 * f172297         030722 schmittm continue to provide remote client implementation of new Core API as defined
 * F174602         030819 prestona Switch to using SICommsException
 * f174317         030829 mattheg  Add local transaction support
 * d172528         030905 mattheg  Add deliverException() method
 * f173765.2       030926 mattheg  Core API M4 update
 * f177889         030929 mattheg  Core API M4 completion
 * d178368         031008 mattheg  Ensure delete set is flowed at the correct priority
 * f181927         031111 mattheg  Allow global transations to be exposed
 * f187521.2.1     040126 mattheg  Unrecoverable reliability -- part 2
 * F188491         030128 prestona Migrate to M6 CF + TCP Channel
 * f191114         040218 mattheg  Multicast support (complete file restructure)
 * d192134         040226 mattheg  Ensure queue is created by subclasses, not by this class
 * d192293         040308 mattheg  NLS file changes
 * f192829         040407 mattheg  Client tuning parameters
 * f200337         040428 mattheg  Message order context implementation
 * f176658.4.2.2   040504 mattheg  deliveryImmediately flag change
 * D210212         040617 mattheg  Ensure order context is flowed during registerAsynchConsumer()
 * F201972.2       040727 mattheg  Core SPI Exceptions rework (not change flagged)
 * D217654         040813 mattheg  Remove un-needed locks
 * D199177         040816 mattheg  JavaDoc
 * D218324         040818 mattheg  Stop receiveWithWait on stopped session spinning
 * F219476.2       040906 prestona Z3 Core SPI changes
 * D235891         040930 mattheg  Runtime property standards
 * D225856         041006 mattheg  Update FFDC class name (not change flagged)
 * D249096         050129 prestona Fix proxy queue synchronization
 * F247845         050203 mattheg  Multicast enablement
 * D264771         050412 mattheg  FFDC compliance
 * D341593         060130 mattheg  Remove un-used locals
 * D384259         060815 prestona Remove multicast support
 * D424200         070426 prestona Readahead consumers hang in receiveWithWait if connection dies
 * 487006          080414 vaughton Refactor, tidyup & fix locking
 * ============================================================================
 */
package com.ibm.ws.sib.comms.client.proxyqueue.impl;

import com.ibm.ejs.ras.TraceNLS;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.sib.exception.SIErrorException;
import com.ibm.websphere.sib.exception.SIIncorrectCallException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.comms.CommsConstants;
import com.ibm.ws.sib.jfapchannel.Conversation;
import com.ibm.ws.sib.mfp.JsMessage;
import com.ibm.ws.sib.mfp.MessageDecodeFailedException;
import com.ibm.ws.sib.utils.ras.SibTr;
import com.ibm.wsspi.sib.core.SITransaction;
import com.ibm.wsspi.sib.core.exception.SIConnectionDroppedException;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;
import com.ibm.wsspi.sib.core.exception.SIConnectionUnavailableException;
import com.ibm.wsspi.sib.core.exception.SILimitExceededException;
import com.ibm.wsspi.sib.core.exception.SINotAuthorizedException;
import com.ibm.wsspi.sib.core.exception.SISessionDroppedException;
import com.ibm.wsspi.sib.core.exception.SISessionUnavailableException;

/*
 * This is the base implementation for a read ahead proxy queue. This queue has the ability
 * to allow clients to receive messages synchronously and asynchronously and is designed to be
 * implemented by subclasses for their specific need.
 */
public abstract class ReadAheadProxyQueueImpl extends AsynchConsumerProxyQueueImpl {
  private static final TraceComponent tc = SibTr.register(ReadAheadProxyQueueImpl.class, CommsConstants.MSG_GROUP, CommsConstants.MSG_BUNDLE);
  private static final TraceNLS nls = TraceNLS.getTraceNLS(CommsConstants.MSG_BUNDLE);

  //@start_class_string_prolog@
  public static final String $sccsid = "@(#) 1.51 SIB/ws/code/sib.comms.client.impl/src/com/ibm/ws/sib/comms/client/proxyqueue/impl/ReadAheadProxyQueueImpl.java, SIB.comms, WASX.SIB, uu1215.01 08/04/16 21:38:29 [4/12/12 22:14:07]";
  //@end_class_string_prolog@

  static {
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Source Info: " + $sccsid);
  }

  /*
   * Constructor
   */
  ReadAheadProxyQueueImpl (final ProxyQueueConversationGroupImpl group, final short id, final Conversation conversation) {
    super(group, id, conversation);
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>","group="+group+", id="+id+", conversation="+conversation);
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "<init>");
  }

  /*
   * Receive a message with wait
   */
  public synchronized JsMessage receiveWithWait (final long to, final SITransaction transaction) throws MessageDecodeFailedException,
                                                                                                        SISessionUnavailableException, SISessionDroppedException,
                                                                                                        SIConnectionUnavailableException, SIConnectionDroppedException,
                                                                                                        SIResourceException, SIConnectionLostException, SILimitExceededException,
                                                                                                        SIErrorException,
                                                                                                        SINotAuthorizedException,
                                                                                                        SIIncorrectCallException {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "receiveWithWait", "to="+to+", transaction="+transaction);

    long timeout = to;

    checkConversationLive();

    // Not allowed to call ReceiveWithWait on an asynchronous consumer
    if (getAsynchConsumerCallback() != null) {
      throw new SIIncorrectCallException(nls.getFormattedMessage("NOT_ALLOWED_WHILE_ASYNCH_SICO1020", null, null));
    }

    JsMessage message = null;

    // Optimisation: if we are started - try to get a message before even thinking about waiting
    if (getStarted()) {
      message = getQueue().get(getId());
    }

    // If we didn't get a message we need to wait for one to arrive
    if (message == null) {
      long startTime;
      boolean timedOut = false;
      // While the session is not closed, we haven't timed out and we are either not started or empty - wait
      while (!getClosed() && !timedOut && (!getStarted() || getQueue().isEmpty(getId()))) {
        startTime = System.currentTimeMillis();
        try {
          wait(timeout);
        } catch (InterruptedException e) {
          // No FFDC Code Needed
          if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Interupted!", e);
        }

        checkConversationLive();

        if (timeout != 0) {
          timeout -= (System.currentTimeMillis() - startTime);
          timedOut = timeout <= 0;
        }
      }

      // We could have exited the above loop for several reasons. If the conditions are right - see if we have a message
      if (!getClosed() && !getQueue().isEmpty(getId()) && getStarted()) {
        message = getQueue().get(getId());
      }
    }

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "receiveWithWait", "rc="+message);
    return message;
  }

  /*
   * Receive a message no wait
   */

  public JsMessage receiveNoWait (final SITransaction transaction) throws MessageDecodeFailedException,
                                                                         SISessionUnavailableException,
                                                                         SISessionDroppedException,
                                                                         SIConnectionUnavailableException,
                                                                         SIConnectionDroppedException,
                                                                         SIResourceException,
                                                                         SIConnectionLostException,
                                                                         SILimitExceededException,
                                                                         SIErrorException,
                                                                         SINotAuthorizedException,
                                                                         SIIncorrectCallException {
    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "receiveNoWait", "transaction="+transaction);

    checkConversationLive();

    // Why is ReceiveNoWait allowed with async but not ReceiveWithWait

    JsMessage message = null;

    if (getStarted() && !getClosed()) {
      message = getQueue().get(getId());
    }

    // If we didn't get a message, then we poke the remote machine to see if it has any messages
    if (message == null) {
      getConversationHelper().flushConsumer();

      if (getStarted() && !getClosed()) {
        message = getQueue().get(getId());
      }
    }

    if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "receiveNoWait");
    return message;
  }
}
