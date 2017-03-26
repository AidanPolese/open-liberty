package com.ibm.websphere.simplicity.application.tasks;

import com.ibm.websphere.simplicity.application.AppConstants;

public class BindJndiForEJBBusinessTask extends MultiEntryApplicationTask {

    public BindJndiForEJBBusinessTask() {

    }

    public BindJndiForEJBBusinessTask(String[][] taskData) {
        super(AppConstants.BindJndiForEJBBusinessTask, taskData);
        for (int i = 1; i < taskData.length; i++) {
            String[] data = taskData[i];
            this.entries.add(new BindJndiForEJBBusinessEntry(data, this));
        }
    }

    public BindJndiForEJBBusinessTask(String[] columns) {
        super(AppConstants.BindJndiForEJBBusinessTask, columns);
    }

    @Override
    public BindJndiForEJBBusinessEntry get(int i) {
        if (i >= size())
            throw new ArrayIndexOutOfBoundsException(i);
        return (BindJndiForEJBBusinessEntry) entries.get(i);
    }

}
