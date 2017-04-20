package com.ibm.websphere.command;


// IBM Confidential OCO Source Material
// 5639-D57 (C) COPYRIGHT International Business Machines Corp. 2000
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
//  FILENAME: CompensableCommand.java
//
//  DESCRIPTION:
//      
//
// Change Log:
//  Date       Pgmr    Defect   Description
//  --------   ------- ------   ---------------------------------
// 03/09/00   v2iicrp  75515    javadoc changes
//
//**********************************************************************


/**
 * The CompensableCommand interface allows you to make a command reversible.
 * When you make a command compensable, you associate a second command
 * with it. The second command compensates for the effects of the first
 * command by undoing its work as completely as possible.</p>
 * <p>
 * The CompensableCommand interface defines one method, getCompensatingCommand(),
 * which returns the compensating command associated with a command. The
 * application programmer must implement both this method and the compensating
 * command itself as part of implementing a compensable command.</p>
 * <p>
 * A client that wants to reverse a compensable command calls the compensating
 * command like this:
 * <pre>
 *       myCommand.getCompensatingCommand().performExecute();
 * </pre></p>
 * 
 * @ibm-api
 */
public interface CompensableCommand 
extends Command 
{
        /**
         * Retrieves the compensating command associated with the command.
         * Call this method only after the associated compensable command has
         * been run.</p>
         * <p>
         * The application programmer implements the getCompensatingCommand
         * method as part of writing a compensable command. For a compensating
         * command whose input properties are the output properties of the
         * original command, the following implementation is sufficient:
         * <pre>
         *      Command command = new MyCompensatingCommand();
         *      command.setInputPropertyX(outputPropertyP);
         *      return command;
         * </pre></p>
         *
         * @return The compensating command associated with the command.
         * @exception CommandException
         * The superclass for all command exceptions. Specifically,
         * UnavailableCompensatingCommandException is thrown if there is no
         * compensating command associated with the command.
	 */
	public Command
	getCompensatingCommand() throws CommandException;
}
