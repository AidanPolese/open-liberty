// IBM Confidential OCO Source Material
// 5724-J08, 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5733-W70 (C) COPYRIGHT International Business Machines Corp. 1997, 2009
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.

package com.ibm.websphere.command.web;

/**
 * This CacheableCommandImpl abstract class provides an implementation
 * for all CacheableCommand interface methods except those that the
 * command writer must write.
 * This class provides a runtime for command execution that interacts
 * with the CommandCache.
 * It also provides the contract between this command runtime and
 * the command writer.
 * <p>
 * CacheableCommandImpl is a super class of all CacheableCommands.
 * @ibm-api 
 */
public abstract class CacheableCommandImpl extends com.ibm.websphere.command.CacheableCommandImpl {
}
