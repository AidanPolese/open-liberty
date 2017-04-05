package com.ibm.wsspi.collective.repository;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

import com.ibm.websphere.collective.repository.CollectiveRepositoryMBean;

/**
 * RepositoryMember defines the client interface to the Collective Repository.
 * 
 * @see CollectiveRepositoryMBean
 * @ibm-spi
 */
public interface RepositoryClient {

    /**
     * @see CollectiveRepositoryMBean#create(String, Object)
     */
    boolean create(String nodeName, Object data)
                    throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * @see CollectiveRepositoryMBean#delete(String)
     */
    boolean delete(String nodeName)
                    throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * @see CollectiveRepositoryMBean#exists(String)
     */
    boolean exists(String nodeName)
                    throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * @see CollectiveRepositoryMBean#getData(String)
     */
    Object getData(String nodeName)
                    throws IOException, IllegalArgumentException, NoSuchElementException;

    /**
     * @see CollectiveRepositoryMBean#getDescendantData(String)
     */
    Map<String, Object> getDescendantData(String nodeName)
                    throws IOException, IllegalArgumentException, IllegalStateException, NoSuchElementException;

    /**
     * @see CollectiveRepositoryMBean#setData(String, Object)
     */
    boolean setData(String nodeName, Object data)
                    throws IOException, IllegalArgumentException, IllegalStateException;

    /**
     * @see CollectiveRepositoryMBean#getChildren(String, boolean)
     */
    Collection<String> getChildren(String nodeName, boolean absolutePath)
                    throws IOException, IllegalArgumentException, IllegalStateException;

}
