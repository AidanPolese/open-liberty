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

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Properties;

import org.eclipse.persistence.sessions.Connector;
import org.eclipse.persistence.sessions.Session;

import com.ibm.wsspi.persistence.internal.eclipselink.sql.WrappingConnection;

/**
 * A Connector implementation that delegates all operations to another Connector. The only piece of
 * support that this adds is that it wraps all java.sql.Connection objects with a
 * WrappingConnection.
 */
public class WrappingConnector implements Connector {
     private static final long serialVersionUID = 1L;
     private final Connector _del;

     public WrappingConnector(Connector delegate) {
          _del = delegate;
     }

     public Connector getDelegate() {
          return _del;
     }

     public Connection connect(Properties properties, Session session) {
          return new WrappingConnection(_del.connect(properties, session));
     }

     public void toString(PrintWriter writer) {
          _del.toString(writer);

     }

     public String getConnectionDetails() {
          return _del.getConnectionDetails();
     }

     public Object clone() {
          try {
               return super.clone();
          } catch (CloneNotSupportedException e) {
               throw new RuntimeException(e);
          }
     }
}
