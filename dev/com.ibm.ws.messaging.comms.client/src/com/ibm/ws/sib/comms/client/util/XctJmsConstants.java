/*
 * @start_prolog@
 * Version: @(#) 1.7 SIB/ws/code/sib.api.jms/src/com/ibm/websphere/sib/api/jms/XctJmsConstants.java, SIB.api.jms, WASX.SIB, aa1225.01 12/02/09 23:59:25 [7/2/12 06:13:58]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70  Copyright IBM Corp. 2011,2012
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * Reason            Date   Origin   Description
 * ---------------   ------ -------- ------------------------------------------
 * F1344-55234.2     011211 skavitha Original   
 * F1344-55237       021211 skavitha PubSub annotation strings added
 * 724314            061211 skavitha New XCT strings added
 * F1344-55548       071211 skavitha New XCT strings added
 * F1344-55985       161211 skavitha New XCT strings added
 * F1344-55545       191211 skavitha New XCT strings added
 * F1344-59478       090212 skavitha Constants for data capture added
 * ============================================================================
 */

package com.ibm.ws.sib.comms.client.util;


/**
 * This file contains constants which are used in XCT annotations and associations
 * 
 */
public interface XctJmsConstants
{
	
    // ***********************XCT CONSTANTS ****************************************
	
    public static final String XCT_JMS ="JMS";
    public static final String XCT_SIBUS ="SIBus";
    public static final String XCT_DEST_NAME ="DestinationName";
    public static final String XCT_DEST_TYPE ="DestinationType";
    public static final String XCT_DEST_TYPE_QUEUE ="Queue";
    public static final String XCT_DEST_TYPE_TOPICSPACE ="TopicSpace";
    public static final String XCT_TRANSACTED ="Transacted";
    public static final String XCT_TRANSACTED_TRUE ="True";
    public static final String XCT_TRANSACTED_FALSE ="False";
    public static final String XCT_MESSAGE_ID ="MessageID";
    public static final String XCT_SYSTEM_MESSAGE_ID ="SystemMessageID";
    public static final String XCT_FAILED ="Failed";
    public static final String XCT_ERROR_MSG_01 ="NoLocalisation";
    public static final String XCT_ERROR_MSG_02 ="SessionNotAvailable";
    public static final String XCT_ERROR_MSG_03 ="NotAuthorized";
    public static final String XCT_ERROR_MSG_04 ="ConfigurationError";
    public static final String XCT_ERROR_MSG_05 ="TemporaryDestinationNotFound";
    public static final String XCT_ERROR_MSG_06 ="Exception";
    public static final String XCT_NO_MESSAGE ="NoMessage";    
    public static final String XCT_RELIABILITY ="Reliability";
    public static final String XCT_ME_UUID ="MessagingEngineUuid";
    public static final String XCT_SOURCE_ME_UUID ="SourceMessagingEngineUuid";
    public static final String XCT_TARGET_ME_UUID ="TargetMessagingEngineUuid";
    public static final String XCT_JMS_SEND ="JMS_SEND";
    public static final String XCT_JMS_RECV ="JMS_RECV";
    public static final String XCT_JMS_TEXT_MSG_SUFFIX = "txt";
    public static final String XCT_JMS_MAP_MSG_SUFFIX = "map";
    

    // *********XCT SEND ANNOTATION/ASSOCIATION CONSTANTS *****************************
	
    public static final String XCT_SEND ="Send";
    public static final String XCT_PROXY_SEND ="ProxySend";
    public static final String XCT_SEND_MESSAGE ="SendMessage";
    public static final String XCT_ACK_MODE ="AcknowledgeMode";
    public static final String XCT_ACK_MODE_TRANSACTED ="SESSION_TRANSACTED";
    public static final String XCT_ACK_MODE_CLIENT ="CLIENT_ACKNOWLEDGE";
    public static final String XCT_ACK_MODE_DUPS_OK ="DUPS_OK_ACKNOWLEDGE";
    public static final String XCT_ACK_MODE_AUTO ="AUTO_ACKNOWLEDGE";
    public static final String XCT_ACK_MODE_NONE ="NONE";
      

    // *********XCT RECEIVE ANNOTATION/ASSOCIATION CONSTANTS *****************************
	 
    public static final String XCT_RECEIVE ="Receive";
    public static final String XCT_CONSUME_SEND ="ConsumeSend";
    public static final String XCT_RECEIVE_NO_WAIT ="ReceiveNoWait";
    public static final String XCT_RECEIVE_WITH_WAIT ="ReceiveWithWait";
    public static final String XCT_PROXY_RECEIVE_NO_WAIT ="ProxyReceiveNoWait";
    public static final String XCT_PROXY_RECEIVE_WITH_WAIT ="ProxyReceiveWithWait";
    public static final String XCT_RECEIVE_INBOUND ="ReceiveInBound";
    public static final String XCT_CONSUME_MESSAGE ="ConsumeMessage";
    public static final String XCT_PROCESS_MESSAGE ="ProcessMessage";
    public static final String XCT_ID ="XctId";
    public static final String XCT_ROOT_ID ="XctRootId";	
    public static final String XCT_CLIENT_ID ="ClientID";
    public static final String XCT_SUBSCRIPTION_ID ="SubscriptionID";
    public static final String XCT_SUBSCRIPTION ="Subscription";
    public static final String XCT_SUBSCRIPTION_DURABLE ="Durable";
    public static final String XCT_SUBSCRIPTION_NONDURABLE ="NonDurable";
      
                
}
