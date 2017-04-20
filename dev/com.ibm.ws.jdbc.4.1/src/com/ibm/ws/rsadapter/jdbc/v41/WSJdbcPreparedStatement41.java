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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ibm.ws.rsadapter.jdbc.WSJdbcConnection;
import com.ibm.ws.rsadapter.jdbc.WSJdbcPreparedStatement;
import com.ibm.ws.rsadapter.spi.StatementCacheKey;

public class WSJdbcPreparedStatement41 extends WSJdbcPreparedStatement {

    /**
     * Do not use. Constructor exists only for CallableStatement wrapper.
     */
    public WSJdbcPreparedStatement41() {
        super();
    }

    public WSJdbcPreparedStatement41(PreparedStatement pstmtImplObject, WSJdbcConnection connWrapper,
                                     int theHoldability, String pstmtSQL) throws SQLException {
        super(pstmtImplObject, connWrapper, theHoldability, pstmtSQL);
    }

    public WSJdbcPreparedStatement41(PreparedStatement pstmtImplObject, WSJdbcConnection connWrapper,
                                     int theHoldability, String pstmtSQL,
                                     StatementCacheKey pstmtKey) throws SQLException {
        super(pstmtImplObject, connWrapper, theHoldability, pstmtSQL, pstmtKey);
    }
}