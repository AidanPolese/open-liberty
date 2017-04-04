package com.ibm.ws.webcontainer.metadata;

import javax.servlet.descriptor.TaglibDescriptor;

public class TaglibDescriptorImpl implements TaglibDescriptor {

    private String location = null;
    private String uri = null;
    
    public TaglibDescriptorImpl(String loc, String u) {
        location = loc;
        uri = u;
    }
    
    @Override
    public String getTaglibLocation() {
        return location;
    }

    @Override
    public String getTaglibURI() {
        return uri;
    }

}
