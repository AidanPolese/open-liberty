// IBM Confidential OCO Source Material
// 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08 (C) COPYRIGHT International Business Machines Corp. 1997, 2006
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
package com.ibm.ws.webcontainer.core;

import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ibm.ws.container.AbstractContainer;
import com.ibm.ws.container.Container;
import com.ibm.wsspi.webcontainer.*;
/**
 * The base class for most active runtime containers.
 */
public class BaseContainer extends AbstractContainer implements RequestProcessor 
{
   protected ArrayList<Command> commands;
   protected RequestMapper requestMapper;
   protected Container parent;
   
   public BaseContainer(String name, Container parent) 
   {
   		this.name = name;
		this.parent = parent;
		if (parent != null)
			parent.addSubContainer(this);
		commands = new ArrayList<Command>(3);   // change this when we see a big picture 
		start();
   }
   
   /**
    * @return com.ibm.ws.core.RequestMapper
    */
   public RequestMapper getRequestMapper() 
   {
       return requestMapper;    
   }
   
   /**
    * @param mapper
    */
   public void setRequestMapper(RequestMapper mapper) 
   {
       requestMapper = mapper;    
   }
   
   /**
    * @param req
    * @param res
    */
   public void handleRequest(ServletRequest req, ServletResponse res) throws Exception
   {
   		throw new Exception("Super class implementation called...Subclass must override this method.");
   }
   
   /**
    * @param command
    */
   public void addCommand(Command command) 
   {
       this.commands.add(command);    
   }
   
   /**
    * @param command
    */
   public void removeCommand(Command command) 
   {
       commands.remove(command);    
   }
   
   /**
    * @param req
    * @param res
    */
   public void execute(Request req, Response res) 
   {
       for (int i=0; i < commands.size(); i++)
       {
           ((Command) commands.get(i)).execute(req, res);
       }    
   }
	/**
	 * Returns the parent.
	 * @return Container
	 */
	public Container getParent()
	{
		return parent;
	}
	
	/**
	 * Sets the parent.
	 * @param parent The parent to set
	 */
	public void setParent(Container parent)
	{
		this.parent = parent;
	}

        //LI3DB816
	@SuppressWarnings("unchecked")
	public Iterator getTargetMappings() {
		return requestMapper.targetMappings();
	}

	public boolean isInternal() {
		// TODO Auto-generated method stub
		return false;
	}

	

}
