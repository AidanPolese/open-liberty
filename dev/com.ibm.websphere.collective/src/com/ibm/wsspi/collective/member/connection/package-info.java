/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2011
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */

/**
 * This package contains all of the public SPIs (exposed as OSGi services)
 * which allow local programmatic access to the Collective Repository.
 * 
 * Access to the SPIs can be obtained from OSGi service lookup or injection
 * of the RepositoryConnectionFactory class. Access is also supported by
 * static methods on RepositoryConnectionFactory. Access via OSGi service
 * injection is recommended, as access through the static methods will
 * require the caller to periodically check if the services have been resolved.
 * 
 * @version 1.1
 */
@org.osgi.annotation.versioning.Version("1.1")
@TraceOptions(traceGroup = "Collective", messageBundle = "com.ibm.wsspi.collective.internal.resources.CollectiveSPIMessages")
package com.ibm.wsspi.collective.member.connection;

import com.ibm.websphere.ras.annotation.TraceOptions;

