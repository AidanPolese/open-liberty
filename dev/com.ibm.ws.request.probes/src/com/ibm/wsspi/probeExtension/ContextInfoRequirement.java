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
 * 
 * 
 * Change activity:
 *
 * Issue       Date        Name     Description
 * ----------- ----------- -------- ------------------------------------
 */
package com.ibm.wsspi.probeExtension;

public interface ContextInfoRequirement {

	/**
	 * Indicates which events, in sampled requests, this ProbeExtension requires
	 * context information to be populated for.
	 */

	int ALL_EVENTS = 0; // context info is required for all events

	/**
	 * Context info is required for events whose type matches a type returned by
	 * getEventTypes.
	 */
	int EVENTS_MATCHING_SPECIFIED_EVENT_TYPES = 1;

	int NONE = 2; // Context info is not required

}
