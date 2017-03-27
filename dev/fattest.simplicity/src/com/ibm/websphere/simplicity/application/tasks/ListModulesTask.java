package com.ibm.websphere.simplicity.application.tasks;

import com.ibm.websphere.simplicity.application.AppConstants;

public class ListModulesTask extends MultiEntryApplicationTask {

    public ListModulesTask() {

    }

    public ListModulesTask(String[][] taskData) {
        super(AppConstants.ListModulesTaskName, taskData);
        for (int i = 1; i < taskData.length; i++) {
            String[] data = taskData[i];
            this.entries.add(new ListModulesEntry(data, this));
        }
    }

    public ListModulesTask(String[] columns) {
        super(AppConstants.ListModulesTaskName, columns);
    }

    @Override
    public ListModulesEntry get(int i) {
        if (i >= size())
            throw new ArrayIndexOutOfBoundsException(i);
        return (ListModulesEntry) entries.get(i);
    }

}
