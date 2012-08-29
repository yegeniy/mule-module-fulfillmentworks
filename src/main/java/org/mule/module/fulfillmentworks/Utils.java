package org.mule.module.fulfillmentworks;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class Utils
{
	public static XMLGregorianCalendar convert(Date d)
	{
		try
		{
			GregorianCalendar c = new GregorianCalendar();
			c.setTime(d);
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		} catch (DatatypeConfigurationException e)
		{
			throw new RuntimeException(e);
		}
	}
}
