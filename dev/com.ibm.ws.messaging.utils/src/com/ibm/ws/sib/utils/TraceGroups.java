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
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 181801          031217 vaughton Original
 * 181801.7        031217 susana   Added entry for MFP API/SPI classes
 * 182639          040112 dcurrie  Added entry for SIB.ra
 * 186991          040113 kalus    Changed "Login" to "Security"
 * 180765.1        040607 gatfora  Added a Message Trace group
 * 184299.6        040608 eveleigh Added entry for SIB.SdoRepository
 * 209736          040616 djvines  Added entry for SIB.exitpoint
 * 182745.8        040907 dwc1     Added entry for WLM Classifier
 * 231247          040914 nottinga Added entry for SIB.dispatcher
 * 280486.1        050613 kgoodson Added entry for JMF
 * 284293          050701 gareth   Added entry for SIB.objectmanager
 * SIB0009.wsn.03  050727 mattheg  Added entry for WSN
 * 302621          050928 nottinga Added entry for componentization
 * 310866          051031 djvines  Added entry for "MESSAGETRACECONTENTSMEDIATION"
 * 339187          060118 vaughton Added entry for Clients
 * 277610          060127 matrober SIBMigrationUtil missing from TraceGroups
 * SIB0117a.2      070322 mleming  Added entry for MQLink fap flows
 * 456176          070801 pnickoll Altered TRGRP_RA to be SIBJmsRaCommon (was SIBRa before)
 * 452462.7        070824 susana   Add TRGRP_MFPSDO to separate out mfp.sdo trace
 * 516687          080509 vaughton Add TRGRP_JFAPSUMMARY trace group
 * 538413          080724 djvines  Move JFAPSUMMARY to MESSAGETRACEJFAP, add MESSAGETRACECOMMS
 * 596315          090626 djvines  Add TRGRP_MFPMQ
 * 599149          010709 pbroad   Add minimal ME<->ME comms trace 
 * ============================================================================
 */

package com.ibm.ws.sib.utils;

/**
 * This class contains 'product' trace group constants. All trace groups
 * should be defined here so that we have a single point of reference.
 */

public final class TraceGroups {

  public final static String TRGRP_ADMIN             = "SIBAdmin";
  public final static String TRGRP_CLIENT            = "SIBClient";
  public final static String TRGRP_COMMON            = "SIBCommon";
  public final static String TRGRP_COMMS             = "SIBCommunications";
  public final static String TRGRP_COMMS_FAP_FLOW    = "SIBCommunicationsFapFlows";
  public final static String TRGRP_CORE              = "SIBCore";
  public final static String TRGRP_DISPATCHER        = "SIBDispatcher";
  public final static String TRGRP_EXAMPLE           = "SIBExample";
  public final static String TRGRP_EXITPOINT         = "SIBExitpoint";
  public final static String TRGRP_IBL               = "SIBIbl";
  public final static String TRGRP_JFAP              = "SIBJFapChannel";
  public final static String TRGRP_JFAPSUMMARY       = "SIBJFapSummary";
  public final static String TRGRP_JMF               = "SIBJmf";
  public final static String TRGRP_JMSRA             = "SIBJmsRa";
  public final static String TRGRP_JMS_EXT           = "SIBJms_External";
  public final static String TRGRP_JMS_INT           = "SIBJms_Internal";
  public final static String TRGRP_MATCHSPACE        = "SIBMatchSpace";
  public final static String TRGRP_MEDIATIONS        = "SIBMediations";
  public final static String TRGRP_MESSAGETRACE      = "SIBMessageTrace";
  public final static String TRGRP_MESSAGETRACEJFAP  = "SIBMessageTraceJFap";
  public final static String TRGRP_MESSAGETRACECOMMS = "SIBMessageTraceComms";
  public final static String TRGRP_MESSAGETRACEMECOMMS = "SIBMessageTraceMEComms";
  public final static String TRGRP_MESSAGETRACECONTENTSMEDIATION = "SIBMessageTraceContentsMediation";
  public final static String TRGRP_MFP               = "SIBMfp";
  public final static String TRGRP_MFPAPI            = "SIBMfpApi";
  public final static String TRGRP_MFPMQ             = "SIBMfpMq";
  public final static String TRGRP_MFPSDO            = "SIBMfpSdo";
  public final static String TRGRP_MQFAP             = "SIBMqFapChannel";
  public final static String TRGRP_MSGSTORE          = "SIBMessageStore";
  public final static String TRGRP_MSGMIGRATION      = "SIBMigrationUtil";
  public final static String TRGRP_OBJMANAGER        = "SIBObjectManager";
  public final static String TRGRP_PROCESSOR         = "SIBProcessor";
  public final static String TRGRP_PSB               = "SIBPsb";
  public final static String TRGRP_RA                = "SIBJmsRaCommon";
  public final static String TRGRP_RMQ               = "SIBRmq";
  public final static String TRGRP_SDOREP            = "SIBSdoRepository";
  public final static String TRGRP_SECURITY          = "SIBSecurity";
  public final static String TRGRP_TRM               = "SIBTrm";
  public final static String TRGRP_UTILS             = "SIBUtils";
  public final static String TRGRP_WLMCLASSIFIER     = "SIBWlmClassifier";
  public final static String TRGRP_WSN               = "SIBWsn";
  public final static String TRGRP_COMPONENTIZATION  = "SIBOSGi";

}
