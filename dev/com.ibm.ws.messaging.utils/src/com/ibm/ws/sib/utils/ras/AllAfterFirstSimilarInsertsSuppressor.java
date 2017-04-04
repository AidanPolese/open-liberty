/* **************************************************************************
 * 
============================================================================
IBM Confidential OCO Source Materials

Copyright IBM Corp. 2012

The source code for this program is not published or otherwise divested
of its trade secrets, irrespective of what has been deposited with the
U.S. Copyright Office.
============================================================================

 *
 *
 *
 *  Change activity:
 *
 * Reason         Date        Origin   Description
 * -------------- ----------- -------- -------------------------------------
 *                                     Version 1.5 copied from CMVC
 *
 * **************************************************************************
 */
package com.ibm.ws.sib.utils.ras;

import com.ibm.ws.sib.utils.ras.SibTr.Suppressor;

/* ************************************************************************** */
/**
 * A Trace suppressor that suppresses every message with the same key
 *
 */
/* ************************************************************************** */
public class AllAfterFirstSimilarInsertsSuppressor extends AbstractSuppressor implements Suppressor
{
  /* -------------------------------------------------------------------------- */
  /* suppress method
  /* -------------------------------------------------------------------------- */
  /**
   * Suppress this message if we have seen the message key and its inserts. We
   * hash up the message key and the inserts so that we DON'T hold onto the
   * object references of the inserts (and hence prevent their garbage
   * collection).
   *
   * @see com.ibm.ws.sib.utils.ras.SibTr.Suppressor#suppress(java.lang.String, java.lang.String)
   * @param msgkey The message key
   * @param formattedMessage The formatted message resolved for language and inserts
   * @return true if the message should be suppressor
   */
  public synchronized SibTr.Suppressor.Decision suppress(String msgkey, String formattedMessage)
  {
    return super.suppress(formattedMessage,formattedMessage);
  }
}
