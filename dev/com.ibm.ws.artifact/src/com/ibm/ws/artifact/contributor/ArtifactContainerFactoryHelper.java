/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2010,2013
 *
 * The source code for this program is not published or other-
 * wise divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 */
package com.ibm.ws.artifact.contributor;

import com.ibm.wsspi.artifact.factory.contributor.ArtifactContainerFactoryContributor;

/**
 * This is the old "internal" interface, retained for back compatibility.
 * <p>
 * As part of promoting this internal interface to be accessible as spi, it
 * had to be repackaged, and is now the ArtifactContainerFactoryContributor interface.
 * which this interface extends, allowing both to be accepted.
 */
public interface ArtifactContainerFactoryHelper extends ArtifactContainerFactoryContributor {}
