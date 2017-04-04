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

import java.util.List;

import com.ibm.wsspi.requestContext.Event;
import com.ibm.wsspi.requestContext.RequestContext;

/**
 * <p>
 * 
 * @interface ProbeExtension : This interface allow the other component to
 *            create there own custom probe extension to probe/query the
 *            transformed method and allow us to avail various information like
 *            event detail , request context and request sampling and also
 *            augment with further operational behavior using the method like
 *            processEntry and processExit method.
 *            <p>
 *            <p>
 *            The component / feature using the request probe api need to create
 *            its own custom implementation of ProbeExtension.
 * 
 *            For Example :
 * 
 *            Class MyProbeExtension implements ProbeExtension {
 *            implement/override all the available method provided by
 *            ProbeExtension to create your probe extension implementation. }
 *            </p>
 * 
 * @version 1.0
 * 
 * @ibm-spi
 */
public interface ProbeExtension {

	/**
	 * Indicates the sample rates for the requested operation
	 * 
	 * @return int : sampleRate == 1: sample every request. sampleRate > 1:
	 *         sample 1 in every sampleRate requests.
	 */
	int getRequestSampleRate();

	/**
	 * @return boolean : Indicates whether or not this ProbeExtension's
	 *         processEvent methods should only be invoked for root events.
	 */
	boolean invokeForRootEventsOnly();

	/**
	 * @return boolean : Indicates if this ProbeExtension should be invoked at
	 *         start of events
	 */
	boolean invokeForEventEntry();

	/**
	 * 
	 * @return boolean : Ascertain if this ProbeExtension should be invoked at
	 *         end of events
	 */
	boolean invokeForEventExit();
	
	/**
	 * 
	 * @return boolean : Indicates if this ProbeExtension should be invoked for
	 *         counter events
	 */
	boolean invokeForCounter();

	/**
	 * @return List<String> : Indicates the list of eventTypes of interest to
	 *         this ProbeExtension(Null indicates all types)
	 */
	List<String> invokeForEventTypes();

	/**
	 * @return int : Indicates which events, in sampled requests, this
	 *         ProbeExtension requires context info to be populated for. 
	 *         ALL_EVENTS = context info is required for all events
	 *         EVENTS_MATCHING_SPECIFIED_EVENT_TYPES = context info is required for
	 *         events whose type matches a type returned by getEventTypes. 
	 *         NONE = context info is not required
	 */
	int getContextInfoRequirement();

	/**
	 * Performs an overridden action/operation at the start of an event
	 * 
	 * @param event
	 *            : Type of the event for which the probe extension is written.
	 * @param rc
	 *            : Context of the request for which the probe extension is
	 *            written.
	 */
	void processEntryEvent(Event event, RequestContext rc);

	/**
	 * Performs an action at the end of an event.
	 * 
	 * @param event
	 *            : Type of the event for which the probe extension is written.
	 * @param rc
	 *            : Context of the request for which the probe extension is
	 *            written.
	 */
	void processExitEvent(Event event, RequestContext rc);
	
	/**
	 * Performs an action at the end of a counter event.
	 * 
	 * @param event
	 *            : Type of the event for which the probe extension is written.
	 */
	void processCounter(Event event);

}
