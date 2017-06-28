/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package com.ibm.websphere.jaxrs20.multipart;

import java.util.List;

import javax.ws.rs.core.MediaType;

/**
 * This interface abstracts the IBM API for MultipartBody operations.
 */
public interface IMultipartBody {

    /**
     * Retrieve MediaType for the IMultipartBody implementation object.
     * 
     * @return MediaType for the IMultipartBody implementation object.
     */
    public MediaType getType();

    /**
     * Retrieve all IAttachment in the IMultipartBody implementation object.
     * 
     * @return List of all IAttachment in the IMultipartBody implementation object.
     */
    public List<IAttachment> getAllAttachments();

    /**
     * Retrieve all child IAttachment in the IMultipartBody implementation object.
     * 
     * @return List of all child IAttachment in the IMultipartBody implementation object.
     */
    public List<IAttachment> getChildAttachments();

    /**
     * Retrieve the root IAttachment in the IMultipartBody implementation object.
     * 
     * @return the root IAttachment in the IMultipartBody implementation object.
     */
    public IAttachment getRootAttachment();

    /**
     * Retrieve IAttachment in the IMultipartBody implementation object according to the contentId.
     * 
     * @param content-id value in header
     * @return the IAttachment in the IMultipartBody implementation object according to the contentId.
     */
    public IAttachment getAttachment(String contentId);

}
