/*
* IBM Confidential
*
* OCO Source Materials
*
* Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested
* of its trade secrets, irrespective of what has been deposited with the
* U.S. Copyright Office.
*/
package com.ibm.jbatch.container.ws;

import java.util.Arrays;
import java.util.List;

/**
 * Constants used during search queries
 */
public class WSSearchConstants {

    // The list of valid fields to sort by.  Temporary until we can use the entity class itself to determine validity
    static public List<String> VALID_SORT_FIELDS = Arrays.asList("createTime", "lastUpdatedTime", "submitter", "amcName",
                                                                 "jobXMLName", "batchStatus", "exitStatus");

    // Valid search parameters.  Search parameters specified other than these (excepting job parameters) will end up in the X-IBM-Unrecognized-Fields response header
    static public List<String> VALID_SEARCH_PARAMS_V3 = Arrays.asList("jobInstanceId", "createTime", "instanceState", "exitStatus",
                                                                      "lastUpdatedTime", "page", "pageSize", "sort");

    // Valid search parameters.  Search parameters specified other than these (excepting job parameters) will end up in the X-IBM-Unrecognized-Fields response header
    static public List<String> VALID_SEARCH_PARAMS_V4 = Arrays.asList("jobInstanceId", "createTime", "instanceState", "exitStatus",
                                                                      "lastUpdatedTime", "page", "pageSize", "sort",
                                                                      "submitter", "appName", "jobName", "ignoreCase");
}
