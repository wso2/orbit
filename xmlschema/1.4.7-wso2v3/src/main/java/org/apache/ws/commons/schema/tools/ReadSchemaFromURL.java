/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.ws.commons.schema.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import org.apache.ws.commons.schema.XmlSchemaCollection;

/**
 * Utility class for testing schema reading.
 */
public class ReadSchemaFromURL {
    
    // in 1.4 there is no constant for this in the JRE.
    private final static String SCHEMA_URI = "http://www.w3.org/2001/XMLSchema";

    /**
     * Read a schema from a URL, perhaps provoking errors.
     * @param args
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     * @throws XPathExpressionException 
     */
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        if (args.length != 1 && args.length != 2) {
            System.err.println("Usage: ReadSchemaFromURL URL -wsdl");
            return;
        }
        boolean wsdl = false;
        if (args.length == 2) {
            wsdl = true;
        }
        XmlSchemaCollection collection = new XmlSchemaCollection();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(args[0]);
        Element schema = document.getDocumentElement();
        if (wsdl) {
            NodeList schemas;
            XPath xpath = XPathFactory.newInstance().newXPath();
            xpath.setNamespaceContext(new NamespaceContext() {

                public String getNamespaceURI(String prefix) {
                    if ("xsd".equals(prefix)) {
                        return SCHEMA_URI;
                    }
                    return null;
                }

                public String getPrefix(String namespaceURI) {
                    if (SCHEMA_URI.equals(namespaceURI)) {
                        return "xsd";
                    }
                    return null;
                }

                public Iterator getPrefixes(String namespaceURI) {
                    List prefixes = new ArrayList();
                    prefixes.add("xsd");
                    return prefixes.iterator();
                }});
            schemas = (NodeList)xpath.evaluate("//xsd:schema", document, XPathConstants.NODESET);
            for (int x = 0; x < schemas.getLength(); x ++) {
                schema = (Element)schemas.item(x);
                collection.read(schema, args[0]);
            }
        } else {
            collection.read(document, args[0], null);
        }
        System.out.println("Success.");
    }

}
