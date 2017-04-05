
/*
* ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012,2013
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 *    Newly added to liberty release   051212
 * ============================================================================
 */

package com.ibm.ws.sib.admin.mxbean;

import java.beans.ConstructorProperties;


public class MessagingSubscription {

  /*
   * ===========================================================================
   *
   * ATTENTION --- THIS CLASS IS SERIALIZABLE.
   *
   * You should take care when modifying this class since doing so could break
   * serialization. Please analyze how your changes will affect serialization,
   * and consider overriding the inherited serialization methods if necessary.
   *
   * Thank you.
   *
   * ===========================================================================
   */

  private static final long serialVersionUID = -9047402482550648638L;

  private long _depth = 0;
  private String _id = null;
  private int _maxMsgs = 1000;
  private String _name = null;
  private String _selector = null;
  private String _subscriberId = null;
  private String[] _topics = null;

  @ConstructorProperties({ "name", "id", "depth", "subscriberId" })
  public MessagingSubscription(long depth, String id, int maxMsgs, String name, String selector, String subscriberId, String[] topics) {
    this._depth = depth;
    this._id = id;
    this._maxMsgs = maxMsgs;
    this._name = name;
    this._selector = selector;
    this._subscriberId = subscriberId;
    this._topics = topics;
  }

  public long getDepth() {
    return _depth;
  }

  public String getId() {
    return _id;
  }

  public String getName() {
    return _name;
  }

  /**
   * @deprecated
   * @return
   */
  public String getIdentifier() {
    return _name;
  }

  public String getSelector() {
    return _selector;
  }

  public String getSubscriberId() {
    return _subscriberId;
  }

  public String[] getTopics() {
    return _topics;
  }
  public String toString(){
	  String details="SIBSubscription name= "+_name+": SIBSubscription id= "+_id+": Subscription depth= "+_depth+": Subscription subscriber Id="+_subscriberId;
	  return details;
  }

}
