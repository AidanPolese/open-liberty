/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * Copyright IBM Corp. 2017
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.microprofile.config.xml.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.microprofile.config.spi.Converter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ibm.ws.microprofile.config.interfaces.ConversionException;

public class ClientXMLConverter implements Converter<Client> {

    /** {@inheritDoc} */
    @Override
    public Client convert(String value) throws ConversionException {

        Client client = new Client();
        InputStream inputStream = null;

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            inputStream = new ByteArrayInputStream(value.getBytes());
            Document document = builder.parse(inputStream);

            Node root = document.getDocumentElement();
            NodeList nodes = root.getChildNodes();
            int len = nodes.getLength();
            for (int i = 0; i < len; i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element ele = (Element) node;
                    if (ele.getTagName().equals("name")) {
                        String name = ele.getTextContent();
                        client.setName(name);
                    } else if (ele.getTagName().equals("address")) {
                        Address address = parseAddress(ele);
                        client.setAddress(address);
                    }
                }
            }

        } catch (ParserConfigurationException e) {
            throw new ConversionException(e);
        } catch (SAXException e) {
            throw new ConversionException(e);
        } catch (IOException e) {
            throw new ConversionException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new ConversionException(e);
                }
            }
        }
        return client;

    }

    /**
     * @param ele
     * @return
     */
    private Address parseAddress(Element root) {
        Address address = new Address();

        NodeList nodes = root.getChildNodes();
        int len = nodes.getLength();
        for (int i = 0; i < len; i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element ele = (Element) node;
                if (ele.getTagName().equals("city")) {
                    String city = ele.getTextContent();
                    address.setCity(city);
                } else if (ele.getTagName().equals("street")) {
                    String street = ele.getTextContent();
                    address.setStreet(street);
                }
            }
        }

        return address;
    }

}
