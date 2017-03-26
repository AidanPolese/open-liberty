package com.ibm.websphere.simplicity.config;

import javax.xml.bind.annotation.XmlElement;

public class JMSActivationSpec extends ActivationSpec {

    @XmlElement(name = "properties.wasJms")
    private ConfigElementList<WasJmsProperties> wasJmsProperties;

    /**
     * @return the wasJmsProperties
     */
    public ConfigElementList<WasJmsProperties> getWasJmsProperties() {
        if (wasJmsProperties == null) {
            wasJmsProperties = new ConfigElementList<WasJmsProperties>();
        }
        return wasJmsProperties;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.websphere.simplicity.config.ConfigElement#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        JMSActivationSpec clone = (JMSActivationSpec) super.clone();
        if (wasJmsProperties != null) {
            for (WasJmsProperties props : wasJmsProperties) {
                clone.getWasJmsProperties().add((WasJmsProperties) props.clone());
            }
        }
        return clone;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.websphere.simplicity.config.ActivationSpec#toString()
     */
    @Override
    public String toString() {
        String nl = System.getProperty("line.separator");
        StringBuilder buf = new StringBuilder(getClass().getSimpleName()).append('{');
        buf.append("id=\"" + (getId() == null ? "" : getId()) + "\" ");
        if (this.getAuthData() != null)
            buf.append("authDataRef=\"" + this.getAuthData() + "\" ");
        if (wasJmsProperties != null) {
            for (WasJmsProperties props : wasJmsProperties) {
                buf.append(props.toString()).append(nl);
            }
        }
        buf.append("}");
        return buf.toString();
    }
}
