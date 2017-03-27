package com.ibm.websphere.simplicity.application.tasks;

public class MapResEnvRefToResEntry extends TaskEntry {

    public MapResEnvRefToResEntry(String[] data, MultiEntryApplicationTask task) {
        super(data, task);
    }

    public String getModule() {
        return super.getModule();
    }

    protected void setModule(String value) {
        super.setModule(value);
    }

    public String getEjb() {
        return super.getEjb();
    }

    protected void setEjb(String value) {
        super.setEjb(value);
    }

    public String getUri() {
        return super.getUri();
    }

    protected void setUri(String value) {
        super.setUri(value);
    }

    public String getResEnvRefBinding() {
        return super.getResEnvRefBinding();
    }

    protected void setResEnvRefBinding(String value) {
        super.setResEnvRefBinding(value);
    }

    public String getResEnvRefType() {
        return super.getResEnvRefType();
    }

    protected void setResEnvRefType(String value) {
        super.setResEnvRefType(value);
    }

    public String getJndi() throws Exception {
        return super.getJndi();
    }

    public void setJndi(String value) throws Exception {
        task.setModified();
        super.setJndi(value);
    }

}
