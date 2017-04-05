package com.ibm.wsspi.stackManager;

/**
 * An 'DeployVariable' is a representation of a deploy variable
 * that contains a stack port name, its initial and increment value. It can be
 * used to allocate the port number based on its initial and increment value
 * during deployment of a stack.
 * 
 * @author amylin
 * 
 */
public interface DeployVariable {

    /**
     * Returns the name of this DeployVariable.
     * 
     * @return String
     */
    public abstract String getName();

    /**
     * Returns the initial value of this DeployVariable.
     * 
     * @return value
     */
    public abstract String getValue();

    /**
     * Returns the increment value for this DeployVariable.
     * 
     * @return increment value
     */
    public abstract int getIncrement();

    /**
     * Sets the name for this DeployVariable.
     * 
     * @param name
     */
    public abstract void setName(String name);

    /**
     * Sets the initial value for this DeployVariable.
     * 
     * @param value
     */
    public abstract void setValue(String value);

    /**
     * Sets the increment value for this DeployVariable.
     * 
     * @param incrementVal
     */
    public abstract void setIncrement(int incrementVal);

}