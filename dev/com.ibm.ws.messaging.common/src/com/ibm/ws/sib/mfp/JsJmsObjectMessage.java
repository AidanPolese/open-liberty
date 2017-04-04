/*
 * 
 * 
 * ===========================================================================
 * IBM Confidential OCO Source Material
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ===========================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 158444          030207 susana   Original
 * 159252          030220 susana   Scaffold implementation part 4 - JMS message bodies
 * 164540          030428 susana   More header field refinements
 * SIB0212.mfp.1   061211 mphillip add get/setRealObject
 * SIB0121.mfp.5   070629 susana   getSerializedObject should throw ObjectFailedToSerializeException
 * ============================================================================
 */
package com.ibm.ws.sib.mfp;

import java.io.IOException;
import java.io.Serializable;

/**
 *  JsJmsObjectMessage extends JsJmsMessage and adds the get/set methods specific
 *  to a JMS ObjectMessage.
 */
public interface JsJmsObjectMessage extends JsJmsMessage {

  /**
   *  Get the byte array containing the serialized object which forms the
   *  payload of the message.
   *  The default value is null.
   *
   *  @return A byte array containing the serialized Object representing the
   *  payload of the message.
   *  @throws ObjectFailedToSerializeException if the real version of the object cannot be serialized
   */
  public byte[] getSerializedObject() throws ObjectFailedToSerializeException;

  /**
   *  Set the body (payload) of the message.
   *
   *  @param payload A byte array containing the serialized Object representing
   *  the payload of the message.
   */
  public void setSerializedObject(byte[] payload);


  /**
   *  Get the real object which forms the payload of the message.
   *  The default value is null.
   *
   *  @return the Serializable object that is the payload of the message.
   *  @throws IOException if the serialized version of the object cannot be deserialized
   *  @throws ClassNotFoundException if the class for the serialized object cannot be found
   */
  public Serializable getRealObject() throws IOException, ClassNotFoundException;

  /**
   *  Set the body (payload) of the message.
   *
   *  @param payload A serializable Object that is the payload of the message.
   */
  public void setRealObject(Serializable payload);

}
