/*
 * 
 * 
 * ============================================================================
 * IBM Confidential OCO Source Materials
 * 
 * Copyright IBM Corp. 2012
 * 
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * 
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * 196675.1.1      040504 philip   Original
 * ============================================================================
 */
 
package com.ibm.ws.sib.admin;

/**
 * Holds an event which occurs at run-time, which can be propogated
 * to the systems operator. 
 * 
 * @see com.ibm.ws.sib.admin.RuntimeEventListener
 */
public interface RuntimeEvent
{
	/**
	 * Obtains an internatialised text message describing the event.
	 * @return
	 */
	public String getMessage();

	/**
	 * Sets an internatialised text message describing the event.
	 */
	public void setMessage(String newMessage);
  
	/**
	 * Gets the type of the message. 
	 * @return
	 */
	public String getType();
  
	/**
	 * Sets the type of the message. 
	 * <p>
	 * For example: sib.processor.mediation.StateChanged
	 * <p>
	 * Use the full package and class name of the class which generated the 
	 * event, with one extra string on the end to indicate which event it is.
	 */
	public void setType(String newType );
  
	/**
	 * Gets the user data if there is any.
	 * <p>
	 * User data is often an object supporting the properties interface. 
	 * 
	 * @return null if there is no userdata, or an object in which there is 
	 * some userdata. 
	 */
	public Object getUserData();
  
	/**
	 * Sets the user data if there is any.
	 * <p>
	 * Setting the user data twice over-writes the first setting.
	 * 
	 * @param newUserData The user data is over-written with this new user data.
	 * If null is supplied, it indicates that there is no user data.
	 */
	public void setUserData(Object newUserData);
}

