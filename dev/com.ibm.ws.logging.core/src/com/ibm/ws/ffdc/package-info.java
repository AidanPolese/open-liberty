/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
/** 
 * This package provides facilities to write first failure data capture (FFDC) records 
 * to assist in debugging problems.  Typical use of this facility is through the 
 * static methods on the {@link com.ibm.ws.ffdc.FFDCFilter} class, to write information when an unexpected 
 * exception has been caught; for example: 
 * <pre>
 * {@code
 * catch (Exception x) {
 *     FFDCFilter.processException(x, getClass().getName(), "129", this);
 *     throw x;
 * }
 * }
 * </pre>
 * 
 * @version 1.0 
 */
@org.osgi.annotation.versioning.Version("1.0")
@TraceOptions(traceGroup = com.ibm.ws.logging.internal.NLSConstants.GROUP, messageBundle = com.ibm.ws.logging.internal.NLSConstants.LOGGING_NLS)
package com.ibm.ws.ffdc;

import com.ibm.websphere.ras.annotation.TraceOptions;

