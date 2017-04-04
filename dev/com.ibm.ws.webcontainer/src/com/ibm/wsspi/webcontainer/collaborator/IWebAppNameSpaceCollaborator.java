package com.ibm.wsspi.webcontainer.collaborator;

public interface IWebAppNameSpaceCollaborator {

	// Added LIDB1181.2
	// LIDB1181.2.4 - modified to accept a ComponentMetaData object
	public void preInvoke(Object compMetaData);

	public void postInvoke();

}