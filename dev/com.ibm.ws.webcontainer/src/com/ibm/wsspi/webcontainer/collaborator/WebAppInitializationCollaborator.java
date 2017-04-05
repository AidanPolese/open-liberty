// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//

package com.ibm.wsspi.webcontainer.collaborator;

import com.ibm.ws.webcontainer.spiadapter.collaborator.IInitializationCollaborator;
import com.ibm.wsspi.adaptable.module.Container;

public interface WebAppInitializationCollaborator extends IInitializationCollaborator 
{
	public void starting(Container moduleContainer);
	public void started(Container moduleContainer);
	public void stopping(Container moduleContainer);
	public void stopped(Container moduleContainer);
}