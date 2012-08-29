package org.mule.module.fulfillmentworks.transformers;

import com.fulfillmentworks.api.OfferOrdered;
import com.fulfillmentworks.api.Order;
import com.fulfillmentworks.api.OrderShipTo;
import com.fulfillmentworks.api.OrderShipping;
import com.fulfillmentworks.api.OrderedBy;
import com.opensky.osis.model.SupplierOrder;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.milyn.Smooks;
import org.milyn.payload.JavaResult;

import javax.xml.transform.stream.StreamSource;
import java.util.List;

import static junit.framework.Assert.*;

public class OSOrderTOFWOrderTest
{
    com.opensky.osis.model.Order oskyOrder;
    
    @Before
    public void setup() throws Exception
    {
        Smooks smooks = new Smooks(this.getClass().getResourceAsStream("/config/smooks/order/smooks-config.xml"));
        JavaResult result = new JavaResult();
        smooks.filterSource(new StreamSource(this.getClass().getResourceAsStream("/samples/order.json")), result);
        
        oskyOrder = result.getBean(com.opensky.osis.model.Order.class);
    }

    @Test
    public void testTransform() throws Exception
    {
        List<Order> orders = new OSOrderTOFWOrder("4db5d79e3337d09b72020000").transform(oskyOrder);
        assertNotNull(orders);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.INDENT_OUTPUT, true);
        String out = mapper.writeValueAsString(orders);
        System.out.println(out);

        assertEquals(2, orders.size());

        // Check the first supplier order got turned into the first order properly
        Order first = orders.get(0);
        assertEquals("1305139935-1", first.getHeader().getID());
        assertEquals("1305139935", first.getHeader().getPONumber());
        assertEquals(oskyOrder.getCreatedAt(), first.getHeader().getEntryDate().toGregorianCalendar().getTime());

        OrderedBy orderedBy = first.getOrderedBy();
        assertEquals("becky case", orderedBy.getFullName());
        assertEquals("18 w 18th street", orderedBy.getAddress1());
        assertEquals("Floor 9 - Suite OpenSky", orderedBy.getAddress2());
        assertEquals("New York", orderedBy.getCity());
        assertEquals("NY", orderedBy.getState());
        assertEquals("10011", orderedBy.getPostalCode());

        OrderShipTo shipTo = first.getShipTo().getOrderShipTo().get(0);
        assertEquals("Becky Case", shipTo.getFullName());
        assertEquals("140 7th Ave", shipTo.getAddress1());
        assertEquals("Apt 5R", shipTo.getAddress2());
        assertEquals("New York", shipTo.getCity());
        assertEquals("NY", shipTo.getState());
        assertEquals("10011", shipTo.getPostalCode());

        OrderShipping orderShipping = first.getShipping();
        assertEquals("U01", orderShipping.getFreightCode());
        assertEquals("Next Day Air", orderShipping.getFreightCodeDescription());

        OfferOrdered firstItem = first.getOffers().getOfferOrdered().get(0);
        assertEquals("test-sku", firstItem.getOffer().getHeader().getID());
        assertEquals(new Integer(2), firstItem.getQuantity());
        assertEquals(40.00, firstItem.getUnitPrice().doubleValue());

        OfferOrdered secondItem = first.getOffers().getOfferOrdered().get(1);
        assertEquals("test-sku-2", secondItem.getOffer().getHeader().getID());
        assertEquals(new Integer(2), secondItem.getQuantity());
        assertEquals(4.00, secondItem.getUnitPrice().doubleValue());

        // The third supplier order should be the second order
        Order second = orders.get(1);
        assertEquals("1305139935-4", second.getHeader().getID());
        assertEquals("1305139935", second.getHeader().getPONumber());
        assertEquals(oskyOrder.getCreatedAt(), second.getHeader().getEntryDate().toGregorianCalendar().getTime());

        orderedBy = second.getOrderedBy();
        assertEquals("becky case", orderedBy.getFullName());
        assertEquals("18 w 18th street", orderedBy.getAddress1());
        assertEquals("Floor 9 - Suite OpenSky", orderedBy.getAddress2());
        assertEquals("New York", orderedBy.getCity());
        assertEquals("NY", orderedBy.getState());
        assertEquals("10011", orderedBy.getPostalCode());

        shipTo = second.getShipTo().getOrderShipTo().get(0);
        assertEquals("Becky Case", shipTo.getFullName());
        assertEquals("140 7th Ave", shipTo.getAddress1());
        assertEquals("Apt 5R", shipTo.getAddress2());
        assertEquals("New York", shipTo.getCity());
        assertEquals("NY", shipTo.getState());
        assertEquals("10011", shipTo.getPostalCode());

        orderShipping = second.getShipping();
        assertEquals("U11", orderShipping.getFreightCode());
        assertEquals("Ground", orderShipping.getFreightCodeDescription());

        firstItem = second.getOffers().getOfferOrdered().get(0);
        assertEquals("test-sku-abc", firstItem.getOffer().getHeader().getID());
        assertEquals(new Integer(1), firstItem.getQuantity());
        assertEquals(25.00, firstItem.getUnitPrice().doubleValue());

        secondItem = second.getOffers().getOfferOrdered().get(1);
        assertEquals("test-sku-abc-2", secondItem.getOffer().getHeader().getID());
        assertEquals(new Integer(3), secondItem.getQuantity());
        assertEquals(4.00, secondItem.getUnitPrice().doubleValue());

