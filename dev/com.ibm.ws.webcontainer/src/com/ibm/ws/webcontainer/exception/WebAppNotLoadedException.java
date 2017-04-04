// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//	  296368	08/05/05	todkap		Nested exceptions lost for problems during application startup

package com.ibm.ws.webcontainer.exception;


public class WebAppNotLoadedException extends WebContainerException
{    
    /**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3257008739499192883L;

	public WebAppNotLoadedException(String s)
    {
        super("Failed to load webapp: " + s);
    }
	
	/**
	 * @param s
	 * @param t
	 */
	public WebAppNotLoadedException(String s, Throwable th) {
        super("Failed to load webapp: " + s, th);
	}
	/**
	 * @param th
	 */
	public WebAppNotLoadedException(Throwable th) {
		super("Failed to load webapp: ", th);

	}
}
