//------------------------------------------------------------------------------
// %Z% %I% %W% %G% %U% [%H% %T%]
//
// COMPONENT_NAME: WAS.sca.ras
//
// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5655-N01, 5733-W61 (C) COPYRIGHT International Business Machines Corp. 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
//
// Defect/Feature  Date      CMVC ID   Description
// --------------  --------  --------- -----------------------------------------
// 410408          20070919  sykesm    Initial implementation
//------------------------------------------------------------------------------
package com.ibm.ws.ras.instrument.internal.model;

public class FieldInfo {

    private String fieldName;
    private String fieldDescriptor;
    private boolean loggerField;
    private boolean sensitive;

    public FieldInfo() {}

    public FieldInfo(String name, String descriptor) {
        this.fieldName = name;
        this.fieldDescriptor = descriptor;
    }

    public String getFieldDescriptor() {
        return fieldDescriptor;
    }

    public void setFieldDescriptor(String fieldDescriptor) {
        this.fieldDescriptor = fieldDescriptor;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public boolean isLoggerField() {
        return loggerField;
    }

    public void setLoggerField(boolean loggerField) {
        this.loggerField = loggerField;
    }

    public boolean isSensitive() {
        return sensitive;
    }

    public void setSensitive(boolean sensitive) {
        this.sensitive = sensitive;
    }

    public void updateDefaultValuesFromClassInfo(ClassInfo classInfo) {
        if (!isSensitive() && classInfo.isSensitive()) {
            setSensitive(true);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(";fieldName=").append(fieldName);
        sb.append(",fieldDescriptor=").append(fieldDescriptor);
        sb.append(",loggerField=").append(loggerField);
        sb.append(",sensitive=").append(sensitive);
        return sb.toString();
    }
}