        assertEquals("Test gift message!!|Gift Wrap", first.getOffers().getOfferOrdered().get(0).getComments());
        assertNull(first.getOffers().getOfferOrdered().get(1).getComments());
    }

    @Test
    public void testTransformWithoutGiftMessage() throws Exception
    {
        oskyOrder.getSupplierOrders().get(0).getLineItems().get(0).setGiftMessage("");
        List<Order> orders = new OSOrderTOFWOrder("4db5d79e3337d09b72020000").transform(oskyOrder);

        assertNotNull(orders);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.INDENT_OUTPUT, true);
        String out = mapper.writeValueAsString(orders);
        System.out.println(out);

        assertEquals(2, orders.size());

        Order order = orders.get(0);
        SupplierOrder supplierOrder = oskyOrder.getSupplierOrders().get(0);
        assertEquals("GW", order.getClassification().getSource().getDescription());
        assertEquals(supplierOrder.getLineItems().size(), order.getOffers().getOfferOrdered().size());
        assertEquals("Gift Wrap", order.getOffers().getOfferOrdered().get(0).getComments());
        assertNull(order.getOffers().getOfferOrdered().get(1).getComments());
    }

    @Test
    public void testTransformWithoutGiftWrap() throws Exception
    {
        oskyOrder.getSupplierOrders().get(0).getLineItems().get(0).setGiftWrap(false);
        List<Order> orders = new OSOrderTOFWOrder("4db5d79e3337d09b72020000").transform(oskyOrder);

        assertNotNull(orders);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.INDENT_OUTPUT, true);
        String out = mapper.writeValueAsString(orders);
        System.out.println(out);

        Order order = orders.get(0);
        SupplierOrder supplierOrder = oskyOrder.getSupplierOrders().get(0);
        assertEquals("GM", order.getClassification().getSource().getDescription());
        assertEquals(supplierOrder.getLineItems().size(), order.getOffers().getOfferOrdered().size());
        assertEquals("Test gift message!!", order.getOffers().getOfferOrdered().get(0).getComments());
        assertNull(order.getOffers().getOfferOrdered().get(1).getComments());
    }

    @Test
    public void testTransformWithoutGiftOptions() throws Exception
    {
        oskyOrder.getSupplierOrders().get(0).getLineItems().get(0).setGiftWrap(false);
        oskyOrder.getSupplierOrders().get(0).getLineItems().get(0).setGiftMessage("");
        List<Order> orders = new OSOrderTOFWOrder("4db5d79e3337d09b72020000").transform(oskyOrder);

        assertNotNull(orders);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.INDENT_OUTPUT, true);
        String out = mapper.writeValueAsString(orders);
        System.out.println(out);

        Order order = orders.get(0);
        SupplierOrder supplierOrder = oskyOrder.getSupplierOrders().get(0);
        assertNull(order.getClassification());
        assertEquals(supplierOrder.getLineItems().size(), order.getOffers().getOfferOrdered().size());
        assertNull(order.getOffers().getOfferOrdered().get(0).getComments());
        assertNull(order.getOffers().getOfferOrdered().get(1).getComments());
    }

    @Test
    public void testTransformWithStandardShipping() throws Exception
    {
        oskyOrder.getSupplierOrders().get(0).setShipSpeedCode("standard");
        List<Order> orders = new OSOrderTOFWOrder("4db5d79e3337d09b72020000").transform(oskyOrder);

        assertNotNull(orders);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.INDENT_OUTPUT, true);
        String out = mapper.writeValueAsString(orders);
        System.out.println(out);

        // Check the first supplier order got turned into the first order properly
        Order first = orders.get(0);
        assertEquals("1305139935-1", first.getHeader().getID());
        assertEquals("1305139935", first.getHeader().getPONumber());
        assertEquals(oskyOrder.getCreatedAt(), first.getHeader().getEntryDate().toGregorianCalendar().getTime());

        OrderShipping orderShipping = first.getShipping();
        assertEquals("U11", orderShipping.getFreightCode());
        assertEquals("Ground", orderShipping.getFreightCodeDescription());
    }


    @Test
    public void testTransformWithVIP() throws Exception
    {
        List<Order> orders = new OSOrderTOFWOrder("4db5d79e3337d09b72020000").transform(oskyOrder,true,"Hello");

        assertNotNull(orders);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.INDENT_OUTPUT, true);
        String out = mapper.writeValueAsString(orders);

        Order order = orders.get(0);
        
        assertEquals("Hello",order.getHeader().getComments());
        assertTrue(order.getClassification().getSource().getDescription().startsWith("VIP"));
    }


    @Test
    public void testTransformWithoutVIP() throws Exception
    {
        List<Order> orders = new OSOrderTOFWOrder("4db5d79e3337d09b72020000").transform(oskyOrder,false,"Hello");

        assertNotNull(orders);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(org.codehaus.jackson.map.SerializationConfig.Feature.INDENT_OUTPUT, true);
        String out = mapper.writeValueAsString(orders);

        Order order = orders.get(0);
        
        assertNull(order.getHeader().getComments());
        assertFalse(order.getClassification().getSource().getDescription().startsWith("VIP"));
    }
}
