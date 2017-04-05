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
 * ---------------  ------ -------- --------------------------------------------
 * 171905.2         170703 rjnorris Initial implementation
 * 171905.14        050903 rjnorris Rename  AckExpected threshold 
 * 171905.32        141103 tevans   PubSub AckExpected/Nack 
 * 224817           170804 rjnorris Added a Max value for increasing AckExp interval 
 * 215177.3         250804 rjnorris Add ProtocolVersion
 * SIB0105.mp.5     040607 cwilkin  Link Transmission Health States
 * 510343           020908 dware    Move GD_GAP_CURIOSITY_THRESHOLD to CustomProperties
 * ============================================================================
 */
package com.ibm.ws.sib.processor.gd;

// Import required classes.

public final class GDConfig
{

 /**
  * Version number of protocol
  */
  public static byte PROTOCOL_VERSION = 1;
  
  /**
   * Timer related parameters needed for Guaranteed delivery.
   */
  /**
   * The timer granularity in milliseconds
   */
  public static int GD_TIMER_TICK = 50;
  /**
   * The number of timers expected to be active at any instant of time.
   */
  public static int GD_EXPECTED_TIMERS = 500;
  /**
   * The number of threads to dispatch the expiry function for timers.
   */
  public static int GD_MAX_TIMER_THREADS = 5;
  /**
   * GD timer values
   */
  public static int GD_ACK_PROPAGATION_THRESHOLD = 200;
  public static int GD_RELEASE_PROPAGATION_THRESHOLD = 500;
  public static int GD_NACK_REPETITION_THRESHOLD = 6000;
  public static int GD_MAX_NACK_REPETITION_THRESHOLD = 600000;
  public static int GD_REQUESTED_FORGETTING_THRESHOLD = 500;

  // Currently the FPT is only used for pubend streams. FPT < DCT (should be)
  public static long GD_FINALITY_PROPAGATION_THRESHOLD = 550000;
  // Currently the AckExp is only used for Source streams.
  public static int GD_ACK_EXPECTED_THRESHOLD = 3000;
  public static int GD_ACK_EXPECTED_THRESHOLD_MAX = 48000;
 
  // The receiving window for messages in a SHB, in milliseconds.
  public static int GD_RECV_WINDOW = 4000;
  // the SHB will remember knowledge GD_PAST_WINDOW ms before oack, to satisfy durable subs
  public static int GD_PAST_WINDOW = 2000;
  // the maximum number of V ticks stored in a KnVTickTable in an output stream
  // and the maximum number of V ticks in a KnStream in an InternalOutputStream
  public static int MAX_V_TICKS = 1000;

  // the following is a temporary parameter for recv window nacking.
  public static long GD_RWA_CURIOSITY_THRESHOLD = 100;
  // the following is a temporary parameter for nacking ticks that have fallen into recv window
  // only if they are greater than this length (in ms)
  public static int GD_MIN_RW_NACK_SIZE = 100;
  // The maximum size of a nack sent on a broker-broker link. units in milliseconds.
  public static int GD_NACK_CHUNK_SIZE = 600;
  
  // Arbitrary constant which indicates the most messages we'll keep
  // around while waiting to determine the status of a stream.
  public static final int FLUSH_CACHE_LENGTH = 10;

  // Number of "are you flushed" rounds we'll attempt before giving up.
  public static final int FLUSH_QUERY_ATTEMPTS = 3;

  // Milliseconds between "are you flushed" queries.
  public static long FLUSH_QUERY_INTERVAL = 3000;

  // Number of "request flush" rounds we'll attempt before giving up.
  public static final int REQUEST_FLUSH_ATTEMPTS = 10;

  // Milliseconds between "request flush" rounds
  public static final long REQUEST_FLUSH_INTERVAL = 10000;

  // Milliseconds between health checks on streams
  public static final int BLOCKED_STREAM_HEALTH_CHECK_INTERVAL = 10000;

}
