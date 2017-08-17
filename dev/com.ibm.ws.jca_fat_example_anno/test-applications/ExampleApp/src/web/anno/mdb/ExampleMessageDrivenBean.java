/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2012
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package web.anno.mdb;

import javax.ejb.MessageDriven;
import javax.resource.ResourceException;
import javax.resource.cci.MessageListener;
import javax.resource.cci.Record;

@MessageDriven
public class ExampleMessageDrivenBean implements MessageListener {
    /**
     * @see javax.resource.cci.MessageListener#onMessage(javax.resource.cci.Record)
     */
    @Override
    public Record onMessage(Record record) throws ResourceException {
        System.out.println("ExampleMessageDrivenBean.onMessage record = " + record);
        return record;
    }
}
