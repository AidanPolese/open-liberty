package com.ibm.websphere.simplicity.application.tasks;

import com.ibm.websphere.simplicity.application.AppConstants;
import com.ibm.websphere.simplicity.application.AssetModule;
import com.ibm.websphere.simplicity.exception.TaskEntryNotFoundException;

public class JSPReloadForWebModTask extends MultiEntryApplicationTask {

    public JSPReloadForWebModTask() {

    }

    public JSPReloadForWebModTask(String[][] taskData) {
        super(AppConstants.JSPReloadForWebModTask, taskData);
        for (int i = 1; i < taskData.length; i++) {
            String[] data = taskData[i];
            this.entries.add(new JSPReloadForWebModEntry(data, this));
        }
    }

    public JSPReloadForWebModTask(String[] columns) {
        super(AppConstants.JSPReloadForWebModTask, columns);
    }

    @Override
    public JSPReloadForWebModEntry get(int i) {
        if (i >= size())
            throw new ArrayIndexOutOfBoundsException(i);
        return (JSPReloadForWebModEntry) entries.get(i);
    }

    public boolean hasModule(AssetModule module) {
        return getEntry(AppConstants.APPDEPL_URI, module.getURI()) != null;
    }

    public JSPReloadForWebModEntry getReloadOptions(AssetModule module) {
        return (JSPReloadForWebModEntry) getEntry(AppConstants.APPDEPL_URI, module.getURI());
    }

    public void setReloadOptions(AssetModule module, boolean reload, int interval) throws Exception {
        if (!hasModule(module))
            throw new TaskEntryNotFoundException();
        modified = true;
        JSPReloadForWebModEntry entry = (JSPReloadForWebModEntry) getEntry(AppConstants.APPDEPL_URI, module.getURI());
        entry.setJspReload(reload);
        entry.setJspReloadInterval(interval);
    }

}
