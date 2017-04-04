package com.ibm.wsspi.webcontainer.collaborator;

import com.ibm.ejs.j2c.HandleList;
import com.ibm.websphere.csi.CSIException;

public interface IConnectionCollaborator {

	public void preInvoke(HandleList handleList, boolean isSingleThreadModel)
			throws  CSIException;

	public void postInvoke(HandleList handleList, boolean isSingleThreadModel)
			throws  CSIException;

}