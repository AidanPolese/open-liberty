package com.ibm.websphere.simplicity.config;

import java.util.Arrays;
import java.util.List;

public class JMSDestination extends AdminObject {

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.websphere.simplicity.config.ConfigElement#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        JMSDestination clone = (JMSDestination) super.clone();
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(getClass().getSimpleName()).append('{');
        buf.append("id=\"" + (getId() == null ? "" : getId()) + "\" ");
        if (getJndiName() != null)
            buf.append("jndiName=\"" + getJndiName() + "\" ");

        List<?> nestedElementsList = Arrays.asList(
                        getProperties_FAT1()
                        );
        for (ConfigElementList<?> nestedElements : (List<ConfigElementList<?>>) nestedElementsList)
            if (nestedElements != null && nestedElements.size() > 0)
                for (Object o : nestedElements)
                    buf.append(", " + o);
        buf.append("}");
        return buf.toString();
    }

}
