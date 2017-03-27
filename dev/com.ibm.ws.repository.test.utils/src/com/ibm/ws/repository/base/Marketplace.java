package com.ibm.ws.repository.base;

import com.ibm.ws.repository.transport.model.AbstractJSON;

public class Marketplace extends AbstractJSON {

    private String name;
    private String description;
    private String client_id;
    private String _id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String id) {
        this._id = id;
    }
}
