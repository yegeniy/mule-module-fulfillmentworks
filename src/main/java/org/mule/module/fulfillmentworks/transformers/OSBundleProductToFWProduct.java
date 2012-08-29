package org.mule.module.fulfillmentworks.transformers;


import com.fulfillmentworks.api.Offer;
import com.fulfillmentworks.api.Product;
import com.opensky.osis.model.StockItem;
import com.opensky.osis.model.product.BundleComponent;
import com.opensky.osis.model.product.BundleProduct;
import org.mule.api.annotations.ContainsTransformerMethods;
import org.mule.api.annotations.Transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContainsTransformerMethods
public class OSBundleProductToFWProduct
{
    public OSBundleProductToFWProduct()
    {

    }

    private ProductTransformerHelper productTransformerHelper;

    public ProductTransformerHelper getProductTransformerHelper()
    {
        return productTransformerHelper;
    }

    public void setProductTransformerHelper(ProductTransformerHelper productTransformerHelper)
    {
        this.productTransformerHelper = productTransformerHelper;
    }

    private Product createProduct(StockItem item, String productName)
    {
        Product product = productTransformerHelper.getTemplateProduct();

        product.getHeader().setPartNumber(item.getSku());
        product.getHeader().setDescription(productName);

        return product;
    }

    private Offer createOffer(StockItem item)
    {

        Offer offer = productTransformerHelper.getTemplateSimpleOffer();

        offer.getHeader().setID(item.getSku());
        offer.getHeader().setDescription(item.getId());

        offer.getComponents().getOfferComponent().get(0).getProduct().getHeader().setPartNumber(item.getSku());
        return offer;
    }


    @Transformer
    public List<Map> transform(BundleProduct osProduct )
    {
        OSSimpleProductToFWProduct simpleTransformer = new OSSimpleProductToFWProduct();
        List<Map> itemList = new ArrayList<Map>();

        for (BundleComponent component : osProduct.getComponents())
        {
            StockItem stockItem = component.getStockItem();
            Product product = this.createProduct(stockItem, component.getProductName());
            Offer offer = this.createOffer(stockItem);

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("Product", product);
            map.put("Offer", offer);

            itemList.add(map);
        }

        return itemList;
    }



}
