/*
 * 
 * 
 * ============================================================================
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2012,2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 158444          030207 susana   Original
 * 158574          030210 susana   Rename make methods to clarify usage & fix setRedelivered
 * 158852          030213 susana   Scaffold implementation part 1
 * 159110          030218 susana   Scaffold implementation part 2
 * 159294          030303 susana   Tidy up message header fields - part 1
 * 164825          030502 susana   More header field refinements
 * 174700          030909 susana   Remove setBodyType
 * 177553          031113 susana   Add getJmsxDeliveryCount
 * 180540          031210 susana   Add uncheckedSetXxxxx
 * 179339.1        031216 susana   Add uncheckedSet for Forward and Reverse Routing Path
 * 180540.1.8      040113 susana   Allow nulls in uncheckedSetReplyXxxx
 * 186986          040119 susana   JmsDestination & JmsReplyTo to byte arrays
 * 182191.1        040120 markesc  Add pub-sub bridge support
 * 186446          040217 susana   JMSXAppId compact representation
 * 190322          040305 susana   Resolve JMS Property anomalies
 * 184157.4        040312 baldwint Preserve true message format information
 * 190322.3        040323 susana   Delete deprecated getPropertyNames
 * 182191.25.1     040415 markesc  Move get/setMQPublicationInfo up to JsApiMessage
 * 199144          040419 susana   Fix javadoc
 * 193066.5        040429 susana   Make setNonNullProperty public & remove redundant sets
 * 185656          040902 susana   Tidy up imports etc
 * 325186.1        051124 susana   Move remaining special property processing from api.jms.impl
 * 384726          060816 susana   Remove Java 5 unchecked warnings
 * 407670.1        070706 susana   Add alreadySent()
 * 572757          090210 susana   Fix generics whinge for getPropertyNameSet
 * 577272.1        090227 susana   Allow JMS API to get JMSXGroupSeq without fluffing up map/lists unnecessarily
 * ============================================================================
 */
package com.ibm.ws.sib.mfp;

import java.util.List;
import java.util.Set;

import com.ibm.websphere.sib.Reliability;
import com.ibm.websphere.sib.SIDestinationAddress;

/**
 * JsJmsMessage extends the general JsApiMessage interface and additionally
 * provides get/set methods for the specific JMS Header fields not already
 * covered by the superclass interfaces.
 * It may be used for accessing and processing any Jetstream JMS message.
 * <p>
 * All JMS message types (e.g. MapMessage) are specializations of
 * JsJmsMessage and can be 'made' from an existing JsJmsMessage of the
 * appropriate type.
 * 
 */
public interface JsJmsMessage extends JsApiMessage {

    /* ************************************************************************* */
    /* Message Body methods */
    /* ************************************************************************* */

    /**
     * Clear the message body.
     */
    public void clearBody();

    /* ************************************************************************* */
    /* Oddments */
    /* ************************************************************************* */

    /**
     * Determine whether the message should be considered to have already been
     * 'sent' into the SIB system.
     * 
     * @return boolean true if the message has already been 'sent', otherwise false.
     */
    public boolean alreadySent();

    /* ************************************************************************* */
    /* Get Methods for header fields */
    /* ************************************************************************* */

    /**
     * Get the value of the JMSRedelivered field from the message header.
     * 
     * @return The PersistenceType instance indicating the JMSDeliveryMode
     *         value (i.e. Persistent or Non-persistent).
     *         PersistenceType.UNKNOWN is returned if the field was not set.
     */
    public PersistenceType getJmsDeliveryMode();

    /**
     * Get the contents of the JMSExpiration field from the message header.
     * 
     * @return A Long containing the JMSExpiration.
     *         Null is returned if the field was not set.
     */
    public Long getJmsExpiration();

    /**
     * Get the contents of the JMSDeliveryTime field from the message header.
     * 
     * @return A Long containing the JMSDeliveryTime.
     *         Null is returned if the field was not set.
     */
    public Long getJmsDeliveryTime();

    /**
     * Get the contents of the JMSDestination field from the message header.
     * 
     * @return A byte array containing the JMSDestination.
     *         Null is returned if the field was not set.
     */
    public byte[] getJmsDestination();

