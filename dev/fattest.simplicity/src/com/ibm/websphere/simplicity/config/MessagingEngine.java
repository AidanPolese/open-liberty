/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2013
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.simplicity.config;

import javax.xml.bind.annotation.XmlElement;

/**
 *
 */
public class MessagingEngine extends ConfigElement {

    @XmlElement(name = "queue")
    private ConfigElementList<Queue> queues;

    @XmlElement(name = "topicSpace")
    private ConfigElementList<ConfigElement> topicSpace;

    /**
     * @return the queues
     */
    public ConfigElementList<Queue> getQueues() {
        if (this.queues == null) {
            this.queues = new ConfigElementList<Queue>();
        }
        return this.queues;
    }

    /**
     * @return the topicSpace
     */
    public ConfigElementList<ConfigElement> getTopicSpace() {
        if (this.topicSpace == null) {
            this.topicSpace = new ConfigElementList<ConfigElement>();
        }
        return this.topicSpace;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.websphere.simplicity.config.ConfigElement#toString()
     */
    @Override
    public String toString() {
        String nl = System.getProperty("line.separator");
        Class clazz = this.getClass();
        StringBuilder buf = new StringBuilder(clazz.getSimpleName())
                        .append('{');
        buf.append("id=\"" + (getId() == null ? "" : getId()) + "\" ");

        if (this.queues != null)
            for (Queue queue : this.queues)
                buf.append(queue.toString()).append(nl);
        if (this.topicSpace != null)
            for (ConfigElement topic : this.topicSpace)
                buf.append(topic.toString()).append(nl);
        buf.append("}");
        return buf.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ibm.websphere.simplicity.config.ConfigElement#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        MessagingEngine clone = (MessagingEngine) super.clone();

        if (this.queues != null) {
            clone.queues = new ConfigElementList<Queue>();
            for (Queue queue : this.queues)
                clone.queues.add((Queue) queue.clone());
        }
        if (this.topicSpace != null) {
            clone.topicSpace = new ConfigElementList<ConfigElement>();
            for (ConfigElement topic : this.topicSpace)
                clone.topicSpace.add((ConfigElement) topic.clone());
        }
        return clone;
    }

}
