package org.mule.module.fulfillmentworks.client.soap;

import java.net.URI;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseClientSoap
{
    protected transient final Logger LOG = LoggerFactory.getLogger(this.getClass());
    
    
    private String userName;
    private String password;
    private Integer connectionTimeout;
    private URL wsdl;    
    private URI overrideEndpoint;
    private boolean mbDebug = true; //Bool, is debug on or off

    public boolean getDebug() {
        return mbDebug;
    }

    public void setDebug(boolean debug) {
        this.mbDebug = debug;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }
    
    public Integer getConnectionTimeout()
    {
        return connectionTimeout;
    }
    
    public void setConnectionTimeout(Integer connectionTimeout)
    {
        this.connectionTimeout = connectionTimeout;
    }
    
    public URL getWsdl()
    {
        return wsdl;
    }
    
    public void setWsdl(URL wsdl)
    {
        this.wsdl = wsdl;
    }
    
    public URI getOverrideEndpoint()
    {
        return overrideEndpoint;
    }
    
    public void setOverrideEndpoint(URI overrideEndpoint)
    {
        this.overrideEndpoint = overrideEndpoint;
    }
}
