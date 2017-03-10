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
package com.ibm.ws.config.xml.internal;

/**
 * This is a ConfigElement that corresponds to an XML element from the configuration. It has not
 * been processed with metatype information.
 */
public class SimpleElement extends ConfigElement {

    private String id;
    protected boolean usingDefaultId = false;

    /**
     * @param nodeName
     */
    public SimpleElement(String nodeName) {
        super(nodeName);
    }

    /**
     * @param configElement
     */
    public SimpleElement(ConfigElement configElement) {
        super(configElement);
        setId(configElement.getId());
    }

    void setId(String id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.config.xml.internal.ConfigElement#getId()
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * Try to figure out is this is a child element or a collection attribute value. If it has attributes
     * or child elements (isTextOnly() false) then it's definitely a child element. If it is text only
     * but has no content, it can't be a collection attribute and must be a child element.
     *
     * @return whether this is a child element (true) or a collection attribute value (false)
     */
    public boolean isChildElement() {
        return !isEmpty() ||
               !isTextOnly() ||
               "".equals(getElementValue());

    }

    /*
     * Configuration is considered empty if if has no attributes or only has "config.alias" attribute.
     */
    private boolean isEmpty() {
        return attributes.isEmpty();
    }

    /**
     * @param index
     */
    public void setDefaultId(int index) {
        if ((getId() == null) && (index > -1)) {
            setId("default-" + index);
            this.usingDefaultId = true;
        }

    }

    public boolean isUsingNonDefaultId() {
        if (getId() == null)
            return false;

        return !usingDefaultId;
    }

    @Override
    public boolean isSimple() {
        return true;
    }

}
