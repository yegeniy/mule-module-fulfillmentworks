package org.mule.module.fulfillmentworks.impl;

import org.mule.module.fulfillmentworks.ProductFinder;
import org.mule.module.fulfillmentworks.client.ProductsClient;

public class FWProductFinder implements ProductFinder
{
    private ProductsClient productsClient;
    
    @Override
    public boolean exists(String sku)
    {
        return 0 <= this.productsClient.getStockInfo(sku);
    }
    
    public ProductsClient getProductsClient()
    {
        return productsClient;
    }
    
    public void setProductsClient(ProductsClient productsClient)
    {
        this.productsClient = productsClient;
    }

}
