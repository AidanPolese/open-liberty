package com.ibm.ws.webserver.plugin.utility.tasks;

import com.ibm.ws.webserver.plugin.utility.utils.ParseLoginAddress;
import com.ibm.ws.webserver.plugin.utility.utils.PluginUtilityConsole;

/**
 * 
 * @author anupag
 */
public class GeneratePluginForCollective{

	private PluginUtilityConsole commandConsole;
	String inputAddress = null;
	
	String cluster = null;
	String targetPath = null;

	public GeneratePluginForCollective(){
	}

	/**
	 * @param collectiveValues
	 * @param clusterValue
	 * @param targetPathValue
	 * @param commandConsole2
	 */
	public GeneratePluginForCollective(String collectiveValues, String clusterValue, String targetPathValue, PluginUtilityConsole commandConsole2) {
		this.inputAddress = collectiveValues;	
		this.cluster = clusterValue;
		this.commandConsole = commandConsole2;
		this.targetPath = targetPathValue;
	}

	/**
	 * Parse <user>:<password>@<host>:<port>
	 * 
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected ParseLoginAddress parseCollectiveAddressValue() throws IllegalArgumentException {

		ParseLoginAddress controllerAddress = new ParseLoginAddress(this.inputAddress, this.commandConsole);
		// parse <user>:<password>@<host>:<port>
		controllerAddress.parseLoginAddressValue("--server");
		
		return controllerAddress;
	}

	/**
	 * @return
	 */
	public String getCluster() {
		return cluster;
	}

	/**
	 * @param cluster
	 */
	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	/**
	 * @return
	 */
	public String getTargetPath() {
		return targetPath;
	}

	/**
	 * @param targetPath
	 */
	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}


}
