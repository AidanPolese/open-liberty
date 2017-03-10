/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2010, 2014
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */

package com.ibm.ejs.ras;

/**
 * {@inheritDoc}
 * 
 * <p>This class exists in support of binary compatibility with
 * the full profile &quot;Tr&quot;. Code that does not need to run in
 * the full profile should switch to {@link com.ibm.websphere.ras.Tr}.
 * 
 * @see com.ibm.websphere.ras.Tr
 * @see com.ibm.websphere.ras.TraceComponent
 * @see com.ibm.websphere.ras.annotation.TraceOptions
 */
public class TraceComponent extends com.ibm.websphere.ras.TraceComponent {

    public final int getLevel() {
        return this.specTraceLevel;
    }

    TraceComponent(Class<?> class1) {
        super(class1);
    }

    protected TraceComponent(Class<?> class1, String group, String bundle) {
        super(class1.getName(), class1, group, bundle);
    }

    protected TraceComponent(String name, Class<?> aClass, String[] groups, String bundle) {
        super(name, aClass, groups, bundle);
    }

    public void setLoggerForCallback(com.ibm.ejs.ras.TraceStateChangeListener listener) {
        this.setLoggerForCallback((com.ibm.websphere.ras.TraceStateChangeListener) listener);
    }
}
