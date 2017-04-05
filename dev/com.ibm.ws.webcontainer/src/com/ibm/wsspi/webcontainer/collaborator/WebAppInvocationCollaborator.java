// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

package com.ibm.wsspi.webcontainer.collaborator;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ibm.wsspi.webcontainer.metadata.WebComponentMetaData;

/**
 * LIBERTY: rather than extend multiple internal interfaces, all the methods are moved 
 * into this interface so it is now the only one needed (and the only one supported).
 * @ibm-private-in-use
 */
public interface WebAppInvocationCollaborator 
{
  public void preInvoke(WebComponentMetaData metaData);
  public void postInvoke(WebComponentMetaData metaData);
  public void preInvoke(WebComponentMetaData metaData, ServletRequest req, ServletResponse res);
  public void postInvoke(WebComponentMetaData metaData, ServletRequest req, ServletResponse res);
}

