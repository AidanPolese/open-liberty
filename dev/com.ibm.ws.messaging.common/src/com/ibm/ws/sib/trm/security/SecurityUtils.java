/*
 * COMPONENT_NAME: sib.admin.security
 *
 *  ORIGINS: 27
 *
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
 * 
 *
 * Change activity:
 *
 * Reason          Date        Origin   Description
 * --------------- ----------- -------- ----------------------------------------
 * d533600         24-Jul-2008 nottinga Initial Code Drop
 */
package com.ibm.ws.sib.trm.security;

import javax.security.auth.Subject;

/**
 * This interface contains helper methods for the Trm CredentialType class.
 */
public interface SecurityUtils 
{
  /* ------------------------------------------------------------------------ */
  /* isSIBServerSubject method                                    
  /* ------------------------------------------------------------------------ */
  /**
   * @param id the subject to query
   * @return true if the subject is the server subject
   */
  public boolean isSIBServerSubject(Subject id);
  /* ------------------------------------------------------------------------ */
  /* getOpaqueAuthorizationToken method                                    
  /* ------------------------------------------------------------------------ */
  /**
   * @param busName the name of the bus
   * @param meName  the name of the messaging engine
   * @param clientIdentity the subject to conver to an OAT
   * @return the OAT.
   */
  public byte[] getOpaqueAuthorizationToken(String busName, String meName, Subject clientIdentity);
}
