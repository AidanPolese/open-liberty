package com.ibm.ws.webserver.plugin.utility.tasks;

import com.ibm.ws.webserver.plugin.utility.utils.ParseLoginAddress;
import com.ibm.ws.webserver.plugin.utility.utils.PluginUtilityConsole;

/**
 * 
 * 
 * @author anupag
 */
public class GeneratePluginForServer{

	String inputAddress = null;
	String targetPath = null;

	protected PluginUtilityConsole commandConsole;

	public GeneratePluginForServer(){

	}

	public GeneratePluginForServer(String input, String targetPathValue, PluginUtilityConsole commandConsole2) {
		this.inputAddress = input;	
		this.commandConsole = commandConsole2;
		this.targetPath = targetPathValue;
	}

	/**
	 * Parse <user>:<password>@<host>:<port>
	 * 
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected ParseLoginAddress parseServerAddressValue() throws IllegalArgumentException {

		ParseLoginAddress serverAddress = new ParseLoginAddress(this.inputAddress, this.commandConsole);
		// parse <user>:<password>@<host>:<port>
		serverAddress.parseLoginAddressValue("--server");
		
		return serverAddress;
	}
	
	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}
	
}
