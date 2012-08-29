package org.mule.module.fulfillmentworks.transformers;


import com.fulfillmentworks.api.ArrayOfOfferComponent;
import com.fulfillmentworks.api.Offer;
import com.fulfillmentworks.api.OfferComponent;
import com.opensky.osis.model.product.BundleComponent;
import com.opensky.osis.model.product.BundleProduct;
import org.mule.api.annotations.ContainsTransformerMethods;
import org.mule.api.annotations.Transformer;

@ContainsTransformerMethods
public class OSBundleProductToFWOffer
{

    ProductTransformerHelper productTransformerHelper;

    public ProductTransformerHelper getProductTransformerHelper() {
        return productTransformerHelper;
    }

    public void setProductTransformerHelper(ProductTransformerHelper productTransformerHelper) {
        this.productTransformerHelper = productTransformerHelper;
    }

    private Offer createOffer(BundleProduct product)
    {
        ArrayOfOfferComponent components = new ArrayOfOfferComponent();
        Offer offer = productTransformerHelper.getTemplateBundleOffer();

        //Remove the template components
        offer.getComponents().getOfferComponent().clear();

        for (BundleComponent component : product.getComponents() )
        {
            OfferComponent offerComponent = productTransformerHelper.generateTemplateOfferComponent();
            offerComponent.setQuantity(component.getQuantity());
            offerComponent.getProduct().getHeader().setPartNumber(component.getStockItem().getSku());
            components.getOfferComponent().add(offerComponent);
        }

        offer.setComponents(components);
        offer.getHeader().setID(product.getStockItem().getSku());
        offer.getHeader().setDescription(product.getName());

        return offer;
    }

    @Transformer
    public Offer transform(BundleProduct osProduct )
    {
        Offer offer = this.createOffer(osProduct);

        return offer;
    }
}
