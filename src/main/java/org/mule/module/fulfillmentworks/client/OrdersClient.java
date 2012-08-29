package org.mule.module.fulfillmentworks.client;

import java.util.List;

import org.mule.module.fulfillmentworks.OfferSearch;

import com.fulfillmentworks.api.GetOfferResult;
import com.fulfillmentworks.api.Offer;
import com.fulfillmentworks.api.Order;
import com.fulfillmentworks.api.Product;

public interface OrdersClient
{
    List<String> addOrders(List<Order> orders);
    int addOffer(Offer offer);
    int addProductToFW(Product product, Offer offer);
    List<GetOfferResult> getOffers(OfferSearch searchParams);
    String getOrderStatus(String id);

}
