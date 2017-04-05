/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.wsspi.persistence.internal.eclipselink;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.sessions.DatabaseSession;

import com.ibm.websphere.ras.annotation.Trivial;

@Trivial
public class TargetServer extends ServerPlatformBase {
     public TargetServer(DatabaseSession newDatabaseSession) {
          super(newDatabaseSession);
     }

     @Override
     public Class<?> getExternalTransactionControllerClass() {
          return TransactionController.class;
     }

     @Override
     public SessionLog getServerLog() {
          return new Log();
     }

}
