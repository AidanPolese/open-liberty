/*
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
 * Reason           Date   Origin   Description
 * ---------------  ------ -------- -------------------------------------------
 * 180483.1         271003 tevans   Interface changes to facilitate remoteGet
 * 181796.1         051103 gatfora  New MS5 Core API
 * 171905.35        201103 rjnorris Remote to Remote PubSub
 * 185688           161203 gatfora  Package restructure for items and itemstreams
 * 171905.44        171203 astley   Stream IDs for pub/sub.
 * 186256.1         301203 tevans   Refactor GD/stream/flush code
 * 171905.49        260104 rjnorris Improve batch handling   
 * 199574           050405 gatfora  Remove use of ArrayLists.
 * 186484.19.3      060704 tevans   Flush at target improvements
 * 201972.1         270704 gatfora  Core SPI Exception rework
 * 244425           151204 tpm      isDestinationFull
 * 244425.1         130105 tpm      forcePut & isDestFull optimisations
 * 266451           080405 tpm      Interbus dest full use routing dest
 * 464463           070708 nyoung   Messages coming off a link are incorrectly exceptioned
 * 464463.1         210708 nyoung   Messages coming off a link are incorrectly exceptioned - pt 2
 * 464463.2         250708 nyoung   Messages coming off a link are incorrectly exceptioned - pt 3
 * 510343           010908 dware    Add methods for issuing servicability messages
 * ============================================================================
 */
package com.ibm.ws.sib.processor.impl.interfaces;

// Import required classes.
import java.util.List;

import com.ibm.websphere.sib.Reliability;
import com.ibm.websphere.sib.exception.SIException;
import com.ibm.websphere.sib.exception.SIIncorrectCallException;
import com.ibm.websphere.sib.exception.SINotPossibleInCurrentConfigurationException;
import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.ws.sib.mfp.JsDestinationAddress;
import com.ibm.ws.sib.processor.gd.ExpressTargetStream;
import com.ibm.ws.sib.processor.gd.GuaranteedTargetStream;
import com.ibm.ws.sib.processor.impl.store.items.MessageItem;
import com.ibm.wsspi.sib.core.exception.SIConnectionLostException;
import com.ibm.wsspi.sib.core.exception.SIRollbackException;

/**
 * @author tevans
 */
/**
 * An interface class for the different types of inpt class.
 */

public interface MessageDeliverer
{
 
  /**
   * @param msgList   List of JsMessages to deliver   
   */
  public void deliverOrderedMessages(List msgList,
                                     GuaranteedTargetStream targetStream,
                                     int priority,
                                     Reliability reliability)
    throws
      SIIncorrectCallException,
      SIResourceException,
      SIConnectionLostException,
      SIRollbackException,
      SINotPossibleInCurrentConfigurationException;
     
  /**
   * @param msg   An express JsMessage to deliver   
   */
  public void deliverExpressMessage(MessageItem msgItem, ExpressTargetStream ets)
    throws
      SIIncorrectCallException,
      SIResourceException,
      SIConnectionLostException,
      SIRollbackException,
      SINotPossibleInCurrentConfigurationException;

  public void forceTargetBatchCompletion(BatchListener listener) 
    throws SIResourceException;

  /**
   * We need to check if a destination is able to accept a message before
   * accepting it onto the target stream. This is more than just a check to see whether the
   * destination is full (defect 244425). It also encompasses other situations such as where
   * a target destination has been put-disabled or where an authorisation check fails.
   * 
   * WARNING - specific to stream full case
   * =======
   * Once a stream is full it should not be considered not_full until the stream has reduced the 
   * backlog a bit. Therefore there is some hysteresis in the switching of destinations from full 
   * to not_full
   * 
   * @param addr the routing destination address if this is a link.
   * If not, then this parameter will be null.
   * @return reason code signifying whether the destination can accept messages or the reason for
   *         not being able to do so.
   */
  public int checkAbleToAcceptMessage(JsDestinationAddress addr) throws SIException;
  
  /**
   * See if a blocked destination is still blocked or whether it is able to accept messages.
   * 
   * WARNING - specific to stream full case
   * =======
   * Once a stream is full it should not be considered not_full until the stream has reduced the 
   * backlog a bit. Therefore there is some hysteresis in the switching of destinations from full 
   * to not_full.
   * 
   * @return reason code signifying whether the destination can accept messages or the reason for
   *         not being able to do so.
   */
  public int checkStillBlocked();    

  /**
   * Report a long lived gap in a GD stream (510343)
   * @param sourceMEUuid
   * @param gap
   */
  public void reportUnresolvedGap(String sourceMEUuid, long gap);
  
  /**
   * Issue an all clear on a previously reported gap in a GD stream (510343)
   * @param sourceMEUuid
   * @param filledGap
   */
  public void reportResolvedGap(String sourceMEUuid, long filledGap);
  
  /**
   * Report a high proportion of repeated messages being sent from a remote ME (510343)
   * @param sourceMEUUID
   * @param percent
   */
  public void reportRepeatedMessages(String sourceMEUUID, int percent);
}
