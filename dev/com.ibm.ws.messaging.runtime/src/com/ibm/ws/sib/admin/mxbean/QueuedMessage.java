/*
* ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012,2013
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 *    Newly added to liberty release   051212
 * ============================================================================
 */
package com.ibm.ws.sib.admin.mxbean;

import java.beans.ConstructorProperties;

public class QueuedMessage {
	String id = null;
	String name = null;
	int approximateLength = 0;
	String state;
	String transactionId;
	String type;
	String systemMessageId;

	@ConstructorProperties({ "id", "name", "approximateLength", "state",
			"transactionId", "type", "systemMessageId" })
	public QueuedMessage(String id, String name, int approximateLength,
			String state, String transactionId, String type,
			String systemMesageId) {

		this.id = id;
		this.name = name;
		this.approximateLength = approximateLength;
		this.state = state;
		this.transactionId = transactionId;
		this.type = type;
		this.systemMessageId = systemMesageId;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getApproximateLength() throws Exception {
		// TODO Auto-generated method stub
		return approximateLength;
	}

	public String getState() {
		return state;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public String getType() throws Exception {
		return type;
	}

	public String getSystemMessageId() {
		return systemMessageId;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer(" SIBQUEUEDMessage :\n ");
		buffer.append("Id = " + id + " : ");
		buffer.append("State= " + state + " : ");
		buffer.append("Transaction Id= " + transactionId + " : ");
		buffer.append("System Message Id= " + systemMessageId + " : ");
		buffer.append("Type= " + type + " : ");

		return buffer.toString();
	}
}
