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
 * Reason          Date      Origin   Description
 * -------------   ------    -------- ----------------------------------------
 *                 18-Jul-03 dcurrie  Creation 
 * 169626.6        21-Jul-03 pnickoll Updated test due to use properties on 
 *                                    core connection 
 * 181796.6        05-Nov-03 djhoward Core SPI move to com.ibm.wsspi.sib.core
 * 201972.4        28-Jul-04 pnickoll Update core SPI exceptions
 * ============================================================================
 */
 
package com.ibm.ws.sib.api.jmsra.stubs;

import java.util.Map;

import javax.security.auth.Subject;

import com.ibm.websphere.sib.exception.SIResourceException;
import com.ibm.wsspi.sib.core.SICoreConnection;
import com.ibm.wsspi.sib.core.exception.SIAuthenticationException;
import com.ibm.wsspi.sib.core.exception.SINotAuthorizedException;
import com.ibm.ws.sib.comms.ClientConnection;
import com.ibm.ws.sib.trm.TrmSICoreConnectionFactory;

/**
 * Stub class for TrmSICoreConnectionFactory.
 */
public class TrmSICoreConnectionFactoryStub extends TrmSICoreConnectionFactory
{

  @Override
public SICoreConnection createConnection(String userName, String password, Map properties)
  {
    return new SICoreConnectionStub(userName, password, properties);
  }

  @Override
public SICoreConnection createConnection(Subject subject, Map properties)
  {
    return new SICoreConnectionStub(subject, properties);
  }

/** {@inheritDoc} */
@Override
public SICoreConnection createConnection(ClientConnection cc, String credentialType, String userid, String password) throws SIResourceException, SINotAuthorizedException, SIAuthenticationException {
    // TODO Auto-generated method stub
    return null;
}
  
}
