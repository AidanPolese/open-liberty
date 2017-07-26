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
package com.ibm.ws.rsadapter.jdbc.v42;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import com.ibm.ws.ffdc.FFDCFilter;
import com.ibm.ws.rsadapter.jdbc.WSJdbcConnection;
import com.ibm.ws.rsadapter.jdbc.WSJdbcUtil;
import com.ibm.ws.rsadapter.jdbc.v41.WSJdbc41DatabaseMetaData;

public class WSJdbc42DatabaseMetaData extends WSJdbc41DatabaseMetaData {

    public WSJdbc42DatabaseMetaData(DatabaseMetaData metaDataImpl, WSJdbcConnection connWrapper) throws SQLException {
        super(metaDataImpl, connWrapper);
    }

    @Override
    public boolean supportsRefCursors() throws SQLException {
        try {
            return mDataImpl.supportsRefCursors();
        } catch (SQLException ex) {
            FFDCFilter.processException(ex, "com.ibm.ws.rsadapter.jdbc.v42.WSJdbc42DatabaseMetaData.supportsRefCursors", "2020", this);
            throw WSJdbcUtil.mapException(this, ex);
        } catch (NullPointerException nullX) {
            // No FFDC code needed; we might be closed.
            throw runtimeXIfNotClosed(nullX);
        }
    }

    @Override
    public long getMaxLogicalLobSize() throws SQLException {
        try {
            return mDataImpl.getMaxLogicalLobSize();
        } catch (SQLException ex) {
            FFDCFilter.processException(ex, "com.ibm.ws.rsadapter.jdbc.WSJdbc42DatabaseMetaData.getMaxLogicalLobSize", "2712", this);
            throw WSJdbcUtil.mapException(this, ex);
        } catch (NullPointerException nullX) {
            // No FFDC code needed; we might be closed.
            throw runtimeXIfNotClosed(nullX);
        }
    }
}
