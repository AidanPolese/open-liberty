package com.ibm.websphere.simplicity.application.tasks;

public class CustomTask extends ApplicationTask {

    public CustomTask(String taskName, String[] columns) {
        super(taskName, columns);
    }

    public CustomTask(String taskName, String[][] taskData) {
        super(taskName, taskData);
    }

    public void setItem(String columnName, int row, String value) {
        super.setItem(columnName, row, value);
    }

    public String getItem(String columnName, int row) {
        modified = true;
        return super.getItem(columnName, row);
    }

}
