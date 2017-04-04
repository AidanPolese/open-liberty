package com.ibm.ws.app.manager.module.internal;

import java.util.List;

import com.ibm.ws.container.service.app.deploy.ContainerInfo;
import com.ibm.ws.container.service.app.deploy.ModuleInfo;

public interface ModuleClassLoaderFactory {
    public ClassLoader createModuleClassLoader(ModuleInfo moduleInfo, List<ContainerInfo> moduleClassesContainers);
}
