//IBM Confidential OCO Source Material
//5724-I63, 5724-H88, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2005
//The source code for this program is not published or otherwise divested
//of its trade secrets, irrespective of what has been deposited with the
//U.S. Copyright Office.
//
//
//CHANGE HISTORY
//Flag    Defect         Date         Modified By         Description
//--------------------------------------------------------------------------------------
//      SECPYX          01/30/06        mmolden             security changes/collaborator refactoring  

package com.ibm.ws.webcontainer.collaborator;

import com.ibm.ejs.j2c.HandleList;
import com.ibm.websphere.csi.CSIException;
import com.ibm.wsspi.webcontainer.collaborator.IConnectionCollaborator;

public class ConnectionCollaborator implements IConnectionCollaborator{
	
	/* (non-Javadoc)
	 * @see com.ibm.wsspi.webcontainer.collaborator.IConnectionCollaboratorHelper#preInvoke(boolean)
	 */
	public void preInvoke (HandleList hl,boolean isSingleThreadModel) throws CSIException{
		
	}
	/* (non-Javadoc)
	 * @see com.ibm.wsspi.webcontainer.collaborator.IConnectionCollaboratorHelper#postInvoke(boolean)
	 */
	public void postInvoke (HandleList hl,boolean isSingleThreadModel) throws CSIException{
		
	}
}
