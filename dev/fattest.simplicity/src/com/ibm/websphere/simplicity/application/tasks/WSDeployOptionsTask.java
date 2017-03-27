package com.ibm.websphere.simplicity.application.tasks;

import com.ibm.websphere.simplicity.application.AppConstants;

public class WSDeployOptionsTask extends ApplicationTask {

    public WSDeployOptionsTask() {

    }

    public WSDeployOptionsTask(String[] columns) {
        super(AppConstants.WSDeployOptionsTask, columns);
    }

    public WSDeployOptionsTask(String[][] data) {
        super(AppConstants.WSDeployOptionsTask, data);
    }

    public String getDeploywsClasspath() {
        return getString(AppConstants.APPDEPL_DEPLOYWS_CLASSPATH, 1);
    }

    public void setDeployWsClasspath(String value) {
        modified = true;
        setItem(AppConstants.APPDEPL_DEPLOYWS_CLASSPATH, 1, value);
    }

    public String getDeploywsJarDirs() {
        return getString(AppConstants.APPDEPL_DEPLOYWS_JARDIRS, 1);
    }

    public void setDeployWsJarDirs(String value) {
        modified = true;
        setItem(AppConstants.APPDEPL_DEPLOYWS_JARDIRS, 1, value);
    }
}