    /**
     * Get the contents of the JMSReplyTo field from the message header.
     * 
     * @return A byte array containing the JMSReplyTo.
     *         Null is returned if the field was not set.
     */
    public byte[] getJmsReplyTo();

    /**
     * Get the value of JMSRedelivered from the message header.
     * 
     * @return A Boolean indicating whether the message has been redelivered.
     * 
     */
    public Boolean getJmsRedelivered();

    /**
     * Get the value of JMSType from the message header.
     * 
     * @return A String containing the JMSType
     *         Null is returned if the field was not set.
     */
    public String getJmsType();

    /**
     * Get the type of the message body.
     * 
     * @return The JmsBodyType instance indicating the type of the body - i.e. Null, Bytes, Map, etc.
     */
    public JmsBodyType getBodyType();

    /* ************************************************************************* */
    /* Set Methods for header fields */
    /* ************************************************************************* */

    /**
     * Set the value of the JMSDeliveryMode field in the message header.
     * 
     * @param value The PersistenceType instance indicating the JMSDeliveryMode
     *            (i.e. Persistent or Non-persistent).
     */
    public void setJmsDeliveryMode(PersistenceType value);

    /**
     * Set the contents of the JMSExpiration field in the message header.
     * 
     * @param value A Long containing the JMSExpiration.
     */
    public void setJmsExpiration(long value);

    /**
     * Set the contents of the JMSDeliveryTime field in the message header.
     * 
     * @param value A Long containing the JMSDeliveryTime.
     */
    public void setJmsDeliveryTime(long value);

    /**
     * Set the contents of the JMSDestination field in the message header.
     * 
     * @param value A byte array containing the JMSDestination.
     */
    public void setJmsDestination(byte[] value);

    /**
     * Set the contents of the JMSReplyTo field in the message header.
     * 
     * @param value A byte array containing the JMSReplyTo.
     */
    public void setJmsReplyTo(byte[] value);

    /**
     * Set the contents of the JMSType field in the message header.
     * 
     * @param value A String containing the JMSType.
     */
    public void setJmsType(String value);

    /* ************************************************************************* */
    /* Unchecked Set Methods for header and API meta-data fields */
    /* ************************************************************************* */

    /**
     * Set the contents of the ForwardRoutingPath field in the message header.
     * The parameter will not be checked for validity.
     * 
     * @param value A List containing the ForwardRoutingPath which is a set of
     *            SIDestinationAddress instances.
     */
    public void uncheckedSetForwardRoutingPath(List<SIDestinationAddress> value);

    /**
     * Set the contents of the ReverseRoutingPath field in the message header.
     * The parameter will not be checked for validity.
     * 
     * @param value A List containing the ReverseRoutingPath which is a set of
     *            SIDestinationAddress instances.
     */
    public void uncheckedSetReverseRoutingPath(List<SIDestinationAddress> value);

    /**
     * Set the contents of the topic Discriminator field in the message header.
     * The parameter will not be checked for validity.
     * 
     * @param value A String containing the Discriminator for Pub/Sub
     */
    public void uncheckedSetDiscriminator(String value);

    /**
     * Set the value of the TimeToLive field in the message header.
     * The parameter will not be checked for validity.
     * 
     * @param value A long containing the TimeToLive of the message.
     */
    public void uncheckedSetTimeToLive(long value);

    /**
     * Set the value of the DeliveryDelay field in the message header.
     * The parameter will not be checked for validity.
     * 
     * @param value A long containing the DeliveryDelay of the message.
     */
    public void uncheckedSetDeliveryDelay(long value);

    /**
     * Set the contents of the ReplyDiscriminator field in the message header.
     * The parameter will not be checked for validity.
     * 
     * @param value A String containing the Discriminator for any Pub/Sub reply.
     */
    public void uncheckedSetReplyDiscriminator(String value);

    /**
     * Set the value of the ReplyPriority field in the message header.
     * The parameter will not be checked for validity.
     * 
     * @param value An Integer containing the Priority for any reply.
     */
    public void uncheckedSetReplyPriority(Integer value);

