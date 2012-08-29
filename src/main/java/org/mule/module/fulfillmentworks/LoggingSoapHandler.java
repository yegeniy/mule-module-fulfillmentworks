package org.mule.module.fulfillmentworks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingSoapHandler implements SOAPHandler<SOAPMessageContext>
{
	private transient final Logger LOG; 

	public LoggingSoapHandler(String logger)
	{
		this.LOG = LoggerFactory.getLogger(logger);
	}
	
	public LoggingSoapHandler(Class logger)
	{
		this.LOG = LoggerFactory.getLogger(logger);
	}
	
	public boolean handleMessage(SOAPMessageContext smc)
	{
		Boolean outboundProperty = (Boolean) smc
				.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (LOG.isDebugEnabled())
		{
			try
			{
				SOAPMessage message = smc.getMessage();
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				message.writeTo(os);
				LOG.debug("{} {}", outboundProperty.booleanValue()?"Sending":"Receiving", os.toString());
			} catch (SOAPException e)
			{
				throw new RuntimeException(e);
			} catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		return outboundProperty;

	}

	public Set<QName> getHeaders()
	{
		// throw new UnsupportedOperationException("Not supported yet.");
		return null;
	}

	public boolean handleFault(SOAPMessageContext context)
	{
		try
		{
			SOAPMessage message = context.getMessage();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			message.writeTo(os);
			LOG.error(os.toString());
		} catch (SOAPException e)
		{
			LOG.error(e.getMessage());
		} catch (IOException e)
		{
			LOG.error(e.getMessage());
		}
		return true;
	}

	public void close(MessageContext context)
	{
		// throw new UnsupportedOperationException("Not supported yet.");
	}
}
