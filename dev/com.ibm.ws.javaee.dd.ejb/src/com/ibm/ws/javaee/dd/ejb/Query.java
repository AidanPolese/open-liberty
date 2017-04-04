/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.ws.javaee.dd.ejb;

import com.ibm.ws.javaee.dd.common.Describable;

/**
 * Represents &lt;query>.
 */
public interface Query
                extends Describable
{
    /**
     * Represents an unspecified value for {@link #getResultTypeMappingValue}.
     */
    int RESULT_TYPE_MAPPING_UNSPECIFIED = -1;

    /**
     * Represents "Local" for {@link #getResultTypeMappingValue}.
     * 
     * @see org.eclipse.jst.j2ee.ejb.ReturnTypeMapping#LOCAL
     */
    int RESULT_TYPE_MAPPING_LOCAL = 0;

    /**
     * Represents "Remote" for {@link #getResultTypeMappingValue}.
     * 
     * @see org.eclipse.jst.j2ee.ejb.ReturnTypeMapping#REMOTE
     */
    int RESULT_TYPE_MAPPING_REMOTE = 1;

    /**
     * @return &lt;query-method>
     */
    QueryMethod getQueryMethod();

    /**
     * @return &lt;result-type-mapping>
     *         <ul>
     *         <li>{@link #RESULT_TYPE_MAPPING_UNSPECIFIED} if unspecified
     *         <li>{@link #RESULT_TYPE_MAPPING_LOCAL} - Local
     *         <li>{@link #RESULT_TYPE_MAPPING_REMOTE} - Remote
     *         </ul>
     */
    int getResultTypeMappingValue();

    /**
     * @return &lt;ejb-ql>
     */
    String getEjbQL();
}
