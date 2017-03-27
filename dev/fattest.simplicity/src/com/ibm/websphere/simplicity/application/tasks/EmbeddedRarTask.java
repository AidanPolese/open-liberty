package com.ibm.websphere.simplicity.application.tasks;

import com.ibm.websphere.simplicity.application.AppConstants;

public class EmbeddedRarTask extends MultiEntryApplicationTask {

    public EmbeddedRarTask() {

    }

    public EmbeddedRarTask(String[][] taskData) {
        super(AppConstants.EmbeddedRarTask, taskData);
        for (int i = 1; i < taskData.length; i++) {
            String[] data = taskData[i];
            this.entries.add(new EmbeddedRarEntry(data, this));
        }
    }

    public EmbeddedRarTask(String[] columns) {
        super(AppConstants.EmbeddedRarTask, columns);
    }

    @Override
    public EmbeddedRarEntry get(int i) {
        if (i >= size())
            throw new ArrayIndexOutOfBoundsException(i);
        return (EmbeddedRarEntry) entries.get(i);
    }

}
