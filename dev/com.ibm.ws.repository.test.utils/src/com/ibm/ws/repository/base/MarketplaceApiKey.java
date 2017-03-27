package com.ibm.ws.repository.base;

import com.ibm.ws.repository.transport.model.AbstractJSON;

public class MarketplaceApiKey extends AbstractJSON {

    private String marketplaceId;
    private String key;

    public String getMarketplaceId() {
        return marketplaceId;
    }

    public void setMarketplaceId(String marketplaceId) {
        this.marketplaceId = marketplaceId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
