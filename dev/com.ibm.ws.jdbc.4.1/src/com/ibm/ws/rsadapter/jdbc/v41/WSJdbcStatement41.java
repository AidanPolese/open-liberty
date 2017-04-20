/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.rsadapter.jdbc.v41;

import java.sql.Statement;

import com.ibm.ws.rsadapter.jdbc.WSJdbcConnection;
import com.ibm.ws.rsadapter.jdbc.WSJdbcStatement;

public class WSJdbcStatement41 extends WSJdbcStatement {

    /**
     * Do not use. Constructor exists only for PreparedStatement wrapper.
     */
    public WSJdbcStatement41() {
        super();
    }

    public WSJdbcStatement41(Statement stmtImplObject, WSJdbcConnection connWrapper, int theHoldability) {
        super(stmtImplObject, connWrapper, theHoldability);
    }
}