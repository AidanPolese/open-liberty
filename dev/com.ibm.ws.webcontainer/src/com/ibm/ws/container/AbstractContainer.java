// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ibm.ws.webcontainer.util.EmptyIterator;

@SuppressWarnings("unchecked")
public abstract class AbstractContainer implements Container 
{
   protected String name = null;
   private boolean isAlive = false;
   protected Map subElements = null;
   protected Map attributes = null;
   protected Configuration config = null;
   
   /**
    * @return boolean
    */
   public boolean isAlive() 
   {
    return this.isAlive;    
   }
   
   public void setAttribute(String name, Object value)
   {
   		if (attributes == null)
   			attributes = new HashMap();
   		attributes.put(name, value);
   }
   
   public Object getAttribute(String name)
   {
   		return attributes.get(name);
   }
   
   /**
    * @return String
    */
   public String getName() 
   {
        return this.name;    
   }
   
   public void setName(String name)
   {
   		this.name = name;
   }
   
   public synchronized void start() 
   {
       this.isAlive = true;    
   }
   
   public synchronized void stop() 
   {
       this.isAlive = false;    
   }
   
   public synchronized void destroy() 
   {
       if (!isAlive())
           return;
       
       Iterator iter = this.subContainers();
       while (iter.hasNext()) {
               Container cont = (Container) iter.next();
               cont.destroy();
               //PK37449 - start
               removeSubContainer(cont.getName());
               //PK37449 - end
       }
       stop();
   }
   
   /**
    * @param name
    * @return Container
    */
   public Container removeSubContainer(String name) 
   {
       if (subElements != null) {
               synchronized (this) {
                       return (Container) subElements.remove(name);
               }
       }
       return null;    
   }
   
   /**
    * @param name
    * @return Container
    */
   public Container getSubContainer(String name) 
   {
   		Iterator i = subContainers();
		Container container = null;
   		while (i.hasNext())
   		{
   			Container c = (Container) i.next();
			if (c.getName().equals(name))
				container = c;
						
   		}
		return container;
   }
   
   /**
    * @return java.util.Iterator
    * This iterator is thread safe, and prevents a concurrentModificationException
    */
   public Iterator subContainers() 
   {
       if (subElements != null) 
       {
           Map clonedElements = (Map) ((HashMap)subElements).clone();
               return clonedElements.values().iterator();
       }
       return EmptyIterator.getInstance();    
   }
   
   /**
    * @param config
    */
   public void initialize(Configuration config) 
   {
       this.config = config;
       name = config.getId();    
   }
   
   /**
    * @param con
    */
   public void addSubContainer(Container con) 
   {
       if (con == null || con.getName() == null)
               throw new RuntimeException("Container Name is null");
       if (subElements == null) {
               subElements = new HashMap();
       }
       synchronized (this) {
               subElements.put(con.getName(), con);
       }    
   }
   
   /**
    * @return com.ibm.ws.container.Configuration
    */
   protected Configuration getConfig() 
   {
		return config;
   }
   
   /**
    * @param config
    */
   protected void setConfig(Configuration config) 
   {
             this.config = config;    
   }
}
