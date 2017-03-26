package com.ibm.websphere.simplicity.application;

import java.util.List;

import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.Scope;
import com.ibm.websphere.simplicity.application.tasks.ApplicationTask;

public class InstallWrapper extends ApplicationOptions {

    private RemoteFile earFile;
    private ArchiveType originalArchiveType;

    protected InstallWrapper(RemoteFile earFile, List<ApplicationTask> tasks, Scope scope, ArchiveType archiveType) throws Exception {
        super(tasks, scope);
        this.earFile = earFile;
        this.originalArchiveType = archiveType;
    }

    public RemoteFile getEarFile() {
        return earFile;
    }

    public void setEarFile(RemoteFile earFile) {
        this.earFile = earFile;
    }

    public ArchiveType getOriginalArchiveType() {
        return this.originalArchiveType;
    }

    public boolean validate() throws Exception {
        return true;
    }

}
