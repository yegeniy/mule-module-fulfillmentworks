package org.mule.module.fulfillmentworks.client.soap;

import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.SOAPFaultException;

import org.mule.module.fulfillmentworks.LoggingSoapHandler;
import org.mule.module.fulfillmentworks.client.ProductsClient;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.fulfillmentworks.api.stockinfo.ArrayOfProductBalanceResult;
import com.fulfillmentworks.api.stockinfo.FulfworksWS;
import com.fulfillmentworks.api.stockinfo.FulfworksWSSoap;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sun.xml.ws.client.BindingProviderProperties;
import com.sun.xml.ws.developer.WSBindingProvider;

@ManagedResource
public class ProductsClientSoapImpl extends BaseClientSoap implements ProductsClient
{
    public static final QName QNAME = new QName("http://sma-promail/","FulfworksWS");
    
    private FulfworksWSSoap stockInfoSoap;
    
    private String systemId;
    
    private DBCollection collection;
    
    @ManagedOperation
    @Override
    public int getStockInfo(String sku)
    {
        ArrayOfProductBalanceResult resultArr = null;
        try {
            resultArr = stockSoap().productBalanceByWarehouse(sku, this.systemId);
        } catch (SOAPFaultException e)
        {
            String err = e.getLocalizedMessage();
            if(!err.contentEquals("Server was unable to process request. ---> Invalid Product ID"))
            {
                throw e;
            }
            return -1;
        }

        return resultArr.getProductBalanceResult().get(0).getAvailable();
    }
    
    protected FulfworksWSSoap stockSoap()
    {
        if(null != this.stockInfoSoap)
        {
            return this.stockInfoSoap;
        }

        FulfworksWS stockInfoService = new FulfworksWS(getWsdl(), QNAME);

        FulfworksWSSoap productSoap = stockInfoService.getFulfworksWSSoap();

        com.fulfillmentworks.api.stockinfo.AuthenticationHeader authHeader = new com.fulfillmentworks.api.stockinfo.AuthenticationHeader();
        authHeader.setPassword(this.getPassword());
        authHeader.setUsername(this.getUserName());


        com.fulfillmentworks.api.stockinfo.ObjectFactory objFact = new com.fulfillmentworks.api.stockinfo.ObjectFactory();
        
        if(null != getConnectionTimeout())
        {
            ((BindingProvider)productSoap).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, getConnectionTimeout());
        }
        
        ((WSBindingProvider) productSoap).setOutboundHeaders(objFact.createAuthenticationHeader(authHeader));

        List<Handler> chain = ((WSBindingProvider) productSoap).getBinding().getHandlerChain();
        chain = (null == chain)?new LinkedList<Handler>():chain;
        chain.add(new LoggingSoapHandler(this.getClass()));

        ((WSBindingProvider) productSoap).getBinding().setHandlerChain(chain);


        return productSoap;

    }

    @ManagedOperation
    @Override
    public boolean exists(String sku)
    {
        return getStockInfo(sku) > -1;
    }

    public void setMongoCollection(DBCollection collection)
    {
        this.collection = collection;
    }
    
    @ManagedAttribute
    public String getSystemId()
    {
        return systemId;
    }
    
    @ManagedAttribute
    public void setSystemId(String systemId)
    {
        this.systemId = systemId;
    }
}
