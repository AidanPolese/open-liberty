package com.ibm.websphere.simplicity.application.tasks;

import com.ibm.websphere.simplicity.application.AppConstants;

public class MapMessageDestinationRefToEJBTask extends MultiEntryApplicationTask {

    public MapMessageDestinationRefToEJBTask() {

    }

    public MapMessageDestinationRefToEJBTask(String[][] taskData) {
        super(AppConstants.MapMessageDestinationRefToEJBTask, taskData);
        for (int i = 1; i < taskData.length; i++) {
            String[] data = taskData[i];
            this.entries.add(new MapMessageDestinationRefToEJBEntry(data, this));
        }
    }

    public MapMessageDestinationRefToEJBTask(String[] columns) {
        super(AppConstants.MapMessageDestinationRefToEJBTask, columns);
    }

    @Override
    public MapMessageDestinationRefToEJBEntry get(int i) {
        if (i >= size())
            throw new ArrayIndexOutOfBoundsException(i);
        return (MapMessageDestinationRefToEJBEntry) entries.get(i);
    }

}
