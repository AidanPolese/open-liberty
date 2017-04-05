package com.ibm.wsspi.stackManager;

import java.util.Map;

/**
 * A 'StackGroup' is a representation of a group of {@link Stack},
 * that can be enhanced by configuring the server.xml to include
 * more {@link Installable} for all the {@link Stack} in this
 * StackGroup.
 * 
 * @author ricogala
 * 
 */
public interface StackGroup {

    /**
     * Gets the name for this StackGroup.
     * 
     * @return String
     */
    public abstract String getName();

    /**
     * Gets the {@link Stack} object from the StackGroup
     * 
     * @return Stack
     */
    public abstract Stack getStack(String stackName);

    /**
     * Gets the {@link Map} of all the {@link Stacks} elements for this StackGroup.
     * 
     * @return Map
     */
    public abstract Map<String, Stack> getStacks();

    /**
     * Sets the name for this StackGroup.
     * 
     */
    public abstract void setName(String name);

    /**
     * Adds a new {@link Stack} to this StackGroup.
     * 
     * @param stack
     * @return {@code true} if successful. {@code false} if the element is already part of this Stack.
     */
    public abstract void addStack(Stack stack);

    /**
     * Checks if the input {@link Stack} is already part of this StackGroup.
     * 
     * @param stackName
     * @return {@code true} if the element is in this StackGroup. {@code false} if it is not.
     */
    public abstract boolean containsStack(String stackName);

    /**
     * Removes the {@link Stack} from the StackGroup.
     * 
     * @param stackName
     */
    public abstract void removeStack(String stackName);

}