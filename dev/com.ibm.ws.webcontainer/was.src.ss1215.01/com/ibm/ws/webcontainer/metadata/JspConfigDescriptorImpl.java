package com.ibm.ws.webcontainer.metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.descriptor.JspPropertyGroupDescriptor;
import javax.servlet.descriptor.TaglibDescriptor;

import com.ibm.wsspi.webcontainer.metadata.BaseJspComponentMetaData;
import com.ibm.wsspi.webcontainer.servlet.IServletContext;
import com.ibm.wsspi.webcontainer.webapp.WebAppConfig;

public class JspConfigDescriptorImpl implements JspConfigDescriptor {

    //List<JspPropertyGroupDescriptor> jspPropertyGroups = null;
    //HashSet<TaglibDescriptor> tagLibMap = null;
    List<JspPropertyGroupDescriptor> jspPropertyGroups = null;
    HashMap tagLibMap = null;
    HashSet<TaglibDescriptor> convertedTagLibMap = null;
    BaseJspComponentMetaData jspMetadata = null;
    
    public JspConfigDescriptorImpl(IServletContext webApp) {
        WebAppConfig webGroupCfg=webApp.getWebAppConfig();
        jspMetadata = (BaseJspComponentMetaData) webGroupCfg.getMetaData().getJspComponentMetadata();
        jspPropertyGroups=jspMetadata.getJspPropertyGroups();
        tagLibMap=(HashMap) jspMetadata.getJspTaglibs();
        convertedTagLibMap = new HashSet<TaglibDescriptor>();
    }
    
    @Override
    public Collection<JspPropertyGroupDescriptor> getJspPropertyGroups() {
        return jspPropertyGroups;
    }

    @Override
    public Collection<TaglibDescriptor> getTaglibs() {
        if (convertedTagLibMap.isEmpty() && tagLibMap != null) {
            //do the conversion
            synchronized(tagLibMap) {
                if (!convertedTagLibMap.isEmpty()) return convertedTagLibMap;
                for (Iterator<Map.Entry> it = tagLibMap.entrySet().iterator(); it.hasNext();) {
                    Map.Entry e = it.next();
                    String taglibLocation = (String)e.getKey();
                    String taglibURI = (String)e.getValue();
                    convertedTagLibMap.add(new TaglibDescriptorImpl(taglibLocation, taglibURI));
                }
            }
        }
        return convertedTagLibMap;
    }

}
