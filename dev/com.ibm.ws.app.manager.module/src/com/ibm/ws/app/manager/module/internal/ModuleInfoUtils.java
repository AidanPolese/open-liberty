package com.ibm.ws.app.manager.module.internal;

import java.io.File;

import com.ibm.ws.javaee.dd.common.ModuleDeploymentDescriptor;

public class ModuleInfoUtils {
    public static String getModuleURIFromLocation(String location) {
        int index = location.lastIndexOf('/');
        if (File.separatorChar != '/') {
            index = Math.max(index, location.lastIndexOf(File.separatorChar));
        }

        String moduleURI = location.substring(index + 1);
        if (moduleURI.endsWith(".xml")) {
            moduleURI = moduleURI.substring(0, moduleURI.length() - 4);
        }

        return moduleURI;
    }

    public static String getModuleName(ModuleDeploymentDescriptor dd, String moduleURI) {
        String moduleName = dd == null ? null : dd.getModuleName();
        if (moduleName == null) {
            moduleName = getModuleNameFromURI(moduleURI);
        }

        return moduleName;
    }

    public static String getModuleNameFromURI(String moduleURI) {
    	String moduleName = moduleURI.substring(moduleURI.lastIndexOf('/') + 1);
        if (moduleName.endsWith(".war") || moduleName.endsWith(".rar") || moduleName.endsWith(".jar")) {
            moduleName = moduleName.substring(0, moduleName.length() - 4);
        }

        return moduleName;
    }
}
