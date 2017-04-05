/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */

package com.ibm.wsspi.persistence.internal.eclipselink;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.Connector;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

public class PsSessionEventListener extends SessionEventAdapter {
     private final static TraceComponent tc = Tr.register(PsSessionEventListener.class);

     @Override
     public void preLogin(SessionEvent event) {
          DatabaseLogin login = event.getSession().getLogin();
          // add wrapping connector
          login.setConnector(new WrappingConnector(login.getConnector()));
     }

     public void removeWrappingConnector(AbstractSession session) {
          DatabaseLogin login = session.getLogin();
          Connector connector = login.getConnector();
          if (connector instanceof WrappingConnector) {
               WrappingConnector wc = (WrappingConnector) connector;
               if (tc.isDebugEnabled()) {
                    Tr.debug(tc, "unwrap() removing wrapping connector");
               }
               login.setConnector(wc.getDelegate());
          }
     }
}
