package com.ibm.websphere.simplicity.application.tasks;

public class MapRunAsRolesToUsersEntry extends TaskEntry {

    public MapRunAsRolesToUsersEntry(String[] data, MultiEntryApplicationTask task) {
        super(data, task);
    }

    public String getRole() {
        return super.getRole();
    }

    protected void setRole(String value) {
        super.setRole(value);
    }

    public String getUser() {
        return super.getUser();
    }

    public void setUser(String value) {
        task.setModified();
        super.setUser(value);
    }

    public String getPassword() {
        return super.getPassword();
    }

    public void setPassword(String value) {
        task.setModified();
        super.setPassword(value);
    }

}
