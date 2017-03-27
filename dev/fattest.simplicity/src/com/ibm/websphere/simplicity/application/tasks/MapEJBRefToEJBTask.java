package com.ibm.websphere.simplicity.application.tasks;

import com.ibm.websphere.simplicity.application.AppConstants;

public class MapEJBRefToEJBTask extends MultiEntryApplicationTask {

    public MapEJBRefToEJBTask() {

    }

    public MapEJBRefToEJBTask(String[][] taskData) {
        super(AppConstants.MapEJBRefToEJBTask, taskData);
        for (int i = 1; i < taskData.length; i++) {
            String[] data = taskData[i];
            this.entries.add(new MapEJBRefToEJBEntry(data, this));
        }
    }

    public MapEJBRefToEJBTask(String[] columns) {
        super(AppConstants.MapEJBRefToEJBTask, columns);
    }

    @Override
    public MapEJBRefToEJBEntry get(int i) {
        if (i >= size())
            throw new ArrayIndexOutOfBoundsException(i);
        return (MapEJBRefToEJBEntry) entries.get(i);
    }

}
