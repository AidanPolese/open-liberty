package com.ibm.websphere.simplicity.application.tasks;

import com.ibm.websphere.simplicity.application.AppConstants;

public class CtxRootForWebModEntry extends TaskEntry {

    public CtxRootForWebModEntry(String[] data, MultiEntryApplicationTask task) {
        super(data, task);
    }

    public String getWebModule() {
        return super.getWebModule();
    }

    protected void setWebModule(String value) {
        super.setWebModule(value);
    }

    public String getUri() {
        return super.getUri();
    }

    protected void setUri(String value) {
        super.setUri(value);
    }

    public String getContextRoot() {
        return getString(AppConstants.APPDEPL_WEB_CONTEXTROOT);
    }

    public void setContextRoot(String value) {
        task.setModified();
        setItem(AppConstants.APPDEPL_WEB_CONTEXTROOT, value);
    }

}
