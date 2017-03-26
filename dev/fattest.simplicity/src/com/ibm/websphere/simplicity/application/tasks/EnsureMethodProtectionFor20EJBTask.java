package com.ibm.websphere.simplicity.application.tasks;

import com.ibm.websphere.simplicity.application.AppConstants;
import com.ibm.websphere.simplicity.application.AssetModule;
import com.ibm.websphere.simplicity.application.types.MethodProtectionType;
import com.ibm.websphere.simplicity.exception.TaskEntryNotFoundException;

public class EnsureMethodProtectionFor20EJBTask extends MultiEntryApplicationTask {

    public EnsureMethodProtectionFor20EJBTask() {

    }

    public EnsureMethodProtectionFor20EJBTask(String[][] taskData) {
        super(AppConstants.EnsureMethodProtectionFor20EJBTask, taskData);
        for (int i = 1; i < taskData.length; i++) {
            String[] data = taskData[i];
            this.entries.add(new EnsureMethodProtectionFor20EJBEntry(data, this));
        }
    }

    public EnsureMethodProtectionFor20EJBTask(String[] columns) {
        super(AppConstants.EnsureMethodProtectionFor20EJBTask, columns);
    }

    @Override
    public EnsureMethodProtectionFor20EJBEntry get(int i) {
        if (i >= size())
            throw new ArrayIndexOutOfBoundsException(i);
        return (EnsureMethodProtectionFor20EJBEntry) entries.get(i);
    }

    public boolean hasModule(AssetModule module) {
        return getEntry(AppConstants.APPDEPL_URI, module.getURI()) != null;
    }

    public MethodProtectionType getMethodProtectionType(AssetModule module) {
        EnsureMethodProtectionFor20EJBEntry entry = (EnsureMethodProtectionFor20EJBEntry) getEntry(AppConstants.APPDEPL_URI, module.getURI());
        return MethodProtectionType.fromValue(entry.getProtectionType());
    }

    public void setMethodProtectionType(AssetModule module, MethodProtectionType type) throws Exception {
        if (!hasModule(module))
            throw new TaskEntryNotFoundException();
        modified = true;
        EnsureMethodProtectionFor20EJBEntry entry = (EnsureMethodProtectionFor20EJBEntry) getEntry(AppConstants.APPDEPL_URI, module.getURI());
        entry.setProtectionType(type.getValue());
    }

}
