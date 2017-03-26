package com.ibm.websphere.simplicity.application.tasks;

import com.ibm.websphere.simplicity.application.AppConstants;
import com.ibm.websphere.simplicity.application.types.OracleIsolationLevelType;

public class CorrectOracleIsolationLevelEntry extends TaskEntry {

    public CorrectOracleIsolationLevelEntry(String[] data, MultiEntryApplicationTask task) {
        super(data, task);
    }

    public String getModule() {
        return super.getModule();
    }

    protected void setModule(String value) {
        super.setModule(value);
    }

    public String getReferenceBinding() {
        return super.getReferenceBinding();
    }

    protected void setReferenceBinding(String value) {
        super.setReferenceBinding(value);
    }

    public String getJndi() throws Exception {
        return super.getJndi();
    }

    protected void setJndi(String value) throws Exception {
        super.setJndi(value);
    }

    public OracleIsolationLevelType getIsolationLevel() {
        return OracleIsolationLevelType.getByValue(getInteger(AppConstants.APPDEPL_ISOLATION_LEVEL, -1));
    }

    public void setIsolationLevel(OracleIsolationLevelType value) {
        task.setModified();
        setInteger(AppConstants.APPDEPL_ISOLATION_LEVEL, value.getValue());
    }

}
