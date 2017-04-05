/*
 * @start_prolog@
 * Version: @(#) 1.3 SIB/ws/code/sib.jfapchannel.client.common.impl/src/com/ibm/ws/sib/jfapchannel/impl/rldispatcher/ReceiveListenerDispatchBarrier.java, SIB.comms, WASX.SIB, uu1215.01 08/07/14 06:31:26 [4/12/12 22:14:16]
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
 * D240062         040106 mattheg  Original
 * 515551          080714 vaughton ME-ME recovery performance (fix up threading)
 * ============================================================================
 */
package com.ibm.ws.sib.jfapchannel.impl.rldispatcher;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.utils.ras.SibTr;

/*
 * This class acts as a barrier. When the barrier is open, calling pass() has no effect. However,
 * if the barrier is locked then calling pass() will block until the barrier is unlocked.
 */

//@ThreadSafe
public class ReceiveListenerDispatchBarrier {
  private static final TraceComponent tc = SibTr.register(ReceiveListenerDispatchBarrier.class, JFapChannelConstants.MSG_GROUP, JFapChannelConstants.MSG_BUNDLE);

  //@start_class_string_prolog@
  public static final String $sccsid = "@(#) 1.3 SIB/ws/code/sib.jfapchannel.client.common.impl/src/com/ibm/ws/sib/jfapchannel/impl/rldispatcher/ReceiveListenerDispatchBarrier.java, SIB.comms, WASX.SIB, uu1215.01 08/07/14 06:31:26 [4/12/12 22:14:16]";
  //@end_class_string_prolog@

  static {
    if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "Source Info: " + $sccsid);
  }

  // Flag to indicate whether the barrier is open or not
  //@GuardedBy("this")
  private boolean barrierOpen = true;

  // Flag used to indicate a correct wakeup (as opposed to a spurious wakeup)
  //@GuardedBy("this")
  private boolean notified;

  // Attempt to pass the barrier. If the barrier is shut, the request will block
  public synchronized void pass () {
    if (!barrierOpen) { // If the barrier has been locked, block in here until notified
      notified = false;
      while (!notified) { // Not correctly notified continue to wait
        try {
          wait();
        } catch (InterruptedException e) {
          // No FFDC Code Needed
        }
      }
    }
  }

  // Shuts the barrier
  public synchronized void lock () {
    barrierOpen = false;
  }

  // Re-opens the barrier - note that the use of notifyAll here means that all blocked threads will be
  // allowed to proceed through the barrier which may cause multiple threads to put rather more data to
  // the queue than was intended by the original designer. To resolve this quirk the locking logic will
  // probably need moving inside the ReceiveListenerDispatchQueue class. Ideally only a single thread
  // should be notified and depending on how much this this thread adds to the queue another thread
  // should be woken until the queue becomes full again when blocking resumes.
  public synchronized boolean unlock () {
    boolean unlocked = false;
    if (!barrierOpen) {
      barrierOpen = true;
      notified = true; // This is a corect wakeup
      notifyAll();

      unlocked = true;
    }
    return unlocked;
  }
}
