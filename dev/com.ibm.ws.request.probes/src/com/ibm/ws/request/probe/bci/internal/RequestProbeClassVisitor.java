/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2014
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 * 
 * 
 * Change activity:
 *
 * Issue       Date        Name     Description
 * ----------- ----------- -------- ------------------------------------
 */
package com.ibm.ws.request.probe.bci.internal;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AnalyzerAdapter;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.request.probe.bci.RequestProbeTransformDescriptor;
import com.ibm.wsspi.request.probe.bci.RequestProbeHelper;


/**
 *
 */
public class RequestProbeClassVisitor extends ClassVisitor {

	//private final RequestProbeTransformDescriptor metaobj;
	private String classname = null;
	private static final TraceComponent tc = Tr.register(RequestProbeClassVisitor.class);
	String[] listOfMonitoredMethods = null;

	/**
	 * @param arg0
	 */
	public RequestProbeClassVisitor(ClassVisitor cv, String clsName) {
		super(Opcodes.ASM5, cv);
		this.classname = clsName;
	}

	/**
	 * Called when a class is visited. This is the method called first
	 */
	@Override
	public void visit(int version, int access, String name,
			String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		classname = name;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {

		MethodInfo mInfo = new MethodInfo(access, name, desc, signature, exceptions);
		MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
		String td = (classname+name+desc).intern();
		RequestProbeTransformDescriptor obj = RequestProbeBCIManagerImpl.getRequestProbeTransformDescriptors().get(td);
		if(obj == null) {
			td = (classname+name+"all").intern();
			obj = RequestProbeBCIManagerImpl.getRequestProbeTransformDescriptors().get(td);
		}

		if (obj != null) {
			AnalyzerAdapter aa = new AnalyzerAdapter(classname, access, name, desc, mv);
			RequestProbeMethodAdapter mvw = new RequestProbeMethodAdapter(mv, mInfo, obj.getEventType(), classname, td, aa);
			return mvw;
		} else {
			return mv;
		}
	}

}
