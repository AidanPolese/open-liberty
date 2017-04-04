/*
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
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 *                                 Version 1.6 copied from CMVC
 * ============================================================================
 */

package com.ibm.ws.sib.admin;


/**
 * Public Admin utilities interface
 */
public interface JsAdminUtilsInterface {

  /*
   * Given a uuid of a messaging engine, find the name of that messaging engine 
   * Use this version when getting the UUID simply to output it to an exception 
   * insert, or error message. 
   */
  public String getMENameByUuidForMessage(String meUuid);

  /*
   * Given a uuid of a messaging engine, find the name of that messaging engine
   */
  public String getMENameByUuid(String meUuid);


  
  /*
   * Given a uuid of a MQ server bus member, find the name of that MQ server bus member
   */
  public String getMQServerBusMemberNameByUuid(String mqUuid);

  /*
   * Given a name of a messaging engine, find the uuid of that messaging engine
   */  
  public String getMEUUIDByName(String meName);

}

