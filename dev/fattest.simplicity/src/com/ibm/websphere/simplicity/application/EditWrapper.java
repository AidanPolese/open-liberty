package com.ibm.websphere.simplicity.application;

import java.util.List;

import com.ibm.websphere.simplicity.Scope;
import com.ibm.websphere.simplicity.application.tasks.ApplicationTask;

public class EditWrapper extends ApplicationOptions {

    private Application application;
    private boolean appEdit;

    public EditWrapper(Application app, List<ApplicationTask> tasks, Scope cell) throws Exception {
        super(tasks, cell);
        this.application = app;
    }

    public Application getApplication() {
        return this.application;
    }

    public boolean isFullApplicationEdit() {
        return appEdit;
    }

}
