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
package com.ibm.ws.request.probe;

public class RequestId {

	long sequenceNumber;
	String id;
	
	public RequestId(long sequenceNumber, String id) {
		this.sequenceNumber = sequenceNumber;
		this.id = id;
	}
	public long getSequenceNumber() {
		return sequenceNumber;
	}

	public String getId() {
		return id;
	}

	/**
	 * Override default behavior of Object.toString() method to return the
	 * getRequestContext.getId() Whenever the toString() is called from
	 * RequestContext Object.
	 */
	@Override
	public String toString() {
		return this.getId();
	}
	
}
