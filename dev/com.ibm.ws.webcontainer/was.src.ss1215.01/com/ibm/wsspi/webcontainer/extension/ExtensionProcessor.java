// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

package com.ibm.wsspi.webcontainer.extension;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ibm.wsspi.webcontainer.RequestProcessor;
import com.ibm.wsspi.webcontainer.metadata.WebComponentMetaData;
import com.ibm.wsspi.webcontainer.servlet.IServletWrapper;

/**
 * 
 * ExtensionProcessor classes are responsible for handling requests that filter down to 
 * them by the URL matching process. They could leverage the <b>IServletContext</b>
 * instance that becomes available when the ExtensionProcessor is created, for 
 * advanced functionality. 
 * @ibm-private-in-use
 */
public interface ExtensionProcessor extends RequestProcessor
{
	/**
	 *
	 * The list of patterns that this ExtensionProcessor wants to be associated
	 * with <b>in addition</b> to the patterns specified by the WebExtensionFactory
	 * that created this ExtensionProcessor.
	 * 
	 * @return patternList
	 */
    @SuppressWarnings("unchecked")
	public List getPatternList();

	public IServletWrapper getServletWrapper(ServletRequest req, ServletResponse resp) throws Exception;

	public WebComponentMetaData getMetaData();
}
