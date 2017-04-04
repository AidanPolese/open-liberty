// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.container;

import java.util.Iterator;

/**
 * Interface that is implemented by objects that
 * act as container for other objects
 */
public interface Container 
{
	
	public Container getParent();
   
   /**
    * Check if container is alive or not
    * @return boolean
    */
   public boolean isAlive();
   
   /**
    * Get name
    * @return String
    */
   public String getName();
   
   /**
    * Start the container
    * This will start all the contained elements also.
    */
   public void start();
   
   /**
    * Stop the container. This will stop all the
    * contained elements also.
    */
   public void stop();
   
   /**
    * Destroy the container
    */
   public void destroy();
   
   /**
    * Remove sub container
    * @param name
    * @return Container
    */
   public Container removeSubContainer(String name);
   
   /**
    * Get at given sub container
    * @param name
    * @return Container
    */
   public Container getSubContainer(String name);
   
   /**
    * Get at all the sub containers
    * @return java.util.Iterator
    */
   @SuppressWarnings("unchecked")
   public Iterator subContainers();
   
   /**
    * Called to initialze the object
    * @param config
    */
   public void initialize(Configuration config);
   
   /**
    * Add sub container
    * @param con
    */
   public void addSubContainer(Container con);
}
