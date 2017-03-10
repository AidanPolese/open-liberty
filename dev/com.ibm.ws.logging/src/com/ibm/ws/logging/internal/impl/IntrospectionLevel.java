/*
 * ============================================================================
 * @start_prolog@
 * Version: @(#) 1.1 SERV1/ws/code/ras.lite/src/com/ibm/ws/ffdc/IntrospectionLevel.java, WAS.ras.lite, WASX.SERV1, kk0826.07 06/11/06 11:21:24 [6/30/08 15:02:49]
 * ============================================================================
 * IBM Confidential OCO Source Materials
 *
 * 5724-I63, 5724-H88, 5724-H89, 5655-N02, 5724-J08  (C) Copyright IBM Corp. 2006
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 * ============================================================================
 * @end_prolog@
 *
 * Change activity:
 *
 * Reason          Date   Origin   Description
 * --------------- ------ -------- --------------------------------------------
 * SIB0048b.cli.3  061023 djvines  Lightweight FFDC
 *                 061031 vaughton Prep SERV1 version
 * ============================================================================
 */
package com.ibm.ws.logging.internal.impl;

import java.util.HashSet;
import java.util.Set;

import com.ibm.ws.ffdc.IncidentStream;

/**
 * An IntrospectionLevel contains the object for a complete level of
 * introspection (i.e. all the object referenced at a particular depth from a
 * root object)
 * 
 */
public class IntrospectionLevel {
    /**
     * How big is the string that will be generated for introspecting just this
     * level
     */
    private int _sizeOfJustThisLevel = 0;

    /**
     * How big is the string that will be generated for introspecting all levels
     * up to and including this one
     */
    private int _sizeOfAllLevelsUpToAndIncludingThisLevel = 0;

    /** What are the level members at this level */
    private final Set<IntrospectionLevelMember> _members;

    /** The cached answer to getNextLevel */
    private IntrospectionLevel _nextLevel = null;

    /** What depth is this Introspection root level */
    private final int _levelDepth;

    /**
     * Construct a new root IntrospectionLevel based on object
     * 
     * @param rootObject
     *            The root object to be introspected
     */
    public IntrospectionLevel(Object rootObject) {
        _levelDepth = 0;
        _members = new HashSet<IntrospectionLevelMember>();
        _members.add(new IntrospectionLevelMember(rootObject));
        computeSizeOfLevelMembers();
        _sizeOfAllLevelsUpToAndIncludingThisLevel = _sizeOfJustThisLevel;
    }

    /**
     * Construct a new IntrospectionLevel.
     * 
     * @param levelDepth
     *            The depth of this introspection level
     * @param parentLevel
     *            The parent level of this level (or null if this the root)
     * @param members
     *            The members of this level
     */
    private IntrospectionLevel(int levelDepth, IntrospectionLevel parentLevel, Set<IntrospectionLevelMember> members) {
        _members = members;
        _levelDepth = levelDepth;
        computeSizeOfLevelMembers();
        if (parentLevel != null)
            _sizeOfAllLevelsUpToAndIncludingThisLevel = parentLevel.getNumberOfBytesInAllLevelsIncludingThisOne();

        _sizeOfAllLevelsUpToAndIncludingThisLevel += _sizeOfJustThisLevel;
    }

    /**
     * Compute how big the string with all the level members of this level will
     * be when printed. If this level has no parent, we also need to include the
     * introductory text that this class's print method will include
     */
    private void computeSizeOfLevelMembers() {
        _sizeOfJustThisLevel = 0;

        for (IntrospectionLevelMember ilm : _members) {
            _sizeOfJustThisLevel += ilm.sizeOfIntrospection();
        }
    }

    /**
     * @return The next of the introspection tree
     */
    public IntrospectionLevel getNextLevel() {
        if (_nextLevel == null) {
            Set<IntrospectionLevelMember> _nextSet = new HashSet<IntrospectionLevelMember>();
            for (IntrospectionLevelMember ilm : _members) {
                _nextSet.addAll(ilm.getChildren());
            }
            _nextLevel = new IntrospectionLevel(_levelDepth + 1, this, _nextSet);
        }

        return _nextLevel;
    }

    /**
     * @return the number of bytes needed to introspect just this level
     */
    public int getNumberOfBytesinJustThisLevel() {
        return _sizeOfJustThisLevel;
    }

    /**
     * @return true if this introspection level has any members
     */
    public boolean hasMembers() {
        return !_members.isEmpty();
    }

    /**
     * @return the numbers needed to introspect all levels down to (and
     *         including this one)
     */
    public int getNumberOfBytesInAllLevelsIncludingThisOne() {
        return _sizeOfAllLevelsUpToAndIncludingThisLevel;
    }

    /**
     * Prints all the level members (note: the behavior is only well defined if
     * called from a root IntrospectionLevel
     * 
     * @param is
     *            The incidentStream on which to print
     * @param actualDepth
     *            The actual depth to introspect down to.
     */
    public void print(IncidentStream is, int actualDepth) {
        for (IntrospectionLevelMember ilm : _members) {
            ilm.print(is, actualDepth);
        }
    }
}
