// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//	298320 		08/15/05      todkap              A JSP tag that throws a subclass of JspException cause a ClassN    WASCC.web.webcontainer    
//      PK52168         10/03/07      mmolden (pmdinh)    Error Page doesn't handle subclass exception correctly.
//

package com.ibm.ws.container;


public class ErrorPage
{
	private String location;
	private String errorParam; // code or exception
	
	public ErrorPage(String location, String error)
	{
		this.location = location;
		this.errorParam = error;
	}
	/**
	 * Returns the location.
	 * @return String
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the location.
	 * @param location The location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Returns the errorParam.
	 * @return String
	 */
	public String getErrorParam() {
		return errorParam;
	}

	/**
	 * Sets the errorParam.
	 * @param errorParam The errorParam to set
	 */
	public void setErrorParam(String errorParam) {
		this.errorParam = errorParam;
	}
	
	@SuppressWarnings("unchecked")
	public Class getException()
	{
		try
		{
			return Class.forName(errorParam, true, Thread.currentThread().getContextClassLoader() ).newInstance().getClass();
		}
		catch (Exception e)
		{
			return null;
		}
	}
        
	//PK52168 - STARTS
	@SuppressWarnings("unchecked")
	public Class getException(ClassLoader warClassLoader)
	{
		try
		{
			return Class.forName(errorParam, true, warClassLoader);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	//PK52168 - ENDS
}
