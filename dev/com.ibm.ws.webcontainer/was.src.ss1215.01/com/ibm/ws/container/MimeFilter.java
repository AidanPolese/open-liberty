// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.container;

public class MimeFilter
{
	String type;
	String target;
	
	public MimeFilter(String type, String target)
	{
		this.type = type;
		this.target = target;
	}
	/**
	 * Returns the target.
	 * @return String
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * Returns the type.
	 * @return String
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the target.
	 * @param target The target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * Sets the type.
	 * @param type The type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