    /**
     * Set the value of the ReplyReliability field in the message header.
     * The parameter will not be checked for validity.
     * 
     * @param value The Reliability instance representing the reliability for any
     *            reply message.
     */
    public void uncheckedSetReplyReliability(Reliability value);

    /**
     * Set the value of the ReplyTimeToLive field in the message header.
     * The parameter will not be checked for validity.
     * 
     * @param value A Long containing the TimeToLive for any reply.
     */
    public void uncheckedSetReplyTimeToLive(Long value);

    /* ************************************************************************* */
    /* Get Method for JMS Properties */
    /* ************************************************************************* */

    /**
     * Return the JMS property value with the given name as an Object.
     * <p>
     * This method is used to return in objectified format any property
     * that has been stored in the Message with any of the setXxxxProperty
     * method calls. If a primitive was stored, the Object returned will be
     * the corresponding object - e.g. an int will be returned as an Integer.
     * 
     * @param name The name of the JMS property
     * 
     * @return An object representing the value of the JMS property with
     *         the given name. Null is returned if the property was not set.
     */
    public Object getObjectProperty(String name);

    /* ************************************************************************* */
    /* Get Methods for calculated property value(s) */
    /* ************************************************************************* */

    /**
     * Get the JMSXDeliveryCount value.
     * 
     * @return An int indicating the number of times this message has been
     *         delivered.
     */
    public int getJmsxDeliveryCount();

    /**
     * Get the JMSXAppId value.
     * 
     * @return A String containing the JMSXAppId property.
     */
    public String getJmsxAppId();

    /* ************************************************************************* */
    /* Set Methods for JMS Properties */
    /* ************************************************************************* */

    /**
     * Set a Java object property value with the given name into the JMS Message.
     * If the value given is null, the property will effectively be deleted.
     * <p>
     * Note that this method should only be called for the objectified primitive
     * types (Integer, Double, Long ...) and Strings.
     * 
     * @param name The name of the Java object property.
     * @param value the Java object property value to set in the JMS Message.
     * 
     * @exception JMSXEception if the property is not valid
     */
    public void setObjectProperty(String name, Object value);

    /**
     * Set a non-null Java Object property value into the JMS Message.
     * 
     * Note that this method should only be called for the objectified primitive
     * types (Integer, Double, Long ...) and Strings.
     * 
     * @param name The name of the Java object property.
     * @param value the Java object property value to set in the JMS Message.
     * 
     * @exception JMSXEception if the property is not valid
     */
    public void setNonNullProperty(String name, Object value);

    /* ************************************************************************* */
    /* Set Methods for calculated property value(s) */
    /* ************************************************************************* */

    /**
     * Set the JMSXAppId value to a compact byte representation.
     * This method is to be used by other Jetstream components.
     * 
     * @param value A Byte representing the JMSXAppId property. It should be one of
     *            the constants defined in the MfpConstants class.
     */
    public void setJmsxAppId(Byte value);

    /**
     * Set the JMSXAppId value to a String value.
     * This method is to be used only by the MFP MQ interop implementation.
     * 
     * @param value A String containing the JMSXAppId value.
     */
    public void setJmsxAppId(String value);

    /* ************************************************************************* */
    /* Miscellaneous Methods for JMS Properties */
    /* ************************************************************************* */

    /**
     * Clear all the JMS Properties from the Message.
     */
    public void clearProperties();

    /*
     * Return a Set containing all of the property names.
     * 
     * @return a Set containing the names of all properties currently set.
     */
    public Set<String> getPropertyNameSet();

    /**
     * Return a boolean indicating whether a property with the given name has been set.
     * 
     * @return True if the property was set in the message, otherwise false.
     */
    public boolean propertyExists(String name);

    /**
     * getJMSXGroupSeq
     * Return the value of the JMSXGroupSeq property if it exists.
     * We can return it as object, as that is all JMS API wants. Actually it only
     * really cares whether it exists, but we'll return Object rather than
     * changing the callng code unnecessarily.
     * 
     * @return Object The value of JMSXGroupSeq, if it has been set, otherwise null.
     */
    public Object getJMSXGroupSeq();

}
