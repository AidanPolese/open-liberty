// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.container;

public class TagLibRef
{
	private String uri;
	private String location;
	
	public TagLibRef(String uri, String location)
	{
		this.uri = uri;
		this.location = location;
	}
	
	/**
	 * Returns the location.
	 * @return String
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Returns the uri.
	 * @return String
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Sets the location.
	 * @param location The location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Sets the uri.
	 * @param uri The uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

}
