package com.ibm.wsspi.webcontainer;

import com.ibm.ws.container.DeployedModule;
import com.ibm.ws.webcontainer.exception.WebAppNotLoadedException;

public interface IWebContainer {
	public void addWebApplication(DeployedModule deployedModule, boolean production) throws WebAppNotLoadedException ;
}
