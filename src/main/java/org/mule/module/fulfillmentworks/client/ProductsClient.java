package org.mule.module.fulfillmentworks.client;

public interface ProductsClient
{
    int getStockInfo(String sku);
    
    boolean exists(String sku);
}
