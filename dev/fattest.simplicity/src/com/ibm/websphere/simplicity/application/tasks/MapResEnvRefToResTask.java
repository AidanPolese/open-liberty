package com.ibm.websphere.simplicity.application.tasks;

import com.ibm.websphere.simplicity.application.AppConstants;

public class MapResEnvRefToResTask extends MultiEntryApplicationTask {

    public MapResEnvRefToResTask() {

    }

    public MapResEnvRefToResTask(String[][] taskData) {
        super(AppConstants.MapResEnvRefToResTask, taskData);
        for (int i = 1; i < taskData.length; i++) {
            String[] data = taskData[i];
            this.entries.add(new MapResEnvRefToResEntry(data, this));
        }
    }

    public MapResEnvRefToResTask(String[] columns) {
        super(AppConstants.MapResEnvRefToResTask, columns);
    }

    @Override
    public MapResEnvRefToResEntry get(int i) {
        if (i >= size())
            throw new ArrayIndexOutOfBoundsException(i);
        return (MapResEnvRefToResEntry) entries.get(i);
    }

}
