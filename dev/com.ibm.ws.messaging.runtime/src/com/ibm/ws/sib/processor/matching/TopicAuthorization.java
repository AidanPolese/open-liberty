package com.ibm.ws.sib.processor.matching;

import com.ibm.ws.sib.processor.impl.MessageProcessor;
import com.ibm.ws.sib.processor.impl.interfaces.DestinationHandler;
import com.ibm.ws.sib.processor.matching.MessageProcessorMatching;
import com.ibm.ws.sib.utils.SIBUuid12;

public class TopicAuthorization {
	
	MessageProcessor _messageProcessor = null;
	
	boolean isSIBSecure = false;
	
	MessageProcessorMatching _messageProcessorMatching = null;

	public TopicAuthorization(MessageProcessor messageProcessor) {
		_messageProcessor = messageProcessor;
		_messageProcessorMatching = new MessageProcessorMatching(_messageProcessor);
	}

	public int getAclRefreshVersion() {
		
		return 0;
	}

	public void addTopicAcl(SIBUuid12 destinationUuid, String topicName, int i,
			MPPrincipal mpPrincipal) {
		// TODO Auto-generated method stub
		
	}

	public void prepareToRefresh() {
		// TODO Auto-generated method stub
		
	}

	public boolean isBusSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean checkPermissionToSubscribe(DestinationHandler topicSpace,
			String topic, String userid,
			TopicAclTraversalResults topicAclTraversalResults) {
		// TODO Auto-generated method stub
		return false;
	}
}
