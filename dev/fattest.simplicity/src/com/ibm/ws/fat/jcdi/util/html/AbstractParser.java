package com.ibm.ws.fat.jcdi.util.html;

import java.nio.CharBuffer;
import java.util.List;

/**
 * This interface represent an abstract parser.
 * 
 * @author yingwang
 *
 */
public interface AbstractParser {

	/**
	 * reset the event handler.
	 * 
	 * @param handler
	 */
	public void reset(Object handler);
	
	/**
	 * Parse and rewrite an array of input CharBuffer and output the result into a list of
	 * CharBuffer.
	 * 
	 * @param inputBuffers input buffers.
	 * @param outputBuffers output buffers.
	 * @throws Exception the exception.
	 */
	public void parse(CharBuffer inputBuffers[], List<CharBuffer> outputBuffers) throws Exception ;
	
}
