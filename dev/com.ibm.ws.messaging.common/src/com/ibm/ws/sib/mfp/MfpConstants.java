/*
 * 
 * 
 * ===========================================================================
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2012,2014
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
 * 158589          030303 susana   Make Factory class names package level
 * 164548          030509 susana   Fix package of message bundle as moved
 * 172633.1        031027 susana   Add Priority & TimeToLive max & min values
 * 181801.6        031217 vaughton Switch to utils trace group
 * 172521.1        040105 baldwint Schema propagation
 * 186446          040217 susana   JMSXAppId compact representation
 * 172024          040401 baldwint Add FFDC diagnostic support
 * 193066          040405 susana   SIMessage property support
 * 189574          040426 baldwint Dump unassembled JMF messages
 * 195445.26       040514 susana   Change message prefix from SIFP to CWSIF
 * 193585.5        040520 susana   Move to SIRCMessages to api
 * 203652          040526 susana   Add flag values for message constructors
 * 205905          040527 susana   Change SIRCMessages to CWSIKMessages
 * 218660.1        040818 susana   Add MESSAGE_HANDLE_SEPARATOR
 * 233061.1        040920 susana   Further SystemContext Handler support
 * 339872          060131 susana   Remove redundant field setting on inbound MQ messages
 * SIB0112c.mfp.1  060817 susana   MemMgmt: Add ability to dump a List of DataSlices
 * 394186          061109 mphillip JMF Schema name added
 * SIB0121.mfp.1   061211 mphillip add UDF constants for updateDataFields
 * 436884          070514 susana   Initialize inbound MQ Broker messages more lazily
 * 452462.7        070824 susana   Add SDO_GROUP to separate out mfp.sdo trace
 * 497803          080213 susana   Add UDF constants for toVerboseString()
 * 499947.1        080303 susana   Potential for OutOfMemory if dump or trace huge message
 * 542981          080812 susana   Add Mime
 * 499947.2        081229 susana   Remove BUFFER_MAX as superseded by custom property
 * ============================================================================
 */
/**
 *   This class just contains component-wide constants.
 *   It does not include any constants used by other components.
 *
 */
package com.ibm.ws.sib.mfp;

public class MfpConstants {

    public final static String MSG_GROUP = com.ibm.ws.sib.utils.TraceGroups.TRGRP_MFP;
    public final static String MSG_BUNDLE = "com.ibm.ws.sib.mfp.CWSIFMessages";
    public final static String SDO_GROUP = com.ibm.ws.sib.utils.TraceGroups.TRGRP_MFPSDO;

    public final static String EXCEPTION_MSG_BUNDLE = "com.ibm.websphere.sib.CWSIKMessages";
    public final static String EXCEPTION_MESSAGE_KEY_PREFIX = "DELIVERY_ERROR_SIRC_";

    public final static String JS_MESSAGE_FACTORY_CLASS = "com.ibm.ws.sib.mfp.impl.JsMessageFactoryImpl";
    public final static String JS_JMS_MESSAGE_FACTORY_CLASS = "com.ibm.ws.sib.mfp.impl.JsJmsMessageFactoryImpl";
    public final static String COMP_HANDSHAKE_CLASS = "com.ibm.ws.sib.mfp.impl.CompHandshakeImpl";

    public final static int MIN_PRIORITY = 0;
    public final static int MAX_PRIORITY = 9;
    public final static int MIN_TIME_TO_LIVE = 0;
    public final static int MIN_DELIVERY_DELAY = 0;
    /**
     * The maximum permitted value for timeToLive. This is equivalent to
     * approximately 291 million years. The imposition of such a limit may
     * appear arbitrary, but it prevents poorly defined behaviour which occurs
     * if we permit values up to Long.MAX_VALUE.
     * 
     */
    public final static long MAX_TIME_TO_LIVE = 0x7f8fe33b8ac46bffL;

    /**
     * The maximum permitted value for timeToLive. This is equivalent to
     * approximately 291 million years. The imposition of such a limit may
     * appear arbitrary, but it prevents poorly defined behaviour which occurs
     * if we permit values up to Long.MAX_VALUE.
     * 
     */
    public final static long MAX_DELIVERY_DELAY = 0x7f8fe33b8ac46bffL;

    /**
     * Flags to denote special processing by message constructors.
     */
    public final static int CONSTRUCTOR_NO_OP = 0x0;
//public final static int CONSTRUCTOR_NO_JMS_FIELDS        = 0x1; // No longer used
    public final static int CONSTRUCTOR_INBOUND_MQ = 0x2; // Added for 339872
    public final static int CONSTRUCTOR_INBOUND_MQ_BROKER = 0x3; // Added for 436884

    /**
     * To avoid the overhead of holding the same 28 character String in every
     * Jetstream produced JMS message, a single byte will be used to represent
     * the appropriate String value.
     * For upward compatibility, an array of String values is indexed by the
     * the single byte.
     */
    //87548
    //Changing the name to WebSphere Embedded Messaging
    public final static String WPM_JMSXAPPID_VALUE = "WebSphere Embedded Messaging";

    public final static Byte NO_JMSXAPPID = new Byte((byte) 0);
    public final static Byte WPM_JMSXAPPID = new Byte((byte) 1);

    public final static int MAX_JMSXAPPID = WPM_JMSXAPPID.intValue();

    public final static String[] JMSXAPPIDS = new String[MAX_JMSXAPPID + 1];

    static {
        JMSXAPPIDS[NO_JMSXAPPID.intValue()] = null;
        JMSXAPPIDS[WPM_JMSXAPPID.intValue()] = WPM_JMSXAPPID_VALUE;
    }

    /**
     * Constants to control the action of the MFP FFDC diagnostic module
     */
    public final static Object DM_BUFFER = new Object();
    public final static Object DM_MESSAGE = new Object();
    public final static Object DM_SLICES = new Object();

    /**
     * Separator character for String representations of Message Handles
     */
    public final static String MESSAGE_HANDLE_SEPARATOR = "_";

    /**
     * Property name for Maelstrom - local copy of the constant:
     * com.ibm.ws.webservices.engine.transport.jms.JMSConstants.JMS_PRP_TRANSVER
     * 
     * The property has been copied here to avoid a runtime dependency which may
     * be missing in a thin JMS client.
     */
    public final static String PRP_TRANSVER = "transportVersion";

    /**
     * Ecore namespace for the JMF Schema
     */
    public static final String MFP_SCHEMA_URI = "com.ibm.ws.sib.mfp/schema";

    /**
     * Indicator that the SOAP format string contains Content-Type rather than WSDL etc
     */
    public static final char MIME_CONTENT_TYPE_INDICATOR = '#';

    /**
     * Constants for the reason for calling updateDataFields
     */
    public static final int UDF_FLATTEN = 1;
    public static final int UDF_ENCODE = 2;
    public static final int UDF_MAKE_INBOUND_SDO = 3;
    public static final int UDF_MAKE_INBOUND_JMS = 4;
    public static final int UDF_MAKE_INBOUND_OTHER = 5;
    public static final int UDF_GET_COPY = 6;
    public static final int UDF_VERBOSE_STRING = 7;
}
