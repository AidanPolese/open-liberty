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
 * ---------------  ------ -------- -------------------------------------------------
 * 171905.24        231003 tevans   Initial pub-sub ME-ME flows
 * 183715.1         241103 millwood Pre-prepare of transaction callback
 * 166834.10        011203 caseyj   Impl Subscriptions Hits, reimpl Total Subs
 * 175207.6         101203 cwilkin  Expiry notification 
 * 345318.1         220206 cwilkin  Provide eventPreUnlocked callback
 * ===========================================================================
 */
package com.ibm.ws.sib.processor.impl.store;

public class MessageEvents
{  
  public final static int PRE_COMMIT_ADD = 0;
  public final static int PRE_COMMIT_REMOVE = 1;
  public final static int POST_COMMIT_ADD = 2;
  public final static int POST_COMMIT_REMOVE = 3;
  public final static int POST_ROLLBACK_ADD = 4;
  public final static int POST_ROLLBACK_REMOVE = 5;
  public final static int UNLOCKED = 6;
  public final static int REFERENCES_DROPPED_TO_ZERO = 7;  
  public final static int PRE_PREPARE_TRANSACTION = 8;
  public final static int POST_COMMITTED_TRANSACTION = 9;  
  public final static int EXPIRY_NOTIFICATION = 10;
  public final static int COD_CALLBACK = 11;
  public final static int PRE_UNLOCKED = 12;
  
  //***Unused events***
  //public void eventCommitAdd(final Transaction transaction);
  //public void eventCommitRemove(final Transaction transaction);
  //public void eventCommitUpdate(final Transaction transaction);
  //public void eventRollbackAdd(final Transaction transaction);
  //public void eventRollbackRemove(final Transaction transaction);
  //public void eventPostCommitUpdate(Transaction transaction);
  //public void eventPostRollbackUpdate(Transaction transaction);
  //public void eventPrecommitUpdate(final Transaction transaction);
  //public void eventRestored();
  //public void eventRollbackUpdate(final Transaction transaction);
}
