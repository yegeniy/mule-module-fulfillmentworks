package org.mule.module.fulfillmentworks.transformers;


import com.fulfillmentworks.api.ArrayOfOfferOrdered;
import com.fulfillmentworks.api.ArrayOfOrderShipTo;
import com.fulfillmentworks.api.OfferID;
import com.fulfillmentworks.api.OfferIDHeader;
import com.fulfillmentworks.api.OfferOrdered;
import com.fulfillmentworks.api.Order;
import com.fulfillmentworks.api.OrderClassification;
import com.fulfillmentworks.api.OrderHeader;
import com.fulfillmentworks.api.OrderShipTo;
import com.fulfillmentworks.api.OrderShipToKey;
import com.fulfillmentworks.api.OrderShipping;
import com.fulfillmentworks.api.OrderedBy;
import com.fulfillmentworks.api.ShipToFlag;
import com.fulfillmentworks.api.Source;
import com.opensky.osis.model.Address;
import com.opensky.osis.model.LineItem;
import com.opensky.osis.model.SupplierOrder;

import org.apache.commons.lang.StringUtils;
import org.mule.api.annotations.ContainsTransformerMethods;
import org.mule.api.annotations.Transformer;
import org.mule.module.fulfillmentworks.Utils;

import java.util.ArrayList;
import java.util.List;


@ContainsTransformerMethods
public class OSOrderTOFWOrder 
{
    private String fulfillmentWorksId = "4db5d79e3337d09b72020000";
    private String standardFreightCode = "U11";
    private String standardFreightDescription = "Ground";

    public void setStandardFreightDescription(String standardFreightDescription) {
        this.standardFreightDescription = standardFreightDescription;
    }

    public void setStandardFreightCode(String standardFreightCode) {
        this.standardFreightCode = standardFreightCode;
    }
    
    public OSOrderTOFWOrder()
    {
        
    }
    
    public OSOrderTOFWOrder(String fulfillmentWorksId)
    {
        this.setFulfillmentWorksId(fulfillmentWorksId);
    }
    
    public String getFulfillmentWorksId()
    {
        return fulfillmentWorksId;
    }
    
    public void setFulfillmentWorksId(String fulfillmentWorksId)
    {
        this.fulfillmentWorksId = fulfillmentWorksId;
    }
    

    @Transformer
    public List<Order> transform(com.opensky.osis.model.Order order)
    {
        return this.transform(order,false,null);
    }
    
    @Transformer
    public List<Order> transform(com.opensky.osis.model.Order order, Boolean isVIP, String vipMessage)
    {
        List<Order> orders = new ArrayList<Order>();

        for (SupplierOrder supplierOrder : order.getSupplierOrders()) {
            if (!supplierOrder.getSupplierId().equals(getFulfillmentWorksId())) {
                continue;
            }

            boolean giftWrap = false;
            boolean giftMessage = false;
            Order fwOrder = new Order();

            ArrayOfOrderShipTo arrayOfOrderShipTo = new ArrayOfOrderShipTo();
            arrayOfOrderShipTo.getOrderShipTo().add(convertAddressToShipTo(order.getShippingAddress()));

            fwOrder.setOrderedBy(convertAddressToOrderedBy(order.getBillingAddress()));
            fwOrder.setShipTo(arrayOfOrderShipTo);

            OrderHeader orderHeader = new OrderHeader();
            orderHeader.setID(supplierOrder.getFulfillmentId());
            orderHeader.setEntryDate(Utils.convert(order.getCreatedAt()));
            orderHeader.setPONumber(order.getId());
            if(isVIP && StringUtils.isNotBlank(vipMessage))
            {
                orderHeader.setComments(vipMessage);
            }
            fwOrder.setHeader(orderHeader);

            OrderShipping orderShipping = new OrderShipping();

            if (supplierOrder.getShipSpeedCode().equals("expedited")) {
                orderShipping.setFreightCode("U01");
                orderShipping.setFreightCodeDescription("Next Day Air");
            } else {
                orderShipping.setFreightCode(standardFreightCode);
                orderShipping.setFreightCodeDescription(standardFreightDescription);
            }

            fwOrder.setShipping(orderShipping);

            ArrayOfOfferOrdered arrayOfOfferOrdered = new ArrayOfOfferOrdered();
            fwOrder.setOffers(arrayOfOfferOrdered);

            for (LineItem lineItem : supplierOrder.getLineItems()) {
                OfferIDHeader offerIDHeader = new OfferIDHeader();
                offerIDHeader.setID(lineItem.getProductSku());

                OfferID offerID = new OfferID();
                offerID.setHeader(offerIDHeader);

                OrderShipToKey orderShipToKey = new OrderShipToKey();
                orderShipToKey.setKey("1");

                OfferOrdered offerOrdered = new OfferOrdered();
                offerOrdered.setOffer(offerID);
                offerOrdered.setQuantity(lineItem.getQuantity());
                offerOrdered.setUnitPrice(lineItem.getPrice());
                offerOrdered.setOrderShipToKey(orderShipToKey);


                String giftLine = null;
                if (lineItem.getGiftMessage() != null && !lineItem.getGiftMessage().equals("")) {
                    giftLine = lineItem.getGiftMessage();
                    giftMessage = true;
                }

                if (lineItem.isGiftWrap()) {
                    giftLine = (giftLine != null) ? giftLine + "|Gift Wrap" : "Gift Wrap";
                    giftWrap = true;
                }

                if (giftLine != null) {
                    offerOrdered.setComments(giftLine);
                }

                arrayOfOfferOrdered.getOfferOrdered().add(offerOrdered);
            }

            

            String orderCodes = "";
            if(isVIP)
            {
                orderCodes += "VIP";
            }
            if (giftMessage) {
                orderCodes += "GM";
            }

            if (giftWrap) {
                orderCodes += "GW";
            }

            if (StringUtils.isNotBlank(orderCodes)) {
                Source source = new Source();
                source.setDescription(orderCodes);

                OrderClassification classification = new OrderClassification();
                classification.setSource(source);
                fwOrder.setClassification(classification);
            }

            orders.add(fwOrder);
        }

        return orders;
    }
    
    public OrderShipTo convertAddressToShipTo(Address a)
    {
        OrderShipTo orderShipTo = new OrderShipTo();
        orderShipTo.setAddress1(a.getAddress1());
        orderShipTo.setAddress2(a.getAddress2());
        orderShipTo.setCity(a.getCity());
        orderShipTo.setState(a.getState());
        orderShipTo.setPostalCode(a.getZip());
        orderShipTo.setCountry(a.getCountry());
        orderShipTo.setPhone(a.getPhone());
        orderShipTo.setFullName(a.getName());
        orderShipTo.setFlag(ShipToFlag.fromValue("Other"));

        //TODO, may need to set this to the user ID - not necessary right now
        //orderShipTo.setUID("1");
        orderShipTo.setKey("1");
        return orderShipTo;
    }

    public OrderedBy convertAddressToOrderedBy(Address a)
    {
        OrderedBy orderedBy = new OrderedBy();
        orderedBy.setFullName(a.getName());
        orderedBy.setAddress1(a.getAddress1());
        orderedBy.setAddress2(a.getAddress2());
        orderedBy.setCity(a.getCity());
        orderedBy.setState(a.getState());
        orderedBy.setPostalCode(a.getZip());
        orderedBy.setPhone(a.getPhone());

        //TODO, these may be necessary items - not necessary right now
        //orderedBy.setUID("1");
        //orderedBy.setTaxExempt(false);
        //orderedBy.setCommercial(false);

        return orderedBy;
    }

}
