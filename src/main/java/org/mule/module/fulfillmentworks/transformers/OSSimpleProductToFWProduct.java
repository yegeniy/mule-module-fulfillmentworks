package org.mule.module.fulfillmentworks.transformers;

import com.fulfillmentworks.api.Offer;
import com.fulfillmentworks.api.Product;
import com.opensky.osis.model.StockItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.annotations.ContainsTransformerMethods;
import org.mule.api.annotations.Transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContainsTransformerMethods
public class OSSimpleProductToFWProduct
{
    Log log = LogFactory.getLog(this.getClass());

    public OSSimpleProductToFWProduct()
    {

    }

    ProductTransformerHelper productTransformerHelper;

    public ProductTransformerHelper getProductTransformerHelper() {
        return productTransformerHelper;
    }

    public void setProductTransformerHelper(ProductTransformerHelper productTransformerHelper) {
        this.productTransformerHelper = productTransformerHelper;
    }


    private List<Map> createProducts(com.opensky.osis.model.product.SimpleProduct osProduct)
    {
        Product product = this.productTransformerHelper.getTemplateProduct();

        List<Map> productList = new ArrayList<Map>();

        for (StockItem item : osProduct.getStockItems())
        {
            if (item.getShipper().getId().equals(this.productTransformerHelper.getFWShipperID()))
            {
                log.debug(String.format("Creating product: %s %s", item.getSku(), item.getId()));

                product.getHeader().setPartNumber(item.getSku());
                product.getHeader().setDescription(osProduct.getName());

                Map<String, Object> productMap = new HashMap<String, Object>();
                productMap.put("Product", product);

                productList.add(productMap);

            }
        }
        return productList;
    }

    private Offer createOffer(Product fwProduct)
    {
        Offer offer = this.productTransformerHelper.getTemplateSimpleOffer();

        offer.getHeader().setID(fwProduct.getHeader().getPartNumber());
        offer.getHeader().setDescription(fwProduct.getHeader().getDescription());
        offer.getComponents().getOfferComponent().get(0).getProduct().getHeader().setPartNumber(fwProduct.getHeader().getPartNumber());

        return offer;
    }

    @Transformer
    public List<Map> transform(com.opensky.osis.model.product.SimpleProduct osProduct )
    {
        List<Map> productList = this.createProducts(osProduct);

        for ( Map podMap : productList)
        {
            podMap.put("Offer", this.createOffer((Product)podMap.get("Product")));
        }

        return productList;
    }
}
