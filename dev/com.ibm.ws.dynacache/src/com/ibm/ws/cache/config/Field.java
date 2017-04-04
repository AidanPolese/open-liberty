// 1.2.1.4, 9/30/04
// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88 (C) COPYRIGHT International Business Machines Corp. 1997, 2004
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
package com.ibm.ws.cache.config;

public class Field {
    public String name;
    public Method method;
    public Field field;
    public int index = -1;

    //implementation methods
    public java.lang.reflect.Field fieldImpl;

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(name);
        if (method != null) {
            sb.append(".").append(method);
        } else if (field != null) {
            sb.append(".").append(field);
        }
        if (index != -1) {
            sb.append(".").append(index);
        }
        return sb.toString();
    }

    @Override
    public Object clone() {
        Field m = new Field();
        m.name = name;
        if (method != null)
            m.method = (Method) method.clone();
        if (field != null)
            m.field = (Field) field.clone();
        m.index = index;
        return m;
    }
}
