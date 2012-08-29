package org.mule.module.fulfillmentworks.client.soap;

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

import org.mule.module.fulfillmentworks.LoggingSoapHandler;
import org.mule.module.fulfillmentworks.OfferSearch;
import org.mule.module.fulfillmentworks.client.OrdersClient;
import org.mule.module.fulfillmentworks.transformers.OSOrderTOFWOrder;

import com.fulfillmentworks.api.AddOrderResult;
import com.fulfillmentworks.api.AddProductResult;
import com.fulfillmentworks.api.AuthenticationHeader;
import com.fulfillmentworks.api.DebugHeader;
import com.fulfillmentworks.api.GetOfferResult;
import com.fulfillmentworks.api.ObjectFactory;
import com.fulfillmentworks.api.Offer;
import com.fulfillmentworks.api.OrderInqRecord;
import com.fulfillmentworks.api.OrderSoap;
import com.fulfillmentworks.api.Order_Service;
import com.fulfillmentworks.api.Product;
import com.fulfillmentworks.api.SaveOfferResult;
import com.opensky.osis.model.Order;
import com.sun.xml.ws.client.BindingProviderProperties;
import com.sun.xml.ws.developer.WSBindingProvider;

public class OrdersClientSoapImpl extends BaseClientSoap implements OrdersClient
{
    public static final QName QNAME = new QName("http://sma-promail/","Order");

    private OrderSoap mSoapOrder;
    
    private OSOrderTOFWOrder transformer;
    
    protected OrderSoap soap()
    {
        if(null != mSoapOrder)
        {
            return this.mSoapOrder;
        }

        Order_Service orderService = new Order_Service(getWsdl(), QNAME);

        OrderSoap s = orderService.getOrderSoap();
        if (null != getOverrideEndpoint()) {
            ((javax.xml.ws.BindingProvider) s).getRequestContext().put(javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY, this.getOverrideEndpoint().toString());
        }
        
        if(null != getConnectionTimeout())
        {
            ((BindingProvider)s).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, getConnectionTimeout());
        }
        
        WSBindingProvider bp = (WSBindingProvider)s;

        AuthenticationHeader header = new AuthenticationHeader();
        header.setPassword(this.getPassword());
        header.setUsername(this.getUserName());

        DebugHeader debugHeader = new DebugHeader();
        debugHeader.setDebug(getDebug());
        ObjectFactory objFact = new ObjectFactory();

        bp.setOutboundHeaders(objFact.createAuthenticationHeader(header), objFact.createDebugHeader(debugHeader));

        List<Handler> chain = bp.getBinding().getHandlerChain();
        chain = (null == chain) ? new LinkedList<Handler>() : chain;
        chain.add(new LoggingSoapHandler(this.getClass()));

        bp.getBinding().setHandlerChain(chain);
        return s;
    }
    
    @Override
    public List<String> addOrders(List<com.fulfillmentworks.api.Order> orders)
    {
        List<String> orderIds = new LinkedList<String>();
        
        for (com.fulfillmentworks.api.Order order : orders) {
            if (order.getOffers() == null || order.getOffers().getOfferOrdered() == null && order.getOffers().getOfferOrdered().size() == 0) {
                continue;
            }

            LOG.debug(String.format("Sending Order to FW: %s", order.toString()));

            AddOrderResult result = soap().addOrder(order);

            if (result.getOrderSeqID() > 0) {
                orderIds.add(order.getHeader().getID());
            }
        }
        
        return orderIds;
    }
    
    public int addOffer(Offer offer) {
        SaveOfferResult result = soap().saveOffer(offer);
        return result.getOfferSeqID();
    }
    
    public List<GetOfferResult> getOffers(OfferSearch searchParams) {
        List<GetOfferResult> arrayOfGetOfferResult = soap().getOffers(
                searchParams.getSorting(),
                searchParams.getGroupDescription(),
                searchParams.getCategories(),
                searchParams.getMailerUID(),
                searchParams.getSearchString(),
                searchParams.getSearchID(),
                searchParams.getSearchDesc(),
                searchParams.getPriceClassDes()
        ).getGetOfferResult();

        if (arrayOfGetOfferResult.isEmpty()) {
            LOG.debug("No offers found for search parameters:", searchParams);
        }

        return arrayOfGetOfferResult;
    }
    
    public String getOrderStatus(String id) {
        String returnStatus = "Error";
        boolean recordsAreValid = true;

        OrderInqRecord orderRec = soap().getOrderInfo(id);

        if (null == orderRec.getShippingOrders()) {
            recordsAreValid = false;
        }

        if (recordsAreValid && (null == orderRec.getShippingOrders().getPickPackType())) {
            recordsAreValid = false;
        }

        if (recordsAreValid) {
            if (orderRec.getShippingOrders().getPickPackType().size() > 1) {
                LOG.warn("Returning status for only a single item but order has several items. OrderID: "
                        + orderRec.getOrdHead().getOrderId());
            }

            if (!orderRec.getShippingOrders().getPickPackType().isEmpty()) {
                returnStatus = orderRec.getShippingOrders().getPickPackType().get(0).getStatus();
            } else {
                LOG.error("No order found with ID: " + id);
            }
        } else {
            LOG.error("Null objects found in return from FW SOAP API. Searching for order # " + id);
        }

        return returnStatus;

    }
    
    @Override
    public int addProductToFW(Product product, Offer offer) {
        LOG.debug(String.format("Adding product via soap: %s", product.getHeader().getPartNumber()));

        AddProductResult result = soap().addProduct(product, offer);
        int seqId = result.getProductSeqID();


        return seqId;
    }
}
