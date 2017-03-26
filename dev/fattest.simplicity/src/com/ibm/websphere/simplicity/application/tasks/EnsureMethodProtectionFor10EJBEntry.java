package com.ibm.websphere.simplicity.application.tasks;

import com.ibm.websphere.simplicity.application.AppConstants;

public class EnsureMethodProtectionFor10EJBEntry extends TaskEntry {

    public EnsureMethodProtectionFor10EJBEntry(String[] data, MultiEntryApplicationTask task) {
        super(data, task);
    }

    public String getEjbModule() {
        return super.getEjbModule();
    }

    protected void setEjbModule(String value) {
        super.setEjbModule(value);
    }

    public String getUri() {
        return super.getUri();
    }

    protected void setUri(String value) {
        super.setUri(value);
    }

    public boolean getDenyAll() {
        return getBoolean(AppConstants.APPDEPL_METHOD_DENYALL_ACCESS_PERMISSION);
    }

    public void setDenyAll(boolean value) {
        task.setModified();
        setBoolean(AppConstants.APPDEPL_METHOD_DENYALL_ACCESS_PERMISSION, value);
    }

}
